package com.example.ium.chat.handler;

import com.example.ium.chat.dto.ChatMessageDto;
import com.example.ium.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSockChatHandler extends TextWebSocketHandler {
  private final ObjectMapper objectMapper;
  private final ChatService chatService;
  
  // WebSockChatHandler 내부
  private final ConcurrentHashMap<String, Set<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();
  
  
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
  }
  
  
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
    
    // 세션 set 조회 또는 생성
    String roomId = chatMessageDto.getRoomId();
    Set<WebSocketSession> sessions = sessionMap.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    
    if (chatMessageDto.getType().equals(ChatMessageDto.MessageType.ENTER)) {
      //사용자가 방에 입장하면  Enter 메세지를 보내도록 해놓음. 이건 새로운사용자가 socket 연결한 것이랑은 다름.
      //socket 연결은 이 메세지 보내기전에 이미 되어있는 상태
      sessions.add(session);
      chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 입장했습니다.");  //TALK일 경우 msg가 있을 거고, ENTER일 경우 메세지 없으니까 message set
      chatMessageDto.setCreatedAt(LocalDateTime.now().format(formatter));
      sendToEachSocket(sessions,new TextMessage(objectMapper.writeValueAsString(chatMessageDto)) );
    }else if (chatMessageDto.getType().equals(ChatMessageDto.MessageType.QUIT)) {
      sessions.remove(session);
      chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 퇴장했습니다..");
      chatMessageDto.setCreatedAt(LocalDateTime.now().format(formatter));
      sendToEachSocket(sessions,new TextMessage(objectMapper.writeValueAsString(chatMessageDto)) );
    } else {
      chatMessageDto.setCreatedAt(LocalDateTime.now().format(formatter));
      chatService.createChatMessage(chatMessageDto);
      sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessageDto))); //입장,퇴장 아닐 때는 클라이언트로부터 온 메세지 그대로 전달.
    }
  }
  private  void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message){
    sessions.parallelStream().forEach( roomSession -> {
      try {
        if(roomSession.isOpen()) {
          roomSession.sendMessage(message);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
  
  
  
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    //javascript에서  session.close해서 연결 끊음. 그리고 이 메소드 실행.
    //session은 연결 끊긴 session을 매개변수로 이거갖고 뭐 하세요.... 하고 제공해주는 것 뿐
  }
  
  
  
}
