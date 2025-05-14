package com.example.electric_usage_tracker.repository;

import com.example.electric_usage_tracker.model.ApplianceUsage;
import com.example.electric_usage_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplianceUsageRepository extends JpaRepository<ApplianceUsage, Long> {
    List<ApplianceUsage> findByUserOrderByRecordedAtDesc(User user);

    @Query("SELECT SUM(a.kWhConsumed) FROM ApplianceUsage a WHERE a.user = :user")
    Double getTotalEnergyByUser(@Param("user") User user);

    @Query("SELECT SUM(a.cost) FROM ApplianceUsage a WHERE a.user = :user")
    Double getTotalCostByUser(@Param("user") User user);
}