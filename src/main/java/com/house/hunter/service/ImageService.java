package com.house.hunter.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ImageService {
    List<UUID> uploadImage(UUID propertyId, MultipartFile[] files) throws IOException;

    List<byte[]> getImages(UUID propertyId) throws IOException;

    void deleteImage(UUID imageId, UUID propertyId) throws IOException;

    void deleteImages(UUID propertyId) throws IOException;

    List<UUID> updateImage(UUID propertyId, MultipartFile[] images) throws IOException;

}
