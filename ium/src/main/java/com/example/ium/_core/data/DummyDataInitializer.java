package com.example.ium._core.data;

import com.example.ium.chat.domain.jpa.repository.ChatMemberJPARepository;
import com.example.ium.chat.domain.jpa.repository.ChatMessageJPARepository;
import com.example.ium.chat.domain.jpa.repository.ChatRoomJPARepository;
import com.example.ium.chat.domain.model.*;
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
import java.util.List;
import java.util.Random;

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
    private final ChatRoomJPARepository chatRoomRepository;
    private final ChatMessageJPARepository chatMessageRepository;
    private final ChatMemberJPARepository chatMemberRepository;
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
        
        try {
            // 1. 전문 분야 생성
            List<Specialization> specializations = createSpecializations();
            
            // 2. 회원 생성
            List<Member> members = createMembers();
            
            // 3. 전문가 프로필 생성
            List<ExpertProfile> expertProfiles = createExpertProfiles(members, specializations);
            
            // 4. 작업 의뢰 생성
            List<WorkRequestEntity> workRequests = createWorkRequests(members, expertProfiles);
            
            // 5. 채팅방 및 메시지 생성
            createChatRoomsAndMessages(members);
            
            // 6. 포인트/크레딧 생성 (더 다양하게)
            createMoneyData(members);
            
            log.info("더미 데이터 초기화가 완료되었습니다!");
            log.info("생성된 데이터: 회원 {}, 전문가 {}, 의뢰 {}, 전문분야 {}", 
                    members.size(), expertProfiles.size(), workRequests.size(), specializations.size());
            
        } catch (Exception e) {
            log.error("더미 데이터 초기화 중 오류 발생: ", e);
        }
    }
    
    // 1. 전문 분야 생성 (더 많이)
    private List<Specialization> createSpecializations() {
        List<Specialization> specializations = new ArrayList<>();
        
        String[] specializationNames = {
            // IT/개발
            "웹 개발", "모바일 앱 개발", "백엔드 개발", "프론트엔드 개발", "풀스택 개발",
            "AI/머신러닝", "데이터 분석", "블록체인", "게임 개발", "DevOps",
            
            // 디자인
            "UI/UX 디자인", "그래픽 디자인", "로고 디자인", "브랜드 디자인", "웹 디자인",
            "모바일 디자인", "인쇄물 디자인", "3D 디자인", "캐릭터 디자인", "일러스트",
            
            // 영상/미디어
            "영상 편집", "모션 그래픽", "애니메이션", "사진 촬영", "영상 제작",
            "유튜브 편집", "광고 영상", "웨딩 영상", "기업 홍보영상", "음향 편집",
            
            // 번역/언어
            "영한 번역", "한영 번역", "중국어 번역", "일본어 번역", "독일어 번역",
            "프랑스어 번역", "스페인어 번역", "러시아어 번역", "통역", "자막 번역",
            
            // 비즈니스/법무
            "법무 자문", "세무 회계", "노무 관리", "특허 출원", "계약서 작성",
            "사업계획서", "투자 제안서", "마케팅 기획", "경영 컨설팅", "IR 자료",
            
            // 콘텐츠/마케팅
            "콘텐츠 작성", "블로그 포스팅", "SNS 마케팅", "유튜브 기획", "카피라이팅",
            "SEO 최적화", "온라인 광고", "바이럴 마케팅", "브랜딩", "네이밍"
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
        
        // 일반 사용자들 (25명)
        String[] usernames = {
            "김철수", "이영희", "박민수", "최지은", "정현우", "강민정", "윤서준", "임소영", "오준혁", "한예린",
            "신동욱", "조미진", "배성훈", "서지혜", "홍길동", "문지원", "장준혁", "김나영", "이준호", "박소라",
            "최민준", "정유진", "강태우", "윤아름", "임지훈", "송미나", "유창현", "노지수", "백준서", "안채원"
        };
        
        String[] emailPrefixes = {
            "kimcs", "leeyh", "parkms", "choije", "junghw", "kangmj", "yunsj", "limsy", "ohjh", "hanyr",
            "sindw", "jomj", "baesh", "seojh", "hongkd", "moonjw", "jangjh", "kimny", "leejh", "parksr",
            "choimj", "jungyj", "kangtw", "yunar", "limjh", "songmn", "yuch", "nojs", "baekjs", "ancw"
        };
        
        for (int i = 0; i < usernames.length; i++) {
            Member member = Member.createMember(
                usernames[i],
                Email.of(emailPrefixes[i] + "@test.com"),
                Password.encode("password123", passwordEncoder)
            );
            members.add(memberRepository.save(member));
        }
        
        // 관리자 계정
        Member admin = Member.builder()
            .username("관리자")
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
        
        // 처음 15명을 전문가로 설정
        for (int i = 0; i < Math.min(15, members.size()); i++) {
            Member member = members.get(i);
            
            ExpertProfile expertProfile = ExpertProfile.createExpertProfile(
                member,
                getIntroMessage(i),
                getPortfolioDescription(i),
                getSchool(i),
                getMajor(i),
                LocalDate.now().minusYears(1 + random.nextInt(8)), // 1-8년 경력
                300000 + (i * 50000) + random.nextInt(200000), // 30만원~200만원 희망 연봉
                random.nextBoolean() // 협상 가능 여부 랜덤
            );
            
            expertProfile = expertProfileRepository.save(expertProfile);
            
            // 전문 분야 매핑 (각 전문가마다 1-4개의 전문분야)
            int specCount = 1 + random.nextInt(4);
            for (int j = 0; j < specCount; j++) {
                int specIndex = (i * 3 + j) % specializations.size();
                ExpertSpecialization expertSpecialization = ExpertSpecialization.createExpertSpecialization(
                    expertProfile, 
                    specializations.get(specIndex).getId()
                );
                expertProfile.addExpertSpecialization(expertSpecialization);
                expertSpecializationRepository.save(expertSpecialization);
            }
            
            // 일부 전문가는 의뢰 완료 경험 시뮬레이션
            for (int k = 0; k < random.nextInt(5); k++) {
                expertProfile.incrementCompletedRequestCount();
            }
            expertProfileRepository.save(expertProfile);
            
            expertProfiles.add(expertProfile);
        }
        
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
            "디자인", "프로그래밍", "영상편집", "디자인", "번역", "법무", "콘텐츠", "데이터분석", "프로그래밍", "3D모델링",
            "프로그래밍", "영상편집", "디자인", "디자인", "번역", "회계", "마케팅", "데이터분석", "프로그래밍", "영상편집",
            "디자인", "프로그래밍", "디자인", "디자인", "법무", "마케팅", "프로그래밍", "디자인", "번역", "프로그래밍"
        };
        
        WorkRequestEntity.Status[] statuses = {
            WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.IN_PROGRESS, WorkRequestEntity.Status.DONE,
            WorkRequestEntity.Status.WAIT, WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.IN_PROGRESS,
            WorkRequestEntity.Status.DONE, WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.WAIT,
            WorkRequestEntity.Status.OPEN, WorkRequestEntity.Status.EXPIRED, WorkRequestEntity.Status.CANCELED
        };
        
        for (int i = 0; i < titles.length; i++) {
            WorkRequestEntity workRequest = new WorkRequestEntity();
            workRequest.setTitle(titles[i]);
            workRequest.setContent(contents[i % contents.length]);
            workRequest.setCategory(categories[i % categories.length]);
            workRequest.setPrice(100000 + random.nextInt(2000000)); // 10만원~210만원
            workRequest.setStatus(statuses[i % statuses.length]);
            workRequest.setType(random.nextBoolean() ? WorkRequestEntity.Type.FORMAL : WorkRequestEntity.Type.INFORMAL);
            workRequest.setAdPoint(random.nextInt(1000));
            
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
    
    // 5. 채팅방 및 메시지 생성 (더 많이, 현실적으로)
    private void createChatRoomsAndMessages(List<Member> members) {
        // 채팅방 생성 (10개)
        for (int i = 0; i < 10; i++) {
            ChatRoom chatRoom = ChatRoom.builder()
                .roomName("채팅방_" + (i + 1))
                .build();
            chatRoom = chatRoomRepository.save(chatRoom);
            
            // 채팅방 멤버 추가 (2-3명)
            int memberCount = 2 + random.nextInt(2);
            List<Member> chatMembers = new ArrayList<>();
            
            for (int j = 0; j < memberCount; j++) {
                Member member = members.get((i + j) % members.size());
                if (!chatMembers.contains(member)) {
                    chatMembers.add(member);
                    ChatMember chatMember = new ChatMember(
                        new ChatMemberId(chatRoom, member)
                    );
                    chatMemberRepository.save(chatMember);
                }
            }
            
            // 채팅 메시지 생성 (3-15개)
            String[][] messageTemplates = {
                {
                    "안녕하세요! 의뢰 건으로 연락드립니다.",
                    "네, 안녕하세요. 어떤 작업을 원하시나요?",
                    "로고 디자인을 부탁드리고 싶습니다.",
                    "가능합니다. 예산과 일정은 어떻게 되시나요?",
                    "예산은 50만원 정도이고, 2주 안에 완성해주시면 됩니다.",
                    "네, 가능합니다. 작업 시작하겠습니다!"
                },
                {
                    "프로젝트 진행 상황은 어떤가요?",
                    "현재 80% 정도 완료되었습니다.",
                    "언제쯤 완성될까요?",
                    "내일까지는 완성해서 보내드리겠습니다.",
                    "네, 감사합니다!"
                },
                {
                    "수정사항이 있어서 연락드립니다.",
                    "어떤 부분을 수정하시길 원하시나요?",
                    "색상을 좀 더 밝게 해주세요.",
                    "네, 알겠습니다. 수정해서 다시 보내드리겠습니다."
                }
            };
            
            String[] messages = messageTemplates[i % messageTemplates.length];
            
            for (int j = 0; j < messages.length; j++) {
                ChatMessage message = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .member(chatMembers.get(j % chatMembers.size()))
                    .content(messages[j])
                    .build();
                chatMessageRepository.save(message);
            }
        }
    }
    
    // 6. 포인트/크레딧 생성 (더 다양하고 현실적으로)
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
            "디지털 마케팅 전문가로서 온라인 마케팅 전략 수립과 실행을 도와드립니다."
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
            "• 성장 마케팅 전문가\n• 스타트업 마케팅 컨설팅\n• 퍼포먼스 마케팅 ROI 300% 달성"
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
            "정보시스템학과", "멀티미디어학과", "국제통상학과", "회계학과", "광고홍보학과"
        };
        return majors[index % majors.length];
    }
}