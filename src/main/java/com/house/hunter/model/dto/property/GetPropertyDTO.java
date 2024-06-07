package com.house.hunter.model.dto.property;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPropertyDTO {
    private UUID id;
    private String title;
    private String address;
    private double price;
    private int squareMeters;
    private String description;
    private String isFurnished;
    private int numberOfRooms;
    private int floorNumber;
    private Date availableFrom;
    private String adType;
    private String apartmentType;
    private String ownerEmail;
    private String district;
    private String status;
    private String ownershipDocument;
}
