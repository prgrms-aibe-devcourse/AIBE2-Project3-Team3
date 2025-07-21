package com.example.ium.chat.domain.repository;

import com.example.ium.chat.domain.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  
  List<ChatMessage> findByChatRoomIdOrderByRegTimeAsc(Long chatRoomId);
}
