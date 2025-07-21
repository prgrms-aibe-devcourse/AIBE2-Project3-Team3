package com.example.ium.chat.domain.model;

import com.example.ium.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberId implements Serializable {
  @ManyToOne
  @JoinColumn(name = "chat_room_id")
  private ChatRoom chatRoom;
  
  @ManyToOne
  @JoinColumn(name = "user_id")
  private Member member;
}
