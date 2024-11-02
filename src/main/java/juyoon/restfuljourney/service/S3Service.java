package juyoon.restfuljourney.service;

import juyoon.restfuljourney.entity.File;
import juyoon.restfuljourney.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3Service.java
    public String uploadFile(MultipartFile file) throws IOException {
        // 파일 형식 검증 로직 추가 (예: 이미지 파일인지 확인)
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        // 파일 이름 생성 및 S3 업로드 로직
        String fileName = generateFileName(file.getOriginalFilename());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(file.getContentType())
                .key(fileName)
                .build();

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        // S3에서 파일 URL 생성
        String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();

        // File 엔티티에 파일 정보 저장
        File fileEntity = new File(fileName, file.getOriginalFilename(), fileUrl);
        fileRepository.save(fileEntity);

        return fileUrl;
    }


    private String generateFileName(String originalFileName) {
        String uniqueId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return uniqueId + "_" + timestamp + "_" + originalFileName;
    }
}
