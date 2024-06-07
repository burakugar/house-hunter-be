package com.house.hunter.controller;


import com.house.hunter.model.dto.property.CreatePropertyDTO;
import com.house.hunter.model.dto.property.GetPropertyDTO;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.dto.property.UpdatePropertyDTO;
import com.house.hunter.model.dto.search.PropertyDTO;
import com.house.hunter.service.ImageService;
import com.house.hunter.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ImageService imageService;

    @Autowired
    public PropertyController(PropertyService propertyService, ImageService imageService) {
        this.propertyService = propertyService;
        this.imageService = imageService;
    }

    @GetMapping("/details")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get property details by id")
    public PropertyDTO getPropertyDetailsById(@RequestParam UUID id) {
        return propertyService.getPropertyById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @Operation(summary = "Create property")
    public ResponseEntity<UUID> createProperty(@RequestBody CreatePropertyDTO propertyCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(propertyCreateDto));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search properties")
    public Page<PropertyDTO> searchProperties(PropertySearchCriteriaDTO criteria, Pageable pageable) {
        return propertyService.searchProperties(criteria, pageable);
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get properties by owner email")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    public List<GetPropertyDTO> getProperties(@PathVariable String email) {
        return propertyService.getProperties(email);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UpdatePropertyDTO> updateProperty(@PathVariable UUID id, @RequestBody UpdatePropertyDTO updatePropertyDTO) {
        UpdatePropertyDTO updatedProperty = propertyService.updateProperty(id, updatePropertyDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete property")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/{propertyId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @Operation(summary = "Upload images of a property")
    public List<UUID> uploadImages(@PathVariable UUID propertyId,
                                   @ArraySchema(
                                           schema = @Schema(type = "string", format = "binary"),
                                           minItems = 1
                                   )
                                   @RequestPart(value = "images") MultipartFile[] images) throws IOException {
        return imageService.uploadImage(propertyId, images);
    }

    @PutMapping(path = "/{propertyId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @Operation(summary = "Update the images of a property")
    public List<UUID> updateImages(@PathVariable UUID propertyId,
                                   @ArraySchema(
                                           schema = @Schema(type = "string", format = "binary"),
                                           minItems = 1
                                   )
                                   @RequestPart(value = "images") MultipartFile[] images) throws IOException {
        return imageService.updateImage(propertyId, images);
    }

    @GetMapping("/{propertyId}/images")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get images of a property")
    public ResponseEntity<List<byte[]>> getImagesByProperty(@PathVariable UUID propertyId) throws IOException {
        return ResponseEntity.ok(imageService.getImages(propertyId));
    }

    @DeleteMapping("/{propertyId}/images/{imageId}")
    @Operation(summary = "Delete image of a property")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId, @PathVariable UUID propertyId) throws IOException {
        imageService.deleteImage(imageId, propertyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{propertyId}/images")
    @Operation(summary = "Delete all images of a property")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteImages(@PathVariable UUID propertyId) throws IOException {
        imageService.deleteImages(propertyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/admin/verify/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify property as admin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> verifyProperty(@PathVariable UUID propertyId) {
        propertyService.verifyProperty(propertyId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/admin/reject/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject property as admin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> rejectProperty(@PathVariable UUID propertyId) {
        propertyService.rejectProperty(propertyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/property-requests/{email}")
    @PreAuthorize("hasAnyRole('ADMIN','LANDLORD')")
    @Operation(summary = "Get property requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getPropertyRequests(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getPropertyRequests(email));

    }

}

