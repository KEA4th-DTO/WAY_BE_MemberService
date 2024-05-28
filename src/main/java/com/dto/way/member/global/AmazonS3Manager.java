package com.dto.way.member.global;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dto.way.member.global.config.AmazonS3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final AmazonS3Config amazonS3Config;

    public String uploadFileToDirectory(String directoryPath, String keyName, MultipartFile file) {
        String fullKeyName = directoryPath + "/" + keyName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonS3Config.getBucket(), fullKeyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("Error at AmazonS3Manager uploadFileToDirectory: {}", (Object) e.getStackTrace());
        }
        return amazonS3.getUrl(amazonS3Config.getBucket(), fullKeyName).toString();
    }


    public void deleteFile(String fileUrl) throws IOException {
        // 예상된 S3 버킷 URL 패턴
        String s3UrlPattern = "https://way-bucket-s3.s3.ap-northeast-2.amazonaws.com/";

        // URL이 예상된 패턴으로 시작하는지 확인
        if (!fileUrl.startsWith(s3UrlPattern)) {
            throw new IllegalArgumentException("Invalid S3 URL: " + fileUrl);
        }

        // 파일 키 추출
        String fileKey = fileUrl.substring(s3UrlPattern.length());

        log.info("s3 객체 키: " + fileKey);
        try {
            amazonS3.deleteObject(amazonS3Config.getBucket(), fileKey);

        } catch (SdkClientException e) {
            log.error("Error deleting file from S3", e);
            throw new IOException("Error deleting file from S3", e);
        }
    }
}
