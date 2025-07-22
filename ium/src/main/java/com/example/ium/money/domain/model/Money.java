package com.example.ium.money.domain.model;

import com.example.ium.member.domain.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "money_tb")
@Builder
@AllArgsConstructor
public class Money {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "money_id")
  private Long id; // 돈 ID
  
  @ManyToOne
  private Member member;
  
  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private MoneyType moneyType;
  
  @Column(name = "price", nullable = false)
  private int price;
}
