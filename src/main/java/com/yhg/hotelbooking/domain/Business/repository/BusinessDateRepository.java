package com.yhg.hotelbooking.domain.Business.repository;

import com.yhg.hotelbooking.domain.Business.entity.BusinessDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDateRepository extends JpaRepository<BusinessDate, Long> {
}