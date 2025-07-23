package com.example.ium.member.domain.model.expert;

import com.example.ium._core.entity.BaseEntity;
import com.example.ium.member.domain.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expert_profile_tb")
public class ExpertProfile extends BaseEntity {

    @Id
    private Long memberId;

    @OneToOne
    @MapsId
    private Member member; // 회원 정보
    @OneToMany(mappedBy = "expertProfile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ExpertSpecialization> expertSpecialization = new ArrayList<>(); // 전문가 전문 분야

    private boolean activated; // 활성화 여부

    @Column(name = "introduce_message", length = 1000)
    private String introduceMessage;
    @Column(name = "portfolio_description", length = 1000)
    private String portfolioDescription;
    @Column(length = 50)
    private String school;
    @Column(length = 50)
    private String major;

    @Embedded
    private CareerDate careerDate; // 경력 시작일
    @Embedded
    private Salary salary; // 희망 연봉
    @Embedded
    private NegoYn negoYn; // 협상 가능 여부
    @Embedded
    private CompletedRequestCount completedRequestCount;

    @Builder
    private ExpertProfile(Long memberId, Member member, boolean activated,
                                 String introduceMessage, String portfolioDescription,
                                 String school, String major,
                                 CareerDate careerDate, Salary salary, NegoYn negoYn, CompletedRequestCount completedRequestCount) {
        this.memberId = memberId;
        this.member = member;
        this.activated = activated;
        this.introduceMessage = introduceMessage;
        this.portfolioDescription = portfolioDescription;
        this.school = school;
        this.major = major;
        this.careerDate = careerDate;
        this.salary = salary;
        this.negoYn = negoYn;
        this.completedRequestCount = completedRequestCount;
    }

    public static ExpertProfile createExpertProfile(Member member,
                                                    String introduceMessage, String portfolioDescription,
                                                    String school, String major,
                                                    LocalDate careerStartDate, int salary, boolean negoYn) {
        return ExpertProfile.builder()
                .memberId(member.getId())
                .member(member)
                .activated(true)
                .introduceMessage(introduceMessage)
                .portfolioDescription(portfolioDescription)
                .school(school)
                .major(major)
                .careerDate(CareerDate.of(careerStartDate))
                .salary(Salary.of(salary))
                .negoYn(NegoYn.of(negoYn))
                .completedRequestCount(CompletedRequestCount.init())
                .build();
    }

    public void addExpertSpecialization(ExpertSpecialization expertSpecialization) {
        this.expertSpecialization.add(expertSpecialization);
        expertSpecialization.setExpertProfile(this);
    }

    // 전문 프로필 활성화 메소드
    public void activate() {
        this.activated = true;
    }
    // 전문 프로필 비활성화 메소드
    public void deactivate() {
        this.activated = false;
    }

    // 의뢰 완료 카운트 증가 메소드
    public void incrementCompletedRequestCount() {
        this.completedRequestCount = this.completedRequestCount.increment();
    }
}
