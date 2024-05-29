package com.dto.way.member.global;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.dto.way.member.global.config.AmazonS3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    public ResponseEntity<byte[]> getObjectByUrl(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        String path = url.getPath();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        String key = decodedPath.substring(1); // 첫 번째 슬래시를 제거하여 객체 키를 얻습니다.

        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(amazonS3Config.getBucket(), key));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

}
