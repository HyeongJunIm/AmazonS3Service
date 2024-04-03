package com.example.amazons3project.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileURL;
    @CreatedDate
    private LocalDateTime createdDate;


    @Builder
    public Image(Long id, String fileURL, LocalDateTime createdDate) {
        this.id = id;
        this.fileURL = fileURL;
        this.createdDate = createdDate;
    }
}
