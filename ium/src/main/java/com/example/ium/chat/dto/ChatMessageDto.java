package com.example.ium.chat.dto;

import com.example.ium.chat.domain.model.ChatMessage;
import com.example.ium.chat.domain.model.ChatMessageDoc;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
public class ChatMessageDto {
  
  // 메시지 타입 : 입장, 채팅, 나감
  public enum MessageType {
    ENTER, TALK,QUIT
  }
  private MessageType type; // 메시지 타입
  private String roomId; // 방번호
  private String sender; // 메시지 보낸사람
  private String message; // 메시지
  private String createdAt;
  
  
  public static ChatMessageDto of(ChatMessage chatMessage) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    
    return ChatMessageDto.builder()
            .type(MessageType.TALK)
            .roomId(String.valueOf(chatMessage.getChatRoom().getId()))
            .sender(chatMessage.getMember().getEmail().getValue())
            .message(chatMessage.getContent())
            .createdAt(chatMessage.getRegTime().format(formatter))
            .build();
  }
}
