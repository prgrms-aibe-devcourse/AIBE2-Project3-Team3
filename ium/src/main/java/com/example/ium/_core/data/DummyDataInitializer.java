package com.example.ium._core.data;

import com.example.ium.member.domain.model.*;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.model.expert.ExpertSpecialization;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.member.domain.repository.ExpertSpecializationJPARepository;
import com.example.ium.member.domain.repository.MemberJPARepository;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataInitializer {
    
    // Repository 의존성 주입
    private final MemberJPARepository memberRepository;
    private final ExpertProfileJPARepository expertProfileRepository;
    private final ExpertSpecializationJPARepository expertSpecializationRepository;
    private final SpecializationJPARepository specializationRepository;
    private final MoneyRepository moneyRepository;
    private final WorkRequestRepository workRequestRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final Random random = new Random();
    
    @EventListener(ApplicationReadyEvent.class) // @PostConstruct 대신 ApplicationReadyEvent 사용
    @Transactional // 트랜잭션 제대로 작동하도록
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
        
        // 3. 전문가 프로필 생성 (모든 전문가에 대해 다양한 프로필 생성)
        List<ExpertProfile> expertProfiles = createExpertProfiles(members, specializations);
        
        // 4. 작업 의뢰 생성
        List<WorkRequestEntity> workRequests = createWorkRequests(members);
        
        // 5. 포인트/크레딧 데이터 생성
        createMoneyData(members);
        
        log.info("더미 데이터 초기화가 완료되었습니다!");
        log.info("생성된 데이터: 회원 {}, 전문가프로필 {}, 전문분야 {}, 작업의뢰 {}", 
                members.size(), expertProfiles.size(), specializations.size(), workRequests.size());
    }
    
    // 1. 전문 분야 생성 (IumApplication.java와 동일한 5개 카테고리)
    private List<Specialization> createSpecializations() {
        // 이미 존재하는 specialization 건너뛰기
        if (specializationRepository.count() > 0) {
            log.info("전문분야 데이터가 이미 존재합니다. 생성을 건너뛉니다.");
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
    
    // 2. 회원 생성
    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        
        // 전체 32명을 전문가로 생성
        String[] usernames = {
            "USERNAME1", "USERNAME2", // 기본 테스트 계정 2개 추가
            "코딩마스터", "디자인킹", "영상편집러", "번역고수", "개발자123", "크리에이터", "마케터Pro", "데이터분석가", "풀스택개발자", "UI디자이너",
            "모션그래픽", "웹개발자", "앱개발러", "로고마스터", "번역전문가", "영상제작자", "프론트엔드", "백엔드개발", "그래픽디자인", "콘텐츠라이터",
            "SEO전문가", "광고기획자", "브랜딩전문", "3D모델러", "게임개발자", "챗봇개발", "데이터과학자", "AI개발자", "블록체인개발", "스타트업CEO"
        };
        
        String[] emailPrefixes = {
            "user1", "user2", // 기본 테스트 계정 이메일
            "coding_master", "design_king", "video_editor", "translator", "developer123", "creator", "marketer_pro", "data_analyst", "fullstack_dev", "ui_designer",
            "motion_graphic", "web_developer", "app_developer", "logo_master", "translation_pro", "video_creator", "frontend_dev", "backend_dev", "graphic_design", "content_writer",
            "seo_expert", "ad_planner", "branding_pro", "3d_modeler", "game_developer", "chatbot_dev", "data_scientist", "ai_developer", "blockchain_dev", "startup_ceo"
        };
        
        // 모든 회원을 전문가로 생성
        for (int i = 0; i < usernames.length; i++) {
            Member member = Member.createExpert(
                usernames[i],
                Email.of(emailPrefixes[i] + "@test.com"),
                Password.encode("test1", passwordEncoder)
            );
            log.debug("전문가 회원 생성: {}", usernames[i]);
            
            members.add(memberRepository.save(member));
        }
        
        // 관리자 계정
        Member admin = Member.builder()
            .username("ADMIN")
            .email(Email.of("admin@test.com"))
            .password(Password.encode("test1", passwordEncoder))
            .role(Role.ADMIN)
            .status(Status.ACTIVE)
            .build();
        members.add(memberRepository.save(admin));
        
        return members;
    }
    
    // 3. 작업 의뢰 생성
    private List<WorkRequestEntity> createWorkRequests(List<Member> members) {
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
            workRequest.setCreatedBy(requestCreator.getEmail().getValue());
            
            // AD 포인트 설정: 4개만 500점 이상, 나머지는 500 미만
            if (highAdPointIndices.contains(i)) {
                workRequest.setAdPoint(500 + random.nextInt(500)); // 500~999점
            } else {
                workRequest.setAdPoint(random.nextInt(500)); // 0~499점
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
    
    // 3. 전문가 프로필 생성 - 모든 전문가에 대해 다양한 프로필 생성
    private List<ExpertProfile> createExpertProfiles(List<Member> members, List<Specialization> specializations) {
        List<ExpertProfile> expertProfiles = new ArrayList<>();
        
        // EXPERT 역할을 가진 회원들만 필터링
        List<Member> experts = members.stream()
                .filter(member -> member.getRole() == Role.EXPERT)
                .toList();
        
        log.info("전체 전문가 수: {}", experts.size());
        
        if (experts.isEmpty()) {
            log.warn("전문가가 없습니다!");
            return expertProfiles;
        }
        
        // 전문가별 프로필 데이터 배열들
        String[] introduceMessages = {
            "안녕하세요! 5년차 프론트엔드 개발자입니다. React, Vue.js를 활용한 반응형 웹 개발을 전문으로 합니다.",
            "창의적이고 실용적인 디자인으로 브랜드의 가치를 높여드립니다. UI/UX부터 브랜딩까지 토탈 디자인 서비스를 제공합니다.",
            "10년 경력의 영상 편집 전문가입니다. 기업 홍보영상부터 유튜브 콘텐츠까지 다양한 영상 제작 경험이 있습니다.",
            "공인회계사 자격을 보유한 세무 전문가입니다. 기업 회계부터 개인 세무까지 정확하고 신속한 업무 처리를 약속드립니다.",
            "영어, 일본어, 중국어 번역이 가능한 다국어 전문가입니다. 비즈니스 문서부터 기술 문서까지 정확한 번역을 제공합니다.",
            "백엔드 개발 전문가로 Spring Boot, Node.js를 활용한 서버 개발과 API 설계를 전문으로 합니다.",
            "모바일 UI/UX 디자인 전문가입니다. 사용자 중심의 직관적인 인터페이스 설계로 앱의 사용성을 높여드립니다.",
            "유튜브 크리에이터를 위한 영상 편집과 썸네일 제작을 전문으로 하는 편집 전문가입니다.",
            "변호사 자격을 보유한 법무 전문가입니다. 계약서 검토부터 법률 자문까지 전문적인 서비스를 제공합니다.",
            "비즈니스 영어 번역과 통역을 전문으로 하는 언어 전문가입니다. 국제 비즈니스 경험이 풍부합니다.",
            "풀스택 개발자로 프론트엔드부터 백엔드까지 원스톱 웹 개발 서비스를 제공합니다.",
            "브랜드 아이덴티티 디자인 전문가입니다. 로고부터 브랜딩 가이드라인까지 체계적인 브랜드 구축을 도와드립니다.",
            "모션 그래픽과 애니메이션을 전문으로 하는 영상 크리에이터입니다. After Effects를 활용한 고퀄리티 영상을 제작합니다.",
            "노무사 자격을 보유한 인사노무 전문가입니다. 취업규칙 작성부터 노무 상담까지 전문적인 서비스를 제공합니다.",
            "기술 문서 번역 전문가입니다. IT, 의료, 특허 등 전문 분야 번역 경험이 풍부합니다.",
            "모바일 앱 개발 전문가로 iOS, Android 네이티브 앱 개발을 전문으로 합니다.",
            "패키지 디자인과 인쇄물 디자인을 전문으로 하는 그래픽 디자이너입니다.",
            "드론 촬영과 항공영상 편집을 전문으로 하는 영상 제작자입니다.",
            "세무사 자격을 보유한 세무 대리인입니다. 법인세 신고부터 세무 조사 대응까지 전문적인 업무를 수행합니다.",
            "의료진 대상 의학 번역과 논문 번역을 전문으로 하는 전문 번역가입니다.",
            "AI/ML 엔지니어로 데이터 분석부터 머신러닝 모델 개발까지 전문적인 서비스를 제공합니다.",
            "웹 디자인과 랜딩페이지 제작을 전문으로 하는 디지털 디자이너입니다.",
            "웨딩 영상과 이벤트 영상 촬영 및 편집을 전문으로 하는 영상 작가입니다.",
            "지식재산권 전문 변리사로 특허 출원부터 상표 등록까지 전문적인 서비스를 제공합니다.",
            "게임 현지화와 다국어 번역을 전문으로 하는 게임 번역 전문가입니다.",
            "블록체인 개발자로 DeFi, NFT 등 Web3 개발을 전문으로 합니다.",
            "3D 모델링과 제품 렌더링을 전문으로 하는 3D 디자이너입니다.",
            "라이브 스트리밍과 실시간 영상 편집을 전문으로 하는 방송 엔지니어입니다.",
            "국제계약서와 법률 문서 번역을 전문으로 하는 법무 번역가입니다.",
            "스타트업 CTO로 기술 전략부터 개발팀 빌딩까지 토탈 기술 컨설팅을 제공합니다."
        };
        
        String[] portfolioDescriptions = {
            "React 기반 SPA 개발 프로젝트 15건, TypeScript 활용 대규모 프론트엔드 아키텍처 설계 경험",
            "스타트업 브랜드 아이덴티티 50개 이상 제작, 삼성전자, LG전자 등 대기업 프로젝트 참여 경험",
            "기업 홍보영상 100편 이상 제작, 넷플릭스 오리지널 시리즈 편집 참여 경험",
            "상장회사 회계감사 5년, 중소기업 세무대리 200건 이상 처리 경험",
            "국제회의 동시통역 50회 이상, 대기업 해외사업 번역 프로젝트 다수 참여",
            "대용량 트래픽 처리 서버 개발, MSA 아키텍처 설계 및 구축 경험",
            "앱스토어 1위 앱 UI/UX 디자인, 사용자 경험 개선을 통한 DAU 300% 증가 사례",
            "구독자 100만 유튜버 전담 편집, 바이럴 영상 제작으로 조회수 1000만 달성 경험",
            "M&A 법무실사 20건 이상, 스타트업 투자계약서 검토 100건 이상 경험",
            "Fortune 500 기업 IR 자료 번역, G20 정상회의 통역 참여 경험",
            "e-커머스 플랫폼 풀스택 개발, 월 거래액 100억 규모 서비스 구축 경험",
            "코카콜라, 나이키 등 글로벌 브랜드 리브랜딩 프로젝트 참여",
            "드라마 메인타이틀 모션그래픽 제작, 광고 애니메이션 500편 이상 제작",
            "대기업 노사분쟁 해결 30건, 근로계약서 표준안 작성 200건 이상",
            "삼성바이오로직스 기술문서 번역, 코로나19 백신 허가 문서 번역 참여",
            "카카오뱅크, 토스 등 핀테크 앱 개발 참여, 앱스토어 TOP 10 앱 개발 경험",
            "CJ제일제당 제품 패키지 디자인, 신세계백화점 VIP 카드 디자인 등",
            "부산국제영화제 공식 드론촬영, 평창올림픽 항공영상 제작 참여",
            "코스피 상장회사 세무조사 대응 15건, 국세청 세무상담 위원 활동",
            "서울대병원 의학논문 번역 100편, 식약처 의료기기 허가 문서 번역",
            "네이버 검색 랭킹 알고리즘 개발 참여, 카카오 추천시스템 구축 경험",
            "현대자동차 공식 홈페이지 리뉴얼, 배달의민족 랜딩페이지 제작",
            "tvN 예능프로그램 영상 제작, 삼성전자 제품 론칭 영상 제작",
            "네이버 특허 출원 200건 이상 담당, 애플 vs 삼성 특허분쟁 참여",
            "리그오브레전드 한국어화 참여, 배틀그라운드 다국어 번역 프로젝트",
            "업비트 거래소 스마트컨트랙트 개발, OpenSea NFT 마켓플레이스 개발 참여",
            "현대모비스 자동차 부품 3D 모델링, 아이폰 제품 렌더링 작업",
            "아프리카TV 실시간 방송 기술 개발, 유튜브 라이브 스트리밍 솔루션 구축",
            "대법원 판례집 번역, 헌법재판소 영문 판결문 번역 프로젝트",
            "쿠팡 CTO 어드바이저, 당근마켓 기술전략 컨설팅, 토스 아키텍처 리뷰"
        };
        
        String[] schools = {
            "서울대학교", "연세대학교", "고려대학교", "KAIST", "포스텍", "성균관대학교", "한양대학교", "이화여자대학교",
            "서강대학교", "중앙대학교", "경희대학교", "한국외국어대학교", "건국대학교", "동국대학교", "홍익대학교",
            "숭실대학교", "국민대학교", "세종대학교", "광운대학교", "단국대학교", "명지대학교", "가천대학교",
            "인하대학교", "아주대학교", "경기대학교", "한국항공대학교", "서울과학기술대학교", "상명대학교",
            "덕성여자대학교", "숙명여자대학교"
        };
        
        String[] majors = {
            "컴퓨터공학과", "시각디자인학과", "영상제작학과", "경영학과", "영어영문학과",
            "소프트웨어학과", "산업디자인학과", "미디어커뮤니케이션학과", "법학과", "국제통상학과",
            "정보통신공학과", "광고홍보학과", "방송영상학과", "행정학과", "중국어중문학과",
            "전자공학과", "제품디자인학과", "디지털미디어학과", "경제학과", "일본어일문학과",
            "데이터사이언스학과", "패션디자인학과", "영화학과", "회계학과", "통번역학과",
            "AI융합학과", "커뮤니케이션디자인학과", "콘텐츠학과", "세무학과", "국제학과"
        };
        
        // 전문분야별 인덱스 범위 정의
        int[] designRange = {1, 6, 11, 16, 21, 26}; // 디자인 관련
        int[] programmingRange = {0, 5, 10, 15, 20, 25}; // 프로그래밍 관련
        int[] videoRange = {2, 7, 12, 17, 22, 27}; // 영상편집 관련
        int[] legalRange = {3, 8, 13, 18, 23, 28}; // 세무/법무/노무 관련
        int[] translationRange = {4, 9, 14, 19, 24, 29}; // 번역/통역 관련
        
        // 모든 전문가에 대해 프로필 생성
        for (int i = 0; i < experts.size(); i++) {
            Member expert = experts.get(i);
            
            try {
                // Member 다시 조회 (영속성 컨텍스트에서 관리되는 엔티티 사용)
                Member managedExpert = memberRepository.findById(expert.getId())
                        .orElseThrow(() -> new RuntimeException("Member not found: " + expert.getId()));
                
                // 인덱스에 따른 데이터 설정
                int dataIndex = i % introduceMessages.length;
                LocalDate careerStartDate = LocalDate.now().minusYears(2 + random.nextInt(8)); // 2-9년 경력
                int salary = 200000 + (random.nextInt(18000) * 100); // 20만원-200만원, 100원 단위
                boolean negoYn = random.nextBoolean();
                
                // ExpertProfile 생성
                ExpertProfile expertProfile = ExpertProfile.createExpertProfile(
                    managedExpert,
                    introduceMessages[dataIndex],
                    portfolioDescriptions[dataIndex],
                    schools[dataIndex % schools.length],
                    majors[dataIndex % majors.length],
                    careerStartDate,
                    salary,
                    negoYn
                );
                
                // ExpertProfile 저장
                ExpertProfile savedExpertProfile = expertProfileRepository.save(expertProfile);
                expertProfiles.add(savedExpertProfile);
                
                // 전문분야 할당 (각 전문가마다 1-2개의 전문분야)
                assignSpecializations(savedExpertProfile, specializations, dataIndex);
                
                log.info("✅ 전문가 프로필 생성 성공: {} (ID: {})", 
                        managedExpert.getUsername(), savedExpertProfile.getMemberId());
                
            } catch (Exception e) {
                log.error("❌ 전문가 프로필 생성 실패: {} - {}", expert.getUsername(), e.getMessage());
                log.error("상세 오류:", e);
            }
        }
        
        return expertProfiles;
    }
    
    // 전문분야 할당 메소드
    private void assignSpecializations(ExpertProfile expertProfile, List<Specialization> specializations, int dataIndex) {
        // 인덱스에 따라 전문분야 결정
        Long primarySpecializationId;
        Long secondarySpecializationId = null;
        
        // 전문분야 매핑 (인덱스 % 5로 순환)
        switch (dataIndex % 5) {
            case 0: // 프로그래밍
                primarySpecializationId = findSpecializationIdByName(specializations, "프로그래밍");
                if (random.nextBoolean()) {
                    secondarySpecializationId = findSpecializationIdByName(specializations, "디자인");
                }
                break;
            case 1: // 디자인
                primarySpecializationId = findSpecializationIdByName(specializations, "디자인");
                if (random.nextBoolean()) {
                    secondarySpecializationId = findSpecializationIdByName(specializations, "영상편집");
                }
                break;
            case 2: // 영상편집
                primarySpecializationId = findSpecializationIdByName(specializations, "영상편집");
                if (random.nextBoolean()) {
                    secondarySpecializationId = findSpecializationIdByName(specializations, "디자인");
                }
                break;
            case 3: // 세무/법무/노무
                primarySpecializationId = findSpecializationIdByName(specializations, "세무/법무/노무");
                if (random.nextBoolean()) {
                    secondarySpecializationId = findSpecializationIdByName(specializations, "번역/통역");
                }
                break;
            default: // 번역/통역
                primarySpecializationId = findSpecializationIdByName(specializations, "번역/통역");
                if (random.nextBoolean()) {
                    secondarySpecializationId = findSpecializationIdByName(specializations, "세무/법무/노무");
                }
                break;
        }
        
        // 중복 체크: primary와 secondary가 같으면 secondary를 null로 설정
        if (primarySpecializationId != null && primarySpecializationId.equals(secondarySpecializationId)) {
            secondarySpecializationId = null;
            log.debug("중복된 전문분야 감지, secondary 제거: expertId={}, specializationId={}", 
                    expertProfile.getMemberId(), primarySpecializationId);
        }
        
        // 주 전문분야 할당
        if (primarySpecializationId != null) {
            // 이미 할당된 전문분야인지 체크
            boolean alreadyAssigned = false;
            for (ExpertSpecialization es : expertProfile.getExpertSpecialization()) {
                if (es.getId().getSpecializationId().equals(primarySpecializationId)) {
                    alreadyAssigned = true;
                    break;
                }
            }
            
            if (!alreadyAssigned) {
                try {
                    ExpertSpecialization primaryExpertSpec = ExpertSpecialization.createExpertSpecialization(
                        expertProfile, primarySpecializationId);
                    expertProfile.addExpertSpecialization(primaryExpertSpec);
                    // cascade=PERSIST로 인해 ExpertProfile 저장 시 자동으로 ExpertSpecialization도 저장됨
                    log.debug("주 전문분야 할당 성공: expertId={}, specializationId={}", 
                            expertProfile.getMemberId(), primarySpecializationId);
                } catch (Exception e) {
                    log.error("주 전문분야 할당 실패: expertId={}, specializationId={}, error={}", 
                            expertProfile.getMemberId(), primarySpecializationId, e.getMessage());
                }
            } else {
                log.debug("이미 할당된 주 전문분야 건너뛴: expertId={}, specializationId={}", 
                        expertProfile.getMemberId(), primarySpecializationId);
            }
        }
        
        // 부 전문분야 할당 (선택적이고 중복이 아닌 경우만)
        if (secondarySpecializationId != null) {
            // 이미 할당된 전문분야인지 체크
            boolean alreadyAssigned = false;
            for (ExpertSpecialization es : expertProfile.getExpertSpecialization()) {
                if (es.getId().getSpecializationId().equals(secondarySpecializationId)) {
                    alreadyAssigned = true;
                    break;
                }
            }
            
            if (!alreadyAssigned) {
                try {
                    ExpertSpecialization secondaryExpertSpec = ExpertSpecialization.createExpertSpecialization(
                        expertProfile, secondarySpecializationId);
                    expertProfile.addExpertSpecialization(secondaryExpertSpec);
                    // cascade=PERSIST로 인해 ExpertProfile 저장 시 자동으로 ExpertSpecialization도 저장됨
                    log.debug("부 전문분야 할당 성공: expertId={}, specializationId={}", 
                            expertProfile.getMemberId(), secondarySpecializationId);
                } catch (Exception e) {
                    log.warn("부 전문분야 할당 실패 (무시함): expertId={}, specializationId={}, error={}", 
                            expertProfile.getMemberId(), secondarySpecializationId, e.getMessage());
                }
            } else {
                log.debug("이미 할당된 부 전문분야 건너뛴: expertId={}, specializationId={}", 
                        expertProfile.getMemberId(), secondarySpecializationId);
            }
        }
    }
    
    // 전문분야 이름으로 ID 찾기
    private Long findSpecializationIdByName(List<Specialization> specializations, String name) {
        return specializations.stream()
                .filter(spec -> spec.getSpecializationName().getValue().equals(name))
                .map(Specialization::getId)
                .findFirst()
                .orElse(null);
    }
    
    // 5. 포인트/크레딧 생성
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
}