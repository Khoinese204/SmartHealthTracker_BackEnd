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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // CHỈ filter các request /api/**
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // true = KHÔNG filter
        // -> filter chỉ chạy khi path bắt đầu bằng /api/
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        // (nếu bạn vẫn muốn filter chỉ cho /api/** thì có thể giữ shouldNotFilter, khỏi
        // check lại ở đây)

        String header = request.getHeader("Authorization");

        // 1. Thiếu hoặc sai format header -> 401
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "status": 401,
                      "message": "Missing or invalid Authorization header"
                    }
                    """);
            return; // DỪNG ở đây, không chạy tiếp filter chain
        }

        String idToken = header.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();

            if (email == null || email.isBlank()) {
                // Token verify được nhưng không có email -> cũng coi như 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("""
                        {
                          "status": 401,
                          "message": "Invalid Firebase token (no email)"
                        }
                        """);
                return;
            }

            // 2. Tìm hoặc tạo user trong DB
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createUserFromFirebase(
                            decodedToken.getUid(),
                            email,
                            decodedToken.getName(),
                            decodedToken.getPicture()));

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Authenticated Firebase user: {}", email);

            // 3. OK -> cho đi tiếp
            filterChain.doFilter(request, response);

        } catch (com.google.firebase.auth.FirebaseAuthException e) {
            // Token sai / hết hạn -> 401
            log.warn("Firebase token verification failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "status": 401,
                      "message": "Invalid or expired Firebase token"
                    }
                    """);
        } catch (Exception e) {
            // Lỗi bất ngờ khác -> 500
            log.error("Unexpected error in FirebaseAuthenticationFilter", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "status": 500,
                      "message": "Internal server error"
                    }
                    """);
        }
    }

    private User createUserFromFirebase(String firebaseUid, String email, String name, String avatarUrl) {
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
