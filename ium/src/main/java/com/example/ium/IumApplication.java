package com.example.ium;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Password;
import com.example.ium.member.domain.repository.MemberJPARepository;
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
                                     PasswordEncoder passwordEncoder) {
    return args -> {
      memberJPARepository.saveAll(Arrays.asList(
              newMember("USERNAME1", "user1@test.com", "test1", passwordEncoder),
              newMember("USERNAME2", "user2@test.com", "test1", passwordEncoder),
              newMember("ADMIN", "admin@test.com", "test1", passwordEncoder)
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
}
