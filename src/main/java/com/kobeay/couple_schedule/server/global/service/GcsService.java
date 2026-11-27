package com.kobeay.couple_schedule.server.global.service;

import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GcsService {
    private final Storage storage;

    @Value("${gcs.bucket-name}")
    private String bucketName;

    public String upload(MultipartFile file) throws IOException {
        String objectName = "profiles/" + UUID.randomUUID();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return objectName;
    }

    public void delete(String objectName) {
        storage.delete(BlobId.of(bucketName, objectName));
    }

    public String generateSignedUrl(String objectName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .build();

        return storage.signUrl(
                blobInfo,
                60,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature()
        ).toString();
    }
}
