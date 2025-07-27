package com.example.ium.money.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.money.domain.model.Money;
import com.example.ium.money.domain.model.MoneyType;
import com.example.ium.money.domain.repository.MoneyRepository;
import com.example.ium.money.dto.response.MoneyInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MoneyService {

  private final MemberJPARepository memberJPARepository;
  private final MoneyRepository moneyRepository;
  private final KakaoPayService kakaoPayService;
  
  public String createCreditCharge(String email, int price) {
    Member member = memberJPARepository.findByEmail(Email.of(email))
            .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));
    
    Money credit = Money.builder()
            .moneyType(MoneyType.CREDIT)
            .member(member)
            .price(price)
            .build();
    moneyRepository.save(credit);
    String result = kakaoPayService.readyPay(credit.getId(), email);
    
    Money point = Money.builder()
            .moneyType(MoneyType.POINT)
            .member(member)
            .price(price/10)
            .build();
    moneyRepository.save(point);
    
    return result;
  }

  public MoneyInfoDto getMoneyInfo(Long memberId) {
    Member member = memberJPARepository.findById(memberId)
            .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

    int credit = moneyRepository.sumPriceByMemberAndMoneyType(member, MoneyType.CREDIT)
            .orElse(0);
    int point = moneyRepository.sumPriceByMemberAndMoneyType(member, MoneyType.POINT)
            .orElse(0);

    return new MoneyInfoDto(credit, point);
  }
}
