package com.example.ium.chat.domain.model;

import com.example.ium._core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_message_tb")
@NoArgsConstructor
public class ChatMessage extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_message_id")
  private Long id; // 채팅 메세지 ID
  
  @ManyToOne
  private ChatRoom chatRoom;
  
  @Column(name = "sender_id", nullable = false)
  private Long senderId;
  
  @Column(name = "content", nullable = false)
  private String content;
}
