package com.example.ium.chat.domain.mongo.repository;

import com.example.ium.chat.domain.model.ChatMessageDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageDocMongoRepository extends MongoRepository<ChatMessageDoc, Long> {
  List<ChatMessageDoc> findByRoomIdOrderByRegTimeAsc(Long roomId);
}
