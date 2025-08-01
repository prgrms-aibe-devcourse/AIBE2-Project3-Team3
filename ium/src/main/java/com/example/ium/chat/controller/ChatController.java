package com.example.ium.chat.controller;

import com.example.ium.chat.domain.model.ChatRoom;
import com.example.ium.chat.dto.ChatMessageDto;
import com.example.ium.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
  private final ChatService chatService;
  
  
  @RequestMapping("/chat/chatList")
  public String chatList(Model model, Principal principal){
    List<ChatRoom> roomList = chatService.findAllRoom(principal.getName());
    model.addAttribute("roomList",roomList);
    return "chat/chatList";
  }
  
  
  @PostMapping("/chat/createRoom")  //방을 만들었으면 해당 방으로 가야지.
  public String createRoom(Model model,
                           @RequestParam String name,
                           @RequestParam String targetUser,
                           Principal principal) {
    // 자기와의 대화방 생성하려고 하는 경우(대부분 admin)
    if(principal.getName().equals(targetUser)) {
      return "redirect:/chat/chatList";
    }
    ChatRoom room = chatService.createRoom(name, principal.getName(), targetUser);
    model.addAttribute("room",room);
    model.addAttribute("username", principal.getName());
    
    List<ChatRoom> roomList = chatService.findAllRoom(principal.getName());
    model.addAttribute("roomList",roomList);
    model.addAttribute("selectedRoomId", room.getId());
    
    List<ChatMessageDto> chatMessageList = chatService.getChatMessageDoc(room);
    model.addAttribute("messageList", chatMessageList);
    
    return "chat/chatRoom";  //만든사람이 채팅방 1빠로 들어가게 됩니다
  }
  
  @GetMapping("/chat/chatRoom")
  public String chatRoom(Model model,
                         @RequestParam Long roomId,
                         Principal principal){
    List<ChatRoom> roomList = chatService.findAllRoom(principal.getName());
    model.addAttribute("roomList",roomList);
    
    ChatRoom room = chatService.findRoomById(roomId);
    model.addAttribute("room", room);
    model.addAttribute("selectedRoomId", roomId);
    model.addAttribute("username", principal.getName());
    
    List<ChatMessageDto> chatMessageList = chatService.getChatMessageDoc(room);
    model.addAttribute("messageList", chatMessageList);
    return "chat/chatRoom";
  }
}
