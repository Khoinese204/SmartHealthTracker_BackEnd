package com.example.smarthealth.config;

import com.example.smarthealth.model.auth.Role;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.repository.RoleRepository;
import com.example.smarthealth.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = header.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = (String) decodedToken.getClaims().getOrDefault("name", email);
            String picture = (String) decodedToken.getClaims().getOrDefault("picture", null);

            // Tìm user trong DB, nếu chưa có thì tạo mới
            User user = userRepository.findByFirebaseUid(firebaseUid)
                    .orElseGet(() -> createUserFromFirebase(firebaseUid, email, name, picture));

            // Tạo GrantedAuthority từ role
            String roleName = user.getRole().getName(); // USER/ADMIN
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));

            var auth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), null, authorities);

            auth.setDetails(user);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            log.warn("Invalid Firebase ID token: {}", e.getMessage());
            // Không set auth, request sẽ bị chặn ở layer security nếu cần
        }

        filterChain.doFilter(request, response);
    }

    private User createUserFromFirebase(String firebaseUid, String email, String name, String avatarUrl) {
        // Lấy role USER mặc định
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User user = User.builder()
                .firebaseUid(firebaseUid)
                .email(email)
                .fullName(name)
                .avatarUrl(avatarUrl)
                .role(userRole)
                .isActive(true)
                .authProvider("FIREBASE")
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}
