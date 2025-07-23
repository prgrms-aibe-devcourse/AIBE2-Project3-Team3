package com.example.ium.chat.domain.model;

import com.example.ium._core.entity.BaseEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_member_tb")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMember extends BaseEntity {

  @EmbeddedId
  private ChatMemberId id;
}
