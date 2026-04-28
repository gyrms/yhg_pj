package com.yhg.hotelbooking.domain.Business.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "business_date")
@Getter
public class BusinessDate {
    @Id
    private Long id = 1L;

    @Column(name = "business_date")
    private LocalDate currentDate;

    public void nextDay() {
        this.currentDate = this.currentDate.plusDays(1);
    }
}