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
import org.springframework.security.core.GrantedAuthority;
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
            String email = decodedToken.getEmail();

            if (email == null || email.isBlank()) {
                log.warn("Firebase token has no email");
                filterChain.doFilter(request, response);
                return;
            }

            // Fetch or create user
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createUserFromFirebase(
                            decodedToken.getUid(),
                            email,
                            decodedToken.getName(),
                            decodedToken.getPicture()));

            // Gán quyền
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Authenticated Firebase user: {}", email);

        } catch (com.google.firebase.auth.FirebaseAuthException e) {
            // TOKEN SAI HOẶC HẾT HẠN
            log.warn("Firebase token verification failed: {}", e.getMessage());
        } catch (Exception e) {
            // LỖI KHÁC (DB, ROLE, LOGIC)
            log.error("Unexpected error in FirebaseAuthenticationFilter", e);
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
