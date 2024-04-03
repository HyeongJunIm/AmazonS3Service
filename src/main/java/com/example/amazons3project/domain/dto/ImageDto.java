package com.example.amazons3project.domain.dto;

import com.example.amazons3project.domain.entity.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDto {
    Long id;
    String fileURL;

    public ImageDto(String fileURL) {
        this.fileURL = fileURL;
    }

    public Image toEntity() {
        return Image.builder()
                .fileURL(fileURL)
                .build();
    }
}