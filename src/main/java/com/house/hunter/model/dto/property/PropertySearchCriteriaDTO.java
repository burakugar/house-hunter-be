package com.house.hunter.model.dto.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import com.house.hunter.constant.IsFurnished;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class PropertySearchCriteriaDTO {
    private String title;
    private Double minPrice;
    private Double maxPrice;
    private int squareMeters;
    private String[] isFurnished;
    private Integer minFloorNumber;
    private Integer maxFloorNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate availableFrom;
    private String[] createdAt;
    private Integer minRooms;
    private Integer maxRooms;
    private String[] adType;
    private String[] apartmentType;
    private String address;
    private String description;
    private String ownerEmail;
    private String district;
    private String status;
}
