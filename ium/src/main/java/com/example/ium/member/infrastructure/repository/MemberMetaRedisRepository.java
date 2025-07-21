package com.example.ium.member.infrastructure.repository;

import com.example.ium.member.domain.meta.MemberMeta;
import org.springframework.data.repository.CrudRepository;

public interface MemberMetaRedisRepository extends CrudRepository<MemberMeta, String> {
}
