package com.example.amazons3project.repository;

import com.example.amazons3project.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepogitory extends JpaRepository<Image,Long> {
}
