package com.example.ium._core.data;

import com.example.ium.member.domain.model.*;
import com.example.ium.member.domain.model.expert.*;
import com.example.ium.member.domain.repository.*;
import com.example.ium.money.domain.model.Money;
import com.example.ium.money.domain.model.MoneyType;
import com.example.ium.money.domain.repository.MoneyRepository;
import com.example.ium.specialization.domain.model.Specialization;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.springframework.data.jpa.domain.AbstractAuditable_.createdBy;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class DummyDataInitializer {
    
    // Repository 의존성 주입
    private final MemberJPARepository memberRepository;
    private final ExpertProfileJPARepository expertProfileRepository;
    private final ExpertSpecializationJPARepository expertSpecializationRepository;
    private final SpecializationJPARepository specializationRepository;
    private final MoneyRepository moneyRepository;
    private final WorkRequestRepository workRequestRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final Random random = new Random();
    
    @PostConstruct
    public void initDummyData() {
        log.info("더미 데이터 초기화 체크를 시작합니다...");
        
        // 이미 데이터가 있으면 초기화하지 않음
        if (memberRepository.count() > 0) {
            log.info("더미 데이터가 이미 존재합니다. (회원 수: {})", memberRepository.count());
            log.info("초기화를 건너뜁니다.");
            return;
        }
        
        log.info("더미 데이터 초기화를 시작합니다...");
        
        // 1. 전문 분야 생성
        List<Specialization> specializations = createSpecializations();
        
        // 2. 회원 생성
        List<Member> members = createMembers();
        
        // 3. 전문가 프로필 생성
        List<ExpertProfile> expertProfiles = createExpertProfiles(members, specializations);
        
        // 4. 작업 의뢰 생성
        List<WorkRequestEntity> workRequests = createWorkRequests(members, expertProfiles);
        
        // 5. 포인트/크레딧 생성
        createMoneyData(members);
        
        // 6. 데이터 정합성 검증 및 수정
        validateAndFixDataIntegrity(members, specializations);
        
        log.info("더미 데이터 초기화가 완료되었습니다!");
        log.info("생성된 데이터: 회원 {}, 전문가 {}, 의뢰 {}, 전문분야 {}", 
                members.size(), expertProfiles.size(), workRequests.size(), specializations.size());
    }
    
    // 1. 전문 분야 생성 (IumApplication.java와 동일한 5개 카테고리)
    private List<Specialization> createSpecializations() {
        // 이미 존재하는 specialization 건너뛰기
        if (specializationRepository.count() > 0) {
            log.info("전문분야 데이터가 이미 존재합니다. 생성을 건너뛁니다.");
            return specializationRepository.findAll();
        }
        
        List<Specialization> specializations = new ArrayList<>();
        
        // IumApplication.java에서 정의한 5개 전문 분야
        String[] specializationNames = {
            "디자인",
            "프로그래밍", 
            "영상편집",
            "세무/법무/노무",
            "번역/통역"
        };
        
        for (String name : specializationNames) {
            Specialization specialization = Specialization.createSpecialization(name);
            specializations.add(specializationRepository.save(specialization));
        }
        
        return specializations;
    }
    
    // 2. 회원 생성 (더 많이)
    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        
        // 전체 30명 중 24명을 전문가로, 6명을 일반 사용자로 생성
        String[] usernames = {
            "코딩마스터", "디자인킹", "영상편집러", "번역고수", "개발자123", "크리에이터", "마케터Pro", "데이터분석가", "풀스택개발자", "UI디자이너",
            "모션그래픽", "웹개발자", "앱개발러", "로고마스터", "번역전문가", "영상제작자", "프론트엔드", "백엔드개발", "그래픽디자인", "콘텐츠라이터",
            "SEO전문가", "광고기획자", "브랜딩전문", "3D모델러", "게임개발자", "챗봇개발", "데이터과학자", "AI개발자", "블록체인개발", "스타트업CEO"
        };
        
        String[] emailPrefixes = {
            "coding_master", "design_king", "video_editor", "translator", "developer123", "creator", "marketer_pro", "data_analyst", "fullstack_dev", "ui_designer",
            "motion_graphic", "web_developer", "app_developer", "logo_master", "translation_pro", "video_creator", "frontend_dev", "backend_dev", "graphic_design", "content_writer",
            "seo_expert", "ad_planner", "branding_pro", "3d_modeler", "game_developer", "chatbot_dev", "data_scientist", "ai_developer", "blockchain_dev", "startup_ceo"
        };
        
        // 처음 24명은 전문가로, 나머지 6명은 일반 사용자로 생성
        for (int i = 0; i < usernames.length; i++) {
            Member member;
            
            if (i < 24) { // 처음 24명은 전문가
                member = Member.createExpert(
                    usernames[i],
                    Email.of(emailPrefixes[i] + "@test.com"),
                    Password.encode("password123", passwordEncoder)
                );
                log.debug("전문가 회원 생성: {}", usernames[i]);
            } else { // 나머지 6명은 일반 사용자
                member = Member.createMember(
                    usernames[i],
                    Email.of(emailPrefixes[i] + "@test.com"),
                    Password.encode("password123", passwordEncoder)
                );
                log.debug("일반 회원 생성: {}", usernames[i]);
            }
            
            members.add(memberRepository.save(member));
        }
        
        // 관리자 계정
        Member admin = Member.builder()
            .username("운영진")
            .email(Email.of("admin123@test.com"))
            .password(Password.encode("admin123", passwordEncoder))
            .role(Role.ADMIN)
            .status(Status.ACTIVE)
            .build();
        members.add(memberRepository.save(admin));
        
        return members;
    }
    
    // 3. 전문가 프로필 생성 (더 많이, 다양하게)
    private List<ExpertProfile> createExpertProfiles(List<Member> members, List<Specialization> specializations) {
        List<ExpertProfile> expertProfiles = new ArrayList<>();
        
        // 기존 전문가 프로필이 있는지 확인
        List<ExpertProfile> existingProfiles = expertProfileRepository.findAll();
        log.info("기존 전문가 프로필 수: {}", existingProfiles.size());
        
        // 기존 전문가 프로필의 member ID 수집
        Set<Long> existingMemberIds = new HashSet<>();
        for (ExpertProfile profile : existingProfiles) {
            existingMemberIds.add(profile.getMember().getId());
        }
        
        // Role.EXPERT인 회원들에 대해서만 전문가 프로필 생성
        int profileCount = 0;
        
        for (Member member : members) {
            // EXPERT role이 아니거나 이미 프로필이 있는 경우 건너뛰기
            if (member.getRole() != Role.EXPERT) {
                continue;
            }
            
            if (existingMemberIds.contains(member.getId())) {
                log.info("회원 {}은 이미 전문가 프로필이 있어 건너뜁니다.", member.getUsername());
                continue;
            }
            
            try {
                
                ExpertProfile expertProfile = ExpertProfile.createExpertProfile(
                    member,
                    getIntroMessage(profileCount),
                    getPortfolioDescription(profileCount),
                    getSchool(profileCount),
                    getMajor(profileCount),
                    LocalDate.now().minusYears(1 + random.nextInt(8)), // 1-8년 경력
                    300000 + (profileCount * 50000) + random.nextInt(200000), // 30만원~200만원 희망 연봉
                    random.nextBoolean() // 협상 가능 여부 랜덤
                );
                
                // 의뢰 완료 경험 시뮬레이션 (더 현실적으로 0-20개 범위)
                int completedCount = random.nextInt(21);
                for (int k = 0; k < completedCount; k++) {
                    expertProfile.incrementCompletedRequestCount();
                }
                
                // 전문가 프로필 저장
                expertProfile = expertProfileRepository.save(expertProfile);
                log.info("전문가 프로필 생성 완료: {}", member.getUsername());
                
                // 전문 분야 매핑 (각 전문가마다 1-3개의 전문분야)
                int specCount = 1 + random.nextInt(3);
                Set<Integer> usedSpecIndices = new HashSet<>(); // 중복 방지
                for (int j = 0; j < specCount; j++) {
                    try {
                        int specIndex;
                        do {
                            specIndex = random.nextInt(specializations.size());
                        } while (usedSpecIndices.contains(specIndex));
                        usedSpecIndices.add(specIndex);
                        
                        ExpertSpecialization expertSpecialization = ExpertSpecialization.createExpertSpecialization(
                            expertProfile, 
                            specializations.get(specIndex).getId()
                        );
                        expertProfile.addExpertSpecialization(expertSpecialization);
                        expertSpecializationRepository.save(expertSpecialization);
                    } catch (Exception e) {
                        log.warn("전문 분야 매핑 중 오류 발생: {}", e.getMessage());
                    }
                }
                
                expertProfiles.add(expertProfile);
                profileCount++;
                
            } catch (Exception e) {
                log.error("전문가 프로필 생성 중 오류 발생 (회원: {}): {}", member.getUsername(), e.getMessage());
                // 오류가 발생해도 계속 진행
                continue;
            }
        }
        
        // 기존 프로필도 반환 목록에 추가 (작업 의뢰 생성 시 사용하기 위해)
        expertProfiles.addAll(existingProfiles);
        
        return expertProfiles;
    }
    
    // 4. 작업 의뢰 생성 (더 많이, 현실적으로)
    private List<WorkRequestEntity> createWorkRequests(List<Member> members, List<ExpertProfile> expertProfiles) {
        List<WorkRequestEntity> workRequests = new ArrayList<>();
        
        String[] titles = {
            "로고 디자인 및 브랜딩 패키지 제작", "React 기반 웹사이트 개발", "제품 홍보 영상 편집",
            "모바일 앱 UI/UX 디자인", "기술 문서 영한 번역", "계약서 검토 및 법무 자문",
            "마케팅 콘텐츠 작성", "데이터 분석 및 시각화", "AI 챗봇 개발", "3D 제품 모델링",
            "쇼핑몰 웹사이트 제작", "유튜브 채널 편집", "카페 인테리어 디자인", "앱 아이콘 디자인",
            "영문 계약서 번역", "세무 신고 대행", "SNS 마케팅 전략", "빅데이터 분석", "게임 개발",
            "웨딩 영상 촬영 및 편집", "기업 로고 리뉴얼", "홈페이지 SEO 최적화", "명함 디자인",
            "프레젠테이션 제작", "특허 출원 대행", "온라인 광고 운영", "모바일 게임 개발",
            "제품 카탈로그 디자인", "영어 홈페이지 번역", "회계 프로그램 개발"
        };
        
        String[] contents = {
            "스타트업을 위한 로고 디자인과 전체적인 브랜딩 가이드라인을 제작해주실 분을 찾습니다.",
            "React와 Node.js를 활용한 반응형 웹사이트 개발 프로젝트입니다.",
            "3분 내외의 제품 소개 영상 편집 및 모션그래픽 작업을 의뢰합니다.",
            "iOS/Android 앱의 전체적인 UI/UX 디자인과 프로토타입 제작을 의뢰합니다.",
            "IT 기술 관련 문서 50페이지 분량의 영어를 한국어로 번역해주실 분을 찾습니다.",
            "사업 파트너십 계약서 검토와 관련 법무 자문이 필요합니다.",
            "브랜드 소개 및 제품 설명을 위한 마케팅 콘텐츠 작성이 필요합니다.",
            "매출 데이터 분석 및 시각적 차트 제작을 의뢰합니다.",
            "고객 상담용 AI 챗봇 개발 프로젝트입니다.",
            "신제품의 3D 모델링 및 렌더링 작업을 의뢰합니다.",
            "의류 쇼핑몰을 위한 온라인 스토어 제작이 필요합니다.",
            "유튜브 채널 운영을 위한 영상 편집 및 썸네일 제작을 의뢰합니다.",
            "20평 규모의 카페 인테리어 컨셉 디자인이 필요합니다.",
            "모바일 앱을 위한 직관적이고 매력적인 아이콘 디자인을 의뢰합니다.",
            "해외 수출용 계약서 영문 번역 및 법적 검토가 필요합니다.",
            "개인사업자 종합소득세 신고 대행을 부탁드립니다.",
            "소셜미디어를 활용한 브랜드 마케팅 전략 수립이 필요합니다.",
            "고객 데이터 분석을 통한 마케팅 인사이트 도출을 의뢰합니다.",
            "모바일 퍼즐 게임 개발 프로젝트입니다.",
            "결혼식 하이라이트 영상 제작을 의뢰합니다.",
            "기존 로고의 현대적 리뉴얼 작업이 필요합니다.",
            "구글 검색 노출 향상을 위한 SEO 최적화 작업을 의뢰합니다.",
            "고급스러운 느낌의 명함 디자인이 필요합니다.",
            "투자 유치용 프레젠테이션 제작을 의뢰합니다.",
            "소프트웨어 특허 출원을 위한 명세서 작성이 필요합니다.",
            "네이버, 구글 광고 운영 및 최적화를 의뢰합니다.",
            "캐주얼 모바일 게임 개발 프로젝트입니다.",
            "제품 브로슈어 및 카탈로그 디자인이 필요합니다.",
            "회사 홈페이지의 영문 버전 번역 작업을 의뢰합니다.",
            "소규모 기업용 회계 관리 프로그램 개발이 필요합니다."
        };
        
        String[] categories = {
            "디자인", "프로그래밍", "영상편집", "디자인", "번역/통역", "세무/법무/노무", "디자인", "프로그래밍", "프로그래밍", "디자인",
            "프로그래밍", "영상편집", "디자인", "디자인", "번역/통역", "세무/법무/노무", "프로그래밍", "프로그래밍", "디자인", "디자인",
            "디자인", "프로그래밍", "디자인", "디자인", "세무/법무/노무", "프로그래밍", "프로그래밍", "디자인", "번역/통역", "프로그래밍"
        };
        
        WorkRequestEntity.Status[] statuses = {
            WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.IN_PROGRESS, WorkRequestEntity.Status.DONE,
            WorkRequestEntity.Status.WAIT, WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.IN_PROGRESS,
            WorkRequestEntity.Status.DONE, WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.WAIT,
            WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.EXPIRED, WorkRequestEntity.Status.CANCELED
        };
        
        // AD 포인트 높은 인덱스를 미리 정의 (4개만 500점 이상)
        Set<Integer> highAdPointIndices = Set.of(0, 5, 10, 15); // 첫 번째, 여섯 번째, 열한 번째, 열여섯 번째
        
        for (int i = 0; i < titles.length; i++) {
            WorkRequestEntity workRequest = new WorkRequestEntity();
            workRequest.setTitle(titles[i]);
            workRequest.setContent(contents[i % contents.length]);
            workRequest.setCategory(categories[i % categories.length]);
            workRequest.setPrice(100000 + random.nextInt(2000000)); // 10만원~210만원
            workRequest.setStatus(statuses[i % statuses.length]);
            workRequest.setType(random.nextBoolean() ? WorkRequestEntity.Type.FORMAL : WorkRequestEntity.Type.INFORMAL);
            
            // 작업 요청 생성자 설정 (회원 중에서 랜덤 선택)
            Member requestCreator = members.get(random.nextInt(members.size()));
            workRequest.setCreatedBy(requestCreator.getEmail().getValue()); // BaseEntity.java에 세터 추가해서 사용하겠습니다.
            
            // AD 포인트 설정: 4개만 500점 이상, 나머지는 500 미만
            if (highAdPointIndices.contains(i)) {
                workRequest.setAdPoint(500 + random.nextInt(500)); // 500~999점
            } else {
                workRequest.setAdPoint(random.nextInt(500)); // 0~499점
            }
            
            // 일부 의뢰에 전문가 할당
            if (random.nextInt(3) != 0 && !expertProfiles.isEmpty()) {
                workRequest.setExpert(expertProfiles.get(random.nextInt(expertProfiles.size())).getMemberId());
            }
            
            // 파일 첨부 시뮬레이션 (50% 확률)
            if (random.nextBoolean()) {
                String[] fileTypes = {"pdf", "docx", "pptx", "xlsx", "zip", "jpg", "png"};
                String fileType = fileTypes[random.nextInt(fileTypes.length)];
                workRequest.setFileName("요구사항_" + (i + 1) + "." + fileType);
                workRequest.setFileUrl("/uploads/requirements/" + (i + 1) + "." + fileType);
            }
            
            // 완료된 의뢰에 결과물 파일 추가
            if (workRequest.getStatus() == WorkRequestEntity.Status.DONE) {
                String[] resultTypes = {"zip", "pdf", "psd", "ai", "mp4", "mov"};
                String resultType = resultTypes[random.nextInt(resultTypes.length)];
                workRequest.setResultFileName("결과물_" + (i + 1) + "." + resultType);
                workRequest.setResultFileUrl("/uploads/results/" + (i + 1) + "." + resultType);
            }
            
            workRequests.add(workRequestRepository.save(workRequest));
        }
        
        return workRequests;
    }
    // 5. 포인트/크레딧 생성 (더 다양하고 현실적으로)
    private void createMoneyData(List<Member> members) {
        for (Member member : members) {
            // 크레딧 충전 내역 (1-5회)
            int creditCount = 1 + random.nextInt(5);
            for (int i = 0; i < creditCount; i++) {
                Money credit = Money.builder()
                    .member(member)
                    .moneyType(MoneyType.CREDIT)
                    .price(50000 + random.nextInt(500000)) // 5만원~55만원
                    .build();
                moneyRepository.save(credit);
            }
            
            // 포인트 적립 내역 (0-3회)
            int pointCount = random.nextInt(4);
            for (int i = 0; i < pointCount; i++) {
                Money point = Money.builder()
                    .member(member)
                    .moneyType(MoneyType.POINT)
                    .price(1000 + random.nextInt(20000)) // 1천원~2만원
                    .build();
                moneyRepository.save(point);
            }
            
            // 일부 사용자는 크레딧 사용 내역도 추가 (음수)
            if (random.nextInt(3) == 0) {
                Money usage = Money.builder()
                    .member(member)
                    .moneyType(MoneyType.CREDIT)
                    .price(-(10000 + random.nextInt(100000))) // -1만원~-11만원
                    .build();
                moneyRepository.save(usage);
            }
        }
    }
    
    // Helper methods for expert profile data (더 다양하게)
    private String getIntroMessage(int index) {
        String[] intros = {
            "안녕하세요! 5년 경력의 웹 개발자입니다. 고객의 요구사항을 정확히 파악하여 최고 품질의 결과물을 제공해드립니다.",
            "UI/UX 디자인 전문가입니다. 사용자 중심의 직관적인 디자인을 만들어드립니다.",
            "10년 경력의 그래픽 디자이너입니다. 브랜딩부터 인쇄물까지 모든 디자인 작업을 도와드립니다.",
            "영상 편집 및 모션그래픽 전문가입니다. 창의적이고 임팩트 있는 영상을 제작해드립니다.",
            "다국어 번역 전문가입니다. 정확하고 자연스러운 번역 서비스를 제공합니다.",
            "법무 및 회계 전문가입니다. 기업의 법무/회계 업무를 안전하게 처리해드립니다.",
            "마케팅 기획 전문가입니다. 효과적인 마케팅 전략을 수립해드립니다.",
            "데이터 분석 및 AI 개발 전문가입니다. 데이터 기반의 인사이트를 제공합니다.",
            "모바일 앱 개발 전문가입니다. iOS와 Android 모두 가능하며, 사용자 경험을 최우선으로 생각합니다.",
            "풀스택 개발자로서 프론트엔드부터 백엔드까지 모든 개발 업무를 담당할 수 있습니다.",
            "브랜드 아이덴티티 디자인을 전문으로 하며, 기업의 가치를 시각적으로 표현하는 것이 저의 특기입니다.",
            "영상 제작 전문가로 기획부터 촬영, 편집까지 원스톱 서비스를 제공합니다.",
            "번역뿐만 아니라 현지화까지 고려한 전문적인 언어 서비스를 제공합니다.",
            "스타트업부터 대기업까지 다양한 규모의 법무 업무 경험이 있습니다.",
            "디지털 마케팅 전문가로서 온라인 마케팅 전략 수립과 실행을 도와드립니다.",
            "게임 개발 전문가입니다. Unity와 Unreal Engine을 활용한 다양한 게임 개발 경험이 있습니다.",
            "3D 모델링 및 애니메이션 전문가입니다. 영화, 게임, 광고 등 다양한 분야의 작업이 가능합니다.",
            "블록체인 개발 전문가입니다. 스마트 컨트랙트 개발과 DApp 구축 경험이 풍부합니다.",
            "클라우드 아키텍처 전문가입니다. AWS, Azure, GCP를 활용한 인프라 설계와 구축을 도와드립니다.",
            "인공지능 및 머신러닝 엔지니어입니다. 실무에서 활용 가능한 AI 솔루션을 개발합니다.",
            "보안 전문가입니다. 웹/앱 보안 진단과 보안 솔루션 구축을 전문으로 합니다.",
            "DevOps 엔지니어로서 CI/CD 파이프라인 구축과 인프라 자동화를 도와드립니다.",
            "전자상거래 개발 전문가입니다. 쇼핑몰 구축부터 결제 시스템까지 통합 솔루션을 제공합니다.",
            "AR/VR 콘텐츠 개발자입니다. 메타버스와 가상현실 콘텐츠 제작 경험이 풍부합니다."
        };
        return intros[index % intros.length];
    }
    
    private String getPortfolioDescription(int index) {
        String[] portfolios = {
            "• 대기업 공식 홈페이지 3건 개발\n• 스타트업 MVP 서비스 5건 구축\n• React, Vue.js, Node.js 전문",
            "• 모바일 앱 UI/UX 디자인 20건\n• 웹서비스 리뉴얼 프로젝트 10건\n• Figma, Sketch, Adobe XD 활용",
            "• 브랜드 아이덴티티 디자인 50건\n• 로고 디자인 100건 이상\n• 인쇄물 디자인 다수",
            "• 기업 홍보영상 제작 30건\n• 유튜브 채널 편집 200편 이상\n• After Effects, Premiere Pro 전문",
            "• 기술문서 번역 500페이지 이상\n• 비즈니스 통역 경험 100회 이상\n• 영어, 중국어, 일본어 가능",
            "• 중소기업 법무 자문 50건\n• 계약서 검토 및 작성 200건\n• 회계 업무 처리 경험 풍부",
            "• 브랜드 마케팅 전략 수립 20건\n• SNS 마케팅 캠페인 기획 50건\n• 성과 분석 및 개선 전문",
            "• 데이터 분석 프로젝트 30건\n• 머신러닝 모델 개발 10건\n• Python, R, SQL 전문",
            "• iOS 앱 개발 15건\n• Android 앱 개발 12건\n• React Native, Flutter 활용",
            "• 웹 풀스택 개발 경험 8년\n• 클라우드 인프라 구축\n• AWS, Docker, Kubernetes 전문",
            "• 중소기업 브랜딩 프로젝트 40건\n• CI/BI 디자인 전문\n• 브랜드 가이드라인 제작",
            "• 드라마, 영화 편집 참여\n• 광고 영상 제작 100편 이상\n• DaVinci Resolve, Avid 활용",
            "• IT 특허 번역 전문\n• 학술논문 번역 200편 이상\n• 의료, 법률 분야 번역 가능",
            "• 상장기업 법무팀 근무 경험\n• M&A, 투자 계약 전문\n• 국제계약 검토 가능",
            "• 성장 마케팅 전문가\n• 스타트업 마케팅 컨설팅\n• 퍼포먼스 마케팅 ROI 300% 달성",
            "• Unity 게임 개발 프로젝트 25건\n• 모바일 게임 출시 10건\n• VR/AR 게임 개발 경험",
            "• 3D 캐릭터 모델링 100건 이상\n• 영화 VFX 작업 참여\n• Maya, Blender, ZBrush 전문",
            "• 블록체인 프로젝트 개발 15건\n• NFT 마켓플레이스 구축\n• Solidity, Web3.js 전문",
            "• 클라우드 마이그레이션 프로젝트 40건\n• 쿠버네티스 클러스터 구축\n• AWS Solutions Architect 자격 보유",
            "• AI 모델 개발 및 배포 20건\n• 컴퓨터 비전 프로젝트 10건\n• TensorFlow, PyTorch 전문",
            "• 모의해킹 및 보안 진단 50건\n• 보안 솔루션 구축 프로젝트 20건\n• CISSP, CEH 자격 보유",
            "• DevOps 파이프라인 구축 30건\n• 인프라 자동화 프로젝트 25건\n• Jenkins, GitLab CI/CD, Terraform 전문",
            "• 대형 쇼핑몰 구축 프로젝트 15건\n• 결제 시스템 연동 50건 이상\n• 옴니채널 커머스 솔루션 구축"
        };
        return portfolios[index % portfolios.length];
    }
    
    private String getSchool(int index) {
        String[] schools = {
            "서울대학교", "연세대학교", "고려대학교", "성균관대학교", "한양대학교",
            "중앙대학교", "경희대학교", "이화여자대학교", "서강대학교", "홍익대학교",
            "건국대학교", "동국대학교", "숭실대학교", "국민대학교", "세종대학교"
        };
        return schools[index % schools.length];
    }
    
    private String getMajor(int index) {
        String[] majors = {
            "컴퓨터공학과", "시각디자인학과", "산업디자인학과", "영상학과", "영어영문학과",
            "법학과", "경영학과", "통계학과", "수학과", "전자공학과",
            "정보시스템학과", "멀티미디어학과", "국제통상학과", "회계학과", "광고홍보학과",
            "소프트웨어학과", "게임공학과", "정보보안학과", "인공지능학과", "데이터사이언스학과"
        };
        return majors[index % majors.length];
    }
    
    // 6. 데이터 정합성 검증 및 수정
    private void validateAndFixDataIntegrity(List<Member> members, List<Specialization> specializations) {
        log.info("데이터 정합성 검증을 시작합니다...");
        
        int fixedCount = 0;
        int expertWithoutProfile = 0;
        
        for (Member member : members) {
            // Role이 EXPERT인데 ExpertProfile이 없는 경우 체크
            if (member.getRole() == Role.EXPERT) {
                boolean hasProfile = expertProfileRepository.existsById(member.getId());
                
                if (!hasProfile) {
                    expertWithoutProfile++;
                    log.warn("Role.EXPERT이지만 프로필이 없는 회원 발견: {} (ID: {})", member.getUsername(), member.getId());
                    
                    try {
                        // ExpertProfile 생성
                        ExpertProfile expertProfile = ExpertProfile.createExpertProfile(
                            member,
                            getIntroMessage(expertWithoutProfile),
                            getPortfolioDescription(expertWithoutProfile),
                            getSchool(expertWithoutProfile),
                            getMajor(expertWithoutProfile),
                            LocalDate.now().minusYears(1 + random.nextInt(8)),
                            300000 + random.nextInt(700000), // 30만원~100만원
                            random.nextBoolean()
                        );
                        
                        // 의뢰 완료 경험 추가
                        int completedCount = random.nextInt(16);
                        for (int i = 0; i < completedCount; i++) {
                            expertProfile.incrementCompletedRequestCount();
                        }
                        
                        expertProfile = expertProfileRepository.save(expertProfile);
                        
                        // 전문 분야 1-3개 랜덤 할당
                        int specCount = 1 + random.nextInt(3);
                        Set<Integer> usedIndices = new HashSet<>();
                        
                        for (int i = 0; i < specCount; i++) {
                            int specIndex;
                            do {
                                specIndex = random.nextInt(specializations.size());
                            } while (usedIndices.contains(specIndex));
                            usedIndices.add(specIndex);
                            
                            ExpertSpecialization expertSpec = ExpertSpecialization.createExpertSpecialization(
                                expertProfile,
                                specializations.get(specIndex).getId()
                            );
                            expertProfile.addExpertSpecialization(expertSpec);
                            expertSpecializationRepository.save(expertSpec);
                        }
                        
                        fixedCount++;
                        log.info("전문가 프로필 복구 완료: {} (전문분야 {}개)", member.getUsername(), specCount);
                        
                    } catch (Exception e) {
                        log.error("전문가 프로필 복구 실패 - 회원: {}, 오류: {}", member.getUsername(), e.getMessage());
                    }
                }
            }
        }
        
        // Role.USER인데 ExpertProfile을 가진 경우 체크 (반대 경우)
        List<ExpertProfile> allProfiles = expertProfileRepository.findAll();
        for (ExpertProfile profile : allProfiles) {
            Member member = profile.getMember();
            if (member.getRole() != Role.EXPERT && member.getRole() != Role.ADMIN) {
                log.warn("Role.USER이지만 전문가 프로필을 가진 회원 발견: {} (ID: {})", member.getUsername(), member.getId());
                
                try {
                    // Role을 EXPERT로 변경
                    member.changeToExpert();
                    memberRepository.save(member);
                    log.info("회원 {} 의 Role을 EXPERT로 변경했습니다.", member.getUsername());
                } catch (Exception e) {
                    log.error("Role 변경 실패 - 회원: {}, 오류: {}", member.getUsername(), e.getMessage());
                }
            }
        }
        
        if (expertWithoutProfile > 0) {
            log.info("데이터 정합성 검증 완료: {}명의 전문가 프로필 복구", fixedCount);
        } else {
            log.info("데이터 정합성 검증 완료: 모든 데이터가 정상입니다.");
        }
        
        // 최종 통계 출력
        long totalMembers = memberRepository.count();
        long expertMembers = memberRepository.findAll().stream()
            .filter(m -> m.getRole() == Role.EXPERT)
            .count();
        long totalProfiles = expertProfileRepository.count();
        long totalSpecializations = specializationRepository.count();
        
        log.info("=== 최종 데이터 통계 ===");
        log.info("전체 회원 수: {}", totalMembers);
        log.info("전문가 회원 수 (Role.EXPERT): {}", expertMembers);
        log.info("전문가 프로필 수: {}", totalProfiles);
        log.info("전문 분야 수: {}", totalSpecializations);
        log.info("====================");
    }
}