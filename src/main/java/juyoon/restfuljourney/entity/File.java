package juyoon.restfuljourney.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;        // S3에 저장된 파일명
    private String originalFileName; // 사용자가 업로드한 원본 파일명

    @Lob // Large Object
    @Column(columnDefinition = "TEXT") // 명시적으로 TEXT 타입으로 설정
    private String fileUrl;          // S3 파일 URL

    private LocalDateTime uploadDate; // 파일 업로드 날짜

    public File(String fileName, String originalFileName, String fileUrl) {
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileUrl = fileUrl;
        this.uploadDate = LocalDateTime.now(); // 파일 업로드 시점 저장
    }
}
