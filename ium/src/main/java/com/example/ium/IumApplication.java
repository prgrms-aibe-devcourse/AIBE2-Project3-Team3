package com.example.ium;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Password;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.specialization.domain.model.Specialization;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
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
                                     PasswordEncoder passwordEncoder) {
    return args -> {
      memberJPARepository.saveAll(Arrays.asList(
              newMember("USERNAME1", "user1@test.com", "test1", passwordEncoder),
              newMember("USERNAME2", "user2@test.com", "test1", passwordEncoder),
              newMember("ADMIN", "admin@test.com", "test1", passwordEncoder)
      ));
      specializationJPARepository.saveAll(Arrays.asList(
                newSpecialization("디자인"),
                newSpecialization("프로그래밍"),
                newSpecialization("영상편집"),
                newSpecialization("세무/법무/노무"),
                newSpecialization("번역/통역")
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
}
