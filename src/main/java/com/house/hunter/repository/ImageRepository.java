package com.house.hunter.repository;

import com.house.hunter.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<List<Image>> findImagesByPropertyId(UUID propertyId);
    Optional<Image> findImageByIdAndPropertyId(UUID id, UUID propertyId);
    void deleteByIdAndPropertyId(UUID id, UUID propertyId);
    void deleteByPropertyId(UUID propertyId);
}

