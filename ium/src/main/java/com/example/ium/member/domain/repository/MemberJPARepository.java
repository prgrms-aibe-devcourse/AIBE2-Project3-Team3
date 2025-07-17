package com.example.ium.member.domain.repository;

import com.example.ium.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface MemberJPARepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
