package com.example.ium.chat.domain.repository;

import com.example.ium.chat.domain.model.ChatMember;
import com.example.ium.chat.domain.model.ChatMemberId;
import com.example.ium.chat.domain.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
  @Query("select distinct cm.id.chatRoom from ChatMember cm where cm.id.member.id = :memberId")
  List<ChatRoom> findChatRoomsByMember(@Param("memberId") Long memberId);
  
  @Query("""
    SELECT cm.id.chatRoom
    FROM ChatMember cm
    WHERE cm.id.member.id IN (:userId1, :userId2)
    GROUP BY cm.id.chatRoom
    HAVING COUNT(DISTINCT cm.id.member.id) = 2
    """)
  Optional<ChatRoom> findChatRoomByMembers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
  
  
}
