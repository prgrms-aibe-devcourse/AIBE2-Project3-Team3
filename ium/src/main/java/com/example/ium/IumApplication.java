package com.example.ium;


import com.example.ium.specialization.domain.model.Specialization;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;


@SpringBootApplication
public class IumApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(IumApplication.class, args);
  }

  @Profile("local")
  @Bean
  CommandLineRunner localServerStart(SpecializationJPARepository specializationJPARepository) {
    return args -> {
      // 전문분야 데이터 중복 체크
      if (specializationJPARepository.count() == 0) {
        specializationJPARepository.save(newSpecialization("디자인"));
        specializationJPARepository.save(newSpecialization("프로그래밍"));
        specializationJPARepository.save(newSpecialization("영상편집"));
        specializationJPARepository.save(newSpecialization("세무/법무/노무"));
        specializationJPARepository.save(newSpecialization("번역/통역"));
      }
    };
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
