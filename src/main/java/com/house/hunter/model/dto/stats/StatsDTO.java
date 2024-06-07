package com.house.hunter.model.dto.stats;

import lombok.Data;
import org.springframework.cache.annotation.Cacheable;

@Data
@Cacheable(value = "statsCache", key = "'allStats'")
public class StatsDTO {
    private long totalProperties;
    private long totalRentalProperties;
    private long totalSaleProperties;
    private long totalUsers;
    private long totalLandlords;
    private long totalTenants;
    private long totalAdmins;
}
