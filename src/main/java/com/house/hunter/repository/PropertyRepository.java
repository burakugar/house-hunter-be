package com.house.hunter.repository;

import com.house.hunter.constant.AdType;
import com.house.hunter.constant.PropertyStatus;
import com.house.hunter.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID>, JpaSpecificationExecutor<Property> {

    Optional<Void> deleteByOwnerEmailAndId(String email, UUID id);

    boolean existsByTitle(String title);

    Optional<Property> findByCreatedAtBefore(LocalDateTime date);

    Optional<Property> findByOwnerEmailAndId(String email, UUID id);

    Optional<List<Property>> findByOwnerId(UUID id);

    long countByAdType(AdType adType);

    long countByStatus(PropertyStatus status);


    long countByAdTypeAndStatus(AdType adType, PropertyStatus status);

}
