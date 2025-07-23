package com.example.ium.chat.domain.repository;

import com.example.ium.chat.domain.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  List<ChatRoom> findByCreatedBy(String email);
}
