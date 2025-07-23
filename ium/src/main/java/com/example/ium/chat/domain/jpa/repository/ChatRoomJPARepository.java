package com.example.ium.chat.domain.jpa.repository;

import com.example.ium.chat.domain.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomJPARepository extends JpaRepository<ChatRoom, Long> {
  List<ChatRoom> findByCreatedBy(String email);
}
