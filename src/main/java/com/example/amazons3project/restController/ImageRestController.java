package com.example.amazons3project.restController;

import com.example.amazons3project.service.S3UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class ImageRestController {
    private final S3UploaderService s3UploaderService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFile(
            @RequestParam("multipartfiles")List<MultipartFile> multipartFiles)
    {
        return ResponseEntity.ok(s3UploaderService.insertFile(multipartFiles));
    }

}
