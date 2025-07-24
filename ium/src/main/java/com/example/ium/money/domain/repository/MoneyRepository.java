package com.example.ium.money.domain.repository;

import com.example.ium.member.domain.model.Member;
import com.example.ium.money.domain.model.Money;
import com.example.ium.money.domain.model.MoneyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MoneyRepository extends JpaRepository<Money, Long> {

    @Query("SELECT SUM(m.price) FROM Money m WHERE m.member = :member AND m.moneyType = :moneyType")
    Optional<Integer> sumPriceByMemberAndMoneyType(@Param("member") Member member, @Param("moneyType") MoneyType moneyType);
}
