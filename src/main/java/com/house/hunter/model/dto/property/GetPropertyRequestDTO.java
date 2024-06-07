package com.house.hunter.model.dto.property;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class GetPropertyRequestDTO {
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
