package com.house.hunter.repository;

import com.house.hunter.constant.DocumentType;
import com.house.hunter.model.entity.Document;
import com.house.hunter.model.entity.Property;
import com.house.hunter.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<List<Document>> findDocumentsByUserEmail(String email);

    Optional<Document> findByFilename(String fileName);

    Optional<Document> findByUserAndDocumentType(User user, DocumentType documentType);

    void deleteByUserId(UUID userId);

    Optional<Document> findByPropertyAndDocumentType(Property property, DocumentType documentType);


}
