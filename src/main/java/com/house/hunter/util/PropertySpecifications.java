package com.house.hunter.util;

import com.house.hunter.constant.AdType;
import com.house.hunter.constant.ApartmentType;
import com.house.hunter.constant.IsFurnished;
import com.house.hunter.constant.PropertyStatus;
import com.house.hunter.model.dto.property.PropertySearchCriteriaDTO;
import com.house.hunter.model.entity.Property;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class PropertySpecifications {
    public static Specification<Property> createSpecification(PropertySearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), PropertyStatus.VERIFIED));
            if (criteria.getTitle() != null && !criteria.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + criteria.getTitle().toLowerCase() + "%"));
            }
            if (criteria.getDistrict() != null && !criteria.getDistrict().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), "%" + criteria.getDistrict().toLowerCase() + "%"));
            }

            if (criteria.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }
            if (criteria.getSquareMeters() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("squareMeters"), criteria.getSquareMeters()));
            }
            if (criteria.getIsFurnished() != null && criteria.getIsFurnished().length > 0) {
                List<IsFurnished> isFurnishedList = Arrays.stream(criteria.getIsFurnished())
                        .map(type -> IsFurnished.valueOf(type.toUpperCase()))
                        .collect(Collectors.toList());
                predicates.add(root.get("isFurnished").in(isFurnishedList));
            }
            if (criteria.getMinFloorNumber() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("floorNumber"), criteria.getMinFloorNumber()));
            }
            if (criteria.getMaxFloorNumber() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("floorNumber"), criteria.getMaxFloorNumber()));
            }
            if (criteria.getAvailableFrom() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("availableFrom"), criteria.getAvailableFrom()));
            }
            if (criteria.getMinRooms() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("numberOfRooms"), criteria.getMinRooms()));
            }
            if (criteria.getMaxRooms() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("numberOfRooms"), criteria.getMaxRooms()));
            }
            if (criteria.getCreatedAt() != null && criteria.getCreatedAt().length > 0) {
                LocalDate currentDate = LocalDate.now();
                List<Predicate> createdAtPredicates = new ArrayList<>();

                for (String createdAt : criteria.getCreatedAt()) {
                    LocalDate startDate;

                    if (createdAt.equals("last24hours")) {
                        startDate = currentDate.minusDays(1);
                    } else if (createdAt.equals("lastWeek")) {
                        startDate = currentDate.minusWeeks(1);
                    } else if (createdAt.equals("lastMonth")) {
                        startDate = currentDate.minusMonths(1);
                    } else {
                        throw new IllegalArgumentException("Invalid createdAt value: " + createdAt);
                    }

                    createdAtPredicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, currentDate));
                }

            }
            if (criteria.getAdType() != null && criteria.getAdType().length > 0) {
                List<AdType> adTypes = Arrays.stream(criteria.getAdType())
                        .map(type -> AdType.valueOf(type.toUpperCase()))
                        .collect(Collectors.toList());
                predicates.add(root.get("adType").in(adTypes));
            }
            if (criteria.getApartmentType() != null && criteria.getApartmentType().length > 0) {
                List<ApartmentType> apartmentTypes = Arrays.stream(criteria.getApartmentType())
                        .map(type -> ApartmentType.valueOf(type.replace(" ", "_").toUpperCase()))
                        .collect(Collectors.toList());
                predicates.add(root.get("apartmentType").in(apartmentTypes));
            }

            if (criteria.getAddress() != null && !criteria.getAddress().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + criteria.getAddress().toLowerCase() + "%"));
            }
            if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + criteria.getDescription().toLowerCase() + "%"));
            }
            if (criteria.getOwnerEmail() != null && !criteria.getOwnerEmail().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("owner").get("email"), criteria.getOwnerEmail()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


