package com.example.ium.chat.domain.model;

import com.example.ium._core.entity.BaseEntity;
import com.example.ium.member.domain.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_message_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_message_id")
  private Long id; // 채팅 메세지 ID
  
  @ManyToOne
  private ChatRoom chatRoom;
  
  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private Member member;
  
  @Column(name = "content", nullable = false)
  private String content;
}
