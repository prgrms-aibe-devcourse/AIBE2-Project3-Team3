package com.example.ium.chat.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.chat.domain.model.*;
import com.example.ium.chat.domain.jpa.repository.ChatMemberJPARepository;
import com.example.ium.chat.domain.jpa.repository.ChatMessageJPARepository;
import com.example.ium.chat.domain.jpa.repository.ChatRoomJPARepository;
import com.example.ium.chat.domain.mongo.repository.ChatMessageDocMongoRepository;
import com.example.ium.chat.dto.ChatMessageDto;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
  
  private final ChatRoomJPARepository chatRoomRepository;
  private final ChatMemberJPARepository chatMemberJPARepository;
  private final ChatMessageJPARepository chatMessageJPARepository;
  private final MemberJPARepository memberJPARepository;
  private final ChatMessageDocMongoRepository chatMessageDocMongoRepository;
  
  public List<ChatRoom> findAllRoom(String email) {
    Member member = getMember(email);
    List<ChatRoom> chatRoomList = chatMemberJPARepository.findChatRoomsByMember(member.getId());
    return chatRoomList;
  }
  
  private Member getMember(String email) {
    return memberJPARepository.findByEmail(Email.of(email))
            .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));
  }
  
  public ChatRoom findRoomById(Long roomId) {
    return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new IumApplicationException(ErrorCode.CHAT_ROOM_NOT_FOUND));
  }
  
  public ChatRoom createRoom(String name, String email, String targetUserEmail) {
    Member member = getMember(email);
    Member targetMember = getMember(targetUserEmail);
    return chatMemberJPARepository.findChatRoomByMembers(member.getId(), targetMember.getId())
            .orElseGet(() -> {
              ChatRoom newRoom = chatRoomRepository.save(ChatRoom.builder().roomName(name).build());
              chatMemberJPARepository.save(new ChatMember(new ChatMemberId(newRoom, member)));
              chatMemberJPARepository.save(new ChatMember(new ChatMemberId(newRoom, targetMember)));
              return newRoom;
            });
  }
  
  public void createChatMessage(ChatMessageDto chatMessageDto) {
    ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(chatMessageDto.getRoomId()))
            .orElseThrow(() -> new IumApplicationException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    
    Member member = getMember(chatMessageDto.getSender());
    
    ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .content(chatMessageDto.getMessage())
            .member(member)
            .build();
    
    chatMessageJPARepository.save(chatMessage);
  }
  
  public List<ChatMessageDto> getChatMessage(ChatRoom room) {
    return chatMessageJPARepository.findByChatRoomIdOrderByRegTimeAsc(room.getId())
            .stream()
            .map(ChatMessageDto::of)
            .collect(Collectors.toList());
  }
  
  public void createChatMessageDoc(ChatMessageDto chatMessageDto) {
    ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(chatMessageDto.getRoomId()))
            .orElseThrow(() -> new IumApplicationException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    
    Member member = getMember(chatMessageDto.getSender());
    
    ChatMessageDoc chatMessage = ChatMessageDoc.builder()
            .roomId(chatRoom.getId())
            .content(chatMessageDto.getMessage())
            .memberId(member.getId())
            .build();
    chatMessageDocMongoRepository.save(chatMessage);
  }
  
  public List<ChatMessageDto> getChatMessageDoc(ChatRoom room) {
    List<ChatMessageDoc> chatMessageDocList = chatMessageDocMongoRepository.findByRoomIdOrderByRegTimeAsc(room.getId());
    
    List<ChatMessageDto> result = new ArrayList<>();
    chatMessageDocList.forEach(chatMessageDoc -> {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
      
      Member member = memberJPARepository.findById(chatMessageDoc.getMemberId())
              .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));
      
      ChatMessageDto dto = ChatMessageDto.builder()
              .type(ChatMessageDto.MessageType.TALK)
              .roomId(String.valueOf(chatMessageDoc.getRoomId()))
              .sender(member.getEmail().getValue())
              .message(chatMessageDoc.getContent())
              .createdAt(chatMessageDoc.getRegTime().format(formatter))
              .build();
      
      result.add(dto);
    });
    
    
    return result;
  }
}
