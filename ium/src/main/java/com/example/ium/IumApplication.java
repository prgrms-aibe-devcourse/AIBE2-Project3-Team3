package com.example.ium;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Password;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.specialization.domain.model.Specialization;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class IumApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(IumApplication.class, args);
  }

  @Profile("local")
  @Bean
  CommandLineRunner localServerStart(MemberJPARepository memberJPARepository,
                                     SpecializationJPARepository specializationJPARepository,
                                     WorkRequestRepository workRequestRepository,
                                     PasswordEncoder passwordEncoder) {
    return args -> {
      memberJPARepository.saveAll(Arrays.asList(
              newMember("USERNAME1", "user1@test.com", "test1", passwordEncoder),
              newMember("USERNAME2", "user2@test.com", "test1", passwordEncoder),
              newMember("ADMIN", "admin@test.com", "test1", passwordEncoder)
      ));
      Member expert = memberJPARepository.save(newMember("Expert", "expert@test.com", "test", passwordEncoder));

      // 전문분야 데이터 중복 체크
      if (specializationJPARepository.count() == 0) {
        specializationJPARepository.saveAll(Arrays.asList(
                  newSpecialization("디자인"),
                  newSpecialization("프로그래밍"),
                  newSpecialization("영상편집"),
                  newSpecialization("세무/법무/노무"),
                  newSpecialization("번역/통역")
        ));
      }
      specializationJPARepository.saveAll(Arrays.asList(
                newSpecialization("디자인"),
                newSpecialization("프로그래밍"),
                newSpecialization("영상편집"),
                newSpecialization("세무/회계"),
                newSpecialization("번역/통역")
      ));
      workRequestRepository.saveAll(Arrays.asList(
                createRequest("의뢰 제목 1", "내용 1", "디자인", WorkRequestEntity.Status.OPEN, WorkRequestEntity.Type.FORMAL, expert.getId()),
                createRequest("의뢰 제목 2", "내용 2", "프로그래밍", WorkRequestEntity.Status.IN_PROGRESS, WorkRequestEntity.Type.INFORMAL, expert.getId()),
                createRequest("의뢰 제목 3", "내용 3", "영상편집", WorkRequestEntity.Status.DONE, WorkRequestEntity.Type.FORMAL, expert.getId()),
                createRequest("의뢰 제목 4", "내용 4", "세무/회계", WorkRequestEntity.Status.WAIT, WorkRequestEntity.Type.INFORMAL, expert.getId()),
                createRequest("의뢰 제목 5", "내용 5", "번역/통역", WorkRequestEntity.Status.CANCELED, WorkRequestEntity.Type.FORMAL, expert.getId()),
                createRequest("의뢰 제목 6", "내용 6", "디자인", WorkRequestEntity.Status.EXPIRED, WorkRequestEntity.Type.FORMAL, expert.getId())
      ));
    };
  }

  private Member newMember(String username, String emailValue, String passwordValue, PasswordEncoder passwordEncoder) {
    Email email = Email.of(emailValue);
    Password password = Password.encode(passwordValue, passwordEncoder);
    return Member.createMember(
            username,
            email,
            password
    );
  }

  private Specialization newSpecialization(String name) {
    return Specialization.createSpecialization(name);
  }

  private WorkRequestEntity createRequest(String title, String content, String category, WorkRequestEntity.Status status, WorkRequestEntity.Type type, Long expertId) {
    WorkRequestEntity entity = new WorkRequestEntity();
    entity.setTitle(title);
    entity.setContent(content);
    entity.setCategory(category);
    entity.setStatus(status);
    entity.setType(type);
    entity.setExpert(expertId);
    entity.setPrice(50000);
    entity.setFileName("sample.pdf");
    entity.setFileUrl("https://example.com/sample.pdf");
    entity.setAdPoint(10);
    return entity;
  }
}
