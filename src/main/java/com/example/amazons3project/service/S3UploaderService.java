package com.example.amazons3project.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.amazons3project.domain.dto.ImageDto;
import com.example.amazons3project.repository.ImageRepogitory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class S3UploaderService {


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.static}")
    private String region;

    private final AmazonS3 amazonS3;
    private final ImageRepogitory imgRepogitory;

    @Transactional
    public List<String> insertFile(List<MultipartFile> multipartFiles){

        List<String> fileURLs = new ArrayList<>();

        multipartFiles.forEach(files -> {
            String fileName = convertFileName(files.getOriginalFilename());
            String URLs = "https://" + bucket + ".s3." + region + ".amzonaws.com/" + fileName;

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(files.getSize());
            objectMetadata.setContentType(files.getContentType());

            try(InputStream inputStream = files.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucket,fileName,inputStream,objectMetadata));
                imgRepogitory.save(new ImageDto(URLs).toEntity());
                fileURLs.add(URLs);
            }catch (IOException e){

                throw  new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드 실패!!");
            }
        });
        return fileURLs;
    }

    /**
     * 파일 이름을 랜덤으로 형성
     * @param fileName
     * @return 랜덤 생성 UUID-파일이름.jpg,
     */
    public String convertFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    /**
     * 파일의 확장자 이름을 획득
     * @param fileName 파일이름
     * @return 파일 확장자
     */
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        }catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"잘못된 형시의 파일" + fileName);
        }
    }


}
