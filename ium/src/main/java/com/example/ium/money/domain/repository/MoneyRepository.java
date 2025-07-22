package com.example.ium.money.domain.repository;

import com.example.ium.money.domain.model.Money;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRepository extends JpaRepository<Money, Long> {
}
