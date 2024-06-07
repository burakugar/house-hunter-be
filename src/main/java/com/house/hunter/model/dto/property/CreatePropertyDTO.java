package com.house.hunter.model.dto.property;

import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import com.house.hunter.constant.IsFurnished;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyDTO {

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Address is required")
    @Size(min = 10, message = "Address must be at least 10 characters long")
    private String address;

    @DecimalMin(value = "0", message = "Price must be a positive number")
    @NotNull
    private double price;

    @Positive(message = "Square meters must be positive")
    @NotNull
    private int squareMeters;

    @NotEmpty(message = "Description is required")
    @Size(min = 20, message = "Description must be at least 20 characters long")
    private String description;

    @NotEmpty(message = "Furnished info cannot be empty")
    @Pattern(regexp = IsFurnished.PATTERN, message = "Invalid furnished value")
    private String isFurnished;
    @NotEmpty(message = "District info cannot be empty")
    private String district;

    @NotNull(message = "Number of rooms is required")
    @Positive(message = "Number of rooms must be positive")
    private int numberOfRooms;

    @PositiveOrZero(message = "Floor number cannot be negative")
    @NotNull
    private int floorNumber;

    @FutureOrPresent(message = "The date must be in the future or present")
    @NotNull
    private Date availableFrom;

    @NotEmpty(message = "Ad type cannot be empty")
    @Pattern(regexp = AdType.PATTERN, message = "Invalid ad type value")
    private String adType;

    @NotEmpty(message = "Apartment type cannot be empty")
    @Pattern(regexp = ApartmentType.PATTERN, message = "Invalid apartment type value")
    private String apartmentType;

    @Email(message = "Owner Email should be a valid email")
    private String ownerEmail;
}

