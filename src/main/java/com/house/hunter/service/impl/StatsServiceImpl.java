package com.house.hunter.service.impl;


import com.house.hunter.constant.AdType;
import com.house.hunter.constant.PropertyStatus;
import com.house.hunter.constant.UserRole;
import com.house.hunter.model.dto.stats.StatsDTO;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Autowired
    public StatsServiceImpl(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "stats", key = "'allStats'")
    public StatsDTO getStats() {
        long totalProperties = propertyRepository.countByStatus(PropertyStatus.VERIFIED);
        long totalRentalProperties = propertyRepository.countByAdTypeAndStatus(AdType.RENTAL, PropertyStatus.VERIFIED);
        long totalSaleProperties = propertyRepository.countByAdTypeAndStatus(AdType.SALE, PropertyStatus.VERIFIED);

        long totalUsers = userRepository.count();
        long totalLandlords = userRepository.countByRole(UserRole.LANDLORD);
        long totalTenants = userRepository.countByRole(UserRole.TENANT);
        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);

        StatsDTO stats = new StatsDTO();
        stats.setTotalProperties(totalProperties);
        stats.setTotalRentalProperties(totalRentalProperties);
        stats.setTotalSaleProperties(totalSaleProperties);
        stats.setTotalUsers(totalUsers);
        stats.setTotalLandlords(totalLandlords);
        stats.setTotalTenants(totalTenants);
        stats.setTotalAdmins(totalAdmins);

        return stats;
    }
}


