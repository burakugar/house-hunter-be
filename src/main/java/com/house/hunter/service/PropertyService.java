package com.house.hunter.service;

import com.house.hunter.model.dto.property.CreatePropertyDTO;
import com.house.hunter.model.dto.property.GetPropertyDTO;
import com.house.hunter.model.dto.property.GetPropertyRequestDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.dto.property.UpdatePropertyDTO;
import com.house.hunter.model.dto.search.PropertyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PropertyService {

    UpdatePropertyDTO updateProperty(UUID id, UpdatePropertyDTO updatePropertyDTO);

    void deleteProperty(UUID id);

    Page<PropertyDTO> searchProperties(PropertySearchCriteriaDTO searchCriteria, Pageable pageable);

    List<GetPropertyDTO> getProperties(String email);

    PropertyDTO getPropertyById(UUID id);

    UUID createProperty(CreatePropertyDTO propertyCreateDto);

    List<GetPropertyRequestDTO> getPropertyRequests(String email);

    void verifyProperty(UUID propertyId);

    void rejectProperty(UUID propertyId);
}

