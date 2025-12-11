package com.example.smarthealth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-json-base64:}")
    private String firebaseServiceAccountJsonBase64;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {

            if (firebaseServiceAccountJsonBase64 == null || firebaseServiceAccountJsonBase64.isBlank()) {
                throw new IllegalStateException(
                        "Firebase service account JSON is missing! Set FIREBASE_SERVICE_ACCOUNT_JSON_BASE64 env.");
            }

            byte[] decoded = Base64.getDecoder().decode(firebaseServiceAccountJsonBase64);

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(decoded));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }
}

// package com.example.smarthealth.config;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.util.StringUtils;

// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.util.Base64;

// @Configuration
// public class FirebaseConfig {

//     // Lấy giá trị từ file .env hoặc application.yaml
//     @Value("${firebase.service-account-json-base64:}")
//     private String serviceAccountJsonBase64;

//     @Bean
//     public FirebaseApp firebaseApp() {
//         try {
//             // 1. QUAN TRỌNG: Kiểm tra nếu chuỗi Base64 rỗng thì TRẢ VỀ NULL thay vì ném lỗi
//             if (!StringUtils.hasText(serviceAccountJsonBase64)) {
//                 System.out.println("=================================================================");
//                 System.out.println("⚠️  WARNING: Không tìm thấy FIREBASE_KEY. Chế độ Auth sẽ bị tắt.");
//                 System.out.println("⚠️  App vẫn sẽ chạy bình thường để test các module khác.");
//                 System.out.println("=================================================================");
//                 return null; // Trả về null để Spring không bị crash
//             }

//             // 2. Nếu có key (sau này deploy thật) thì giải mã và khởi tạo
//             byte[] decodedBytes = Base64.getDecoder().decode(serviceAccountJsonBase64);
//             ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decodedBytes);

//             FirebaseOptions options = FirebaseOptions.builder()
//                     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                     .build();

//             if (FirebaseApp.getApps().isEmpty()) {
//                 return FirebaseApp.initializeApp(options);
//             }
//             return FirebaseApp.getInstance();

//         } catch (IOException e) {
//             System.err.println("❌ ERROR khởi tạo Firebase: " + e.getMessage());
//             return null; // Gặp lỗi cũng trả về null luôn để app sống
//         }
//     }
// }