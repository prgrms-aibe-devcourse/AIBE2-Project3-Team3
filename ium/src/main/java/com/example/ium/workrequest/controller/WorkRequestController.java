package com.example.ium.workrequest.controller;

import com.example.ium.workrequest.dto.ExpertDto;
import com.example.ium.workrequest.dto.WorkRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WorkRequestController {

    @GetMapping("/")
    public String showWorkRequest(Model model) {

        WorkRequestDto workRequest = new WorkRequestDto(
                "통역/번역",
                "의뢰합니다. 전문가를 찾습니다.",
                50000,
                "소소핑",
                "open",
                "안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…안녕하세요 저는…"
        );

        List<ExpertDto> experts = List.of(
                new ExpertDto("전문가1", "통역/번역 전문가", 50000, "소소핑", "image1.jpg"),
                new ExpertDto("전문가2", "통역/번역 전문가", 50000, "소소핑", "image2.jpg"),
                new ExpertDto("전문가3", "통역/번역 전문가", 50000, "소소핑", "image3.jpg")
        );

        model.addAttribute("request", workRequest);
        model.addAttribute("aiExperts", experts);

        return "workrequest";
    }
}