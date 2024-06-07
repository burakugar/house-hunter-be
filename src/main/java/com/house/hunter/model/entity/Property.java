package com.house.hunter.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import com.house.hunter.constant.IsFurnished;
import com.house.hunter.constant.PropertyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Address is required")
    private String address;

    @DecimalMin(value = "0", message = "Price must be a positive number")
    @Max(value = 50000000, message = "Price must be less than 50000000 CZK")
    @NotNull
    private double price;

    @Positive(message = "Square meters must be positive")
    @NotNull
    private int squareMeters;

    @NotEmpty(message = "Description is required")
    @Size(min = 20, message = "Description must be at least 10 characters long")
    private String description;

    @NotEmpty(message = "District is required")
    @Size(min = 6, message = "District must be at least 6 characters long")
    private String district;

    @Enumerated(EnumType.STRING)
    private IsFurnished isFurnished;

    @NotNull(message = "Number of rooms is required")
    private int numberOfRooms;

    @NotNull
    @Min(-10)
    @Max(100)
    private int floorNumber;

    @Temporal(TemporalType.DATE)
    @FutureOrPresent(message = "The date must be in the future or present")
    @NotNull
    private Date availableFrom;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AdType adType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ApartmentType apartmentType;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"properties"})
    private User owner;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"property"})
    List<Image> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JsonIgnoreProperties({"property"})
    private PropertyStatus status;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"property"})
    private Document document = new Document();
}
