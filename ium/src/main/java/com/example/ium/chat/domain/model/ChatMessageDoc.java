package com.example.ium.chat.domain.model;

import com.example.ium._core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_message")
@Builder
@Getter
public class ChatMessageDoc extends BaseEntity {
  @Id
  public String id;
  
  @Column(nullable = false)
  public String content;
  
  public Long memberId;
  
  public Long roomId;
}
