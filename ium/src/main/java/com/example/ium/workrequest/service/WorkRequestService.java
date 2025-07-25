package com.example.ium.workrequest.service;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.member.infrastructure.service.FireBaseFileService;
import com.example.ium.money.domain.model.Money;
import com.example.ium.money.domain.model.MoneyType;
import com.example.ium.money.domain.repository.MoneyRepository;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.workrequest.dto.MatchedDto;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium._core.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkRequestService {

    private final WorkRequestRepository workRequestRepository;
    private final FireBaseFileService fireBaseFileService;
    private final MoneyRepository moneyRepository;
    private final MemberJPARepository memberJPARepository;
    private final ExpertProfileJPARepository expertProfileJPARepository;


    // 전체 (ad_point 높은 순으로 정렬)
    public List<WorkRequestEntity> getAllRequests() {
        return workRequestRepository.findAllByOrderByAdPointDesc();
    }

    // 카테고리별 필터링
    public List<WorkRequestEntity> getRequestsByCategory(String category) {
        // 카테고리 매핑
        String mappedCategory = mapCategory(category);
        return workRequestRepository.findByCategoryContainingIgnoreCaseOrderByAdPointDesc(mappedCategory);
    }

    // 검색 기능
    public List<WorkRequestEntity> searchRequests(String search) {
        return workRequestRepository.findBySearchTermOrderByAdPointDesc(search);
    }

    // 카테고리와 검색 조건 모두 적용
    public List<WorkRequestEntity> getRequestsByCategoryAndSearch(String category, String search) {
        String mappedCategory = mapCategory(category);
        return workRequestRepository.findByCategoryAndSearchOrderByAdPointDesc(mappedCategory, search);
    }

    // 카테고리 매핑 메소드
    private String mapCategory(String category) {
        if (category == null || category.trim().isEmpty() || "all".equals(category)) {
            return "";
        }

        switch (category) {
            case "design":
                return "디자인";
            case "programming":
                return "프로그래밍";
            case "video":
                return "영상";
            case "legal":
                return "법무";
            case "translation":
                return "번역";
            default:
                return category;
        }
    }

    // 하나
    public WorkRequestEntity getRequest(Long id) {
        return workRequestRepository.findById(id)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.WORK_REQUEST_NOT_FOUND));
    }

    // 새 요청
    public WorkRequestEntity saveRequest(WorkRequestEntity workRequest) {
        return workRequestRepository.save(workRequest);
    }

    public WorkRequestEntity getLatestRequest() {
        return workRequestRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("등록된 요청이 없습니다."));
    }
    public MatchedDto getMatchedExpert(Long expertId) {
        ExpertProfile profile = expertProfileJPARepository.findByIdByEagerLoading(expertId)
                .orElseThrow(() -> new RuntimeException("프로필 없음"));

        Member member = memberJPARepository.findById(expertId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        return new MatchedDto(
                member.getUsername(),
                member.getEmail(),
                member.getRole(),
                profile.getCareerDate(),
                profile.getSalary(),
                profile.getSchool()
        );
    }

    public void uploadFile(MultipartFile file, Long workRequestId, String email) {
        String fileUrl = fireBaseFileService.uploadFile(file, file.getOriginalFilename());

        WorkRequestEntity workRequest = workRequestRepository.findById(workRequestId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.WORK_REQUEST_NOT_FOUND));
        workRequest.setFileUrl(fileUrl);
        workRequest.setFileName(file.getOriginalFilename());
        workRequest.setStatus(WorkRequestEntity.Status.DONE);
        workRequestRepository.save(workRequest);

        Member member = memberJPARepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        Money money = Money.builder()
                .moneyType(workRequest.getType().equals(WorkRequestEntity.Type.FORMAL) ? MoneyType.CREDIT : MoneyType.POINT)
                .price(workRequest.getPrice())
                .member(member)
                .build();
        moneyRepository.save(money);
    }
}