package com.example.ium._core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FireBaseConfig {

    // Resource 타입으로 변경하여 클래스패스에서 리소스를 로드
    @Value("${firebase.key}")
    private Resource firebaseKeyResource;

    @PostConstruct
    public FirebaseApp firebaseApp() throws IOException {

        log.info("Firebase key path: {}", firebaseKeyResource.getFilename());

        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream fis = firebaseKeyResource.getInputStream()) { // 리소스를 InputStream 으로 읽어오기
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(fis))
                        .build();

                return FirebaseApp.initializeApp(options);
            }
        }

        return FirebaseApp.getInstance();
    }
}
