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
