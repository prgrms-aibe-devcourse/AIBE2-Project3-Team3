package com.example.ium.workrequest.controller;

import com.example.ium.workrequest.dto.ExpertDto;
import com.example.ium.workrequest.WorkRequestEntity;
import com.example.ium.workrequest.service.WorkRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class WorkRequestController {

    private final WorkRequestService workRequestService;

    @GetMapping("/workrequest")
    public String showWorkRequest(Model model) {
        WorkRequestEntity workRequest = workRequestService.getLatestRequest();
        if (workRequest == null) {
            // 데이터 없으면 null 처리
            workRequest = new WorkRequestEntity();
            workRequest.setTitle("아직 등록된 의뢰가 없습니다");
        }

        model.addAttribute("request", workRequest);

        // 아직 DB에 없는 aiExperts 는 더미 데이터로
        List<ExpertDto> experts = List.of(
                new ExpertDto("전문가1", "통역/번역 전문가", 50000, "소소핑", "image1.jpg"),
                new ExpertDto("전문가2", "통역/번역 전문가", 50000, "소소핑", "image2.jpg"),
                new ExpertDto("전문가3", "통역/번역 전문가", 50000, "소소핑", "image3.jpg")
        );
        model.addAttribute("aiExperts", experts);
        model.addAttribute("targetUser", workRequest.getCreatedBy()); // TODO service 단 연결후에 작업

        return "request/workrequest";
    }

    @GetMapping("/workrequest/registration")
    public String showWorkRequestRegiPage() {
        return "request/workrequestregi";
    }
    @PostMapping("/workrequest/registration")
    public String registerWorkRequest(@ModelAttribute WorkRequestEntity workRequest) {
        workRequestService.saveRequest(workRequest);
        return "redirect:/workrequest";
    }
}