package com.example.ium.member.infrastructure.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FireBaseFileService {

    @Value("${firebase.storage}")
    private String firebaseStorageUrl;

    public String uploadFile(MultipartFile multipartFile, String fileName) {

        try {
            Bucket bucket = StorageClient.getInstance().bucket(firebaseStorageUrl);
            Blob blob = bucket.create(fileName, multipartFile.getInputStream(), multipartFile.getContentType());
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            return String.format("https://storage.googleapis.com/%s/%s", firebaseStorageUrl, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
