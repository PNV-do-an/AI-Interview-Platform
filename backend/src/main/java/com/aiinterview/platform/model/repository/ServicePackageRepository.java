package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {

    List<ServicePackage> findByActiveTrueOrderByPriceAsc();

    boolean existsByName(String name);
}
