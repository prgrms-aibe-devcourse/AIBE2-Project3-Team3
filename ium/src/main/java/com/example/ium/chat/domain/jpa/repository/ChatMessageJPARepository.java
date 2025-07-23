package com.example.ium.chat.domain.jpa.repository;

import com.example.ium.chat.domain.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageJPARepository extends JpaRepository<ChatMessage, Long> {
  
  List<ChatMessage> findByChatRoomIdOrderByRegTimeAsc(Long chatRoomId);
}
