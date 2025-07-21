package com.example.ium.chat.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.chat.domain.model.ChatMember;
import com.example.ium.chat.domain.model.ChatMemberId;
import com.example.ium.chat.domain.model.ChatMessage;
import com.example.ium.chat.domain.model.ChatRoom;
import com.example.ium.chat.domain.repository.ChatMemberRepository;
import com.example.ium.chat.domain.repository.ChatMessageRepository;
import com.example.ium.chat.domain.repository.ChatRoomRepository;
import com.example.ium.chat.dto.ChatMessageDto;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
  
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MemberJPARepository memberJPARepository;
  
  public List<ChatRoom> findAllRoom(String email) {
    Member member = getMember(email);
    List<ChatRoom> chatRoomList = chatMemberRepository.findChatRoomsByMember(member.getId());
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
    return chatMemberRepository.findChatRoomByMembers(member.getId(), targetMember.getId())
            .orElseGet(() -> {
              ChatRoom newRoom = chatRoomRepository.save(ChatRoom.builder().roomName(name).build());
              chatMemberRepository.save(new ChatMember(new ChatMemberId(newRoom, member)));
              chatMemberRepository.save(new ChatMember(new ChatMemberId(newRoom, targetMember)));
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
    
    chatMessageRepository.save(chatMessage);
  }
  
  public List<ChatMessageDto> getChatMessage(ChatRoom room) {
    return chatMessageRepository.findByChatRoomIdOrderByRegTimeAsc(room.getId())
            .stream()
            .map(ChatMessageDto::of)
            .collect(Collectors.toList());
  }
}
