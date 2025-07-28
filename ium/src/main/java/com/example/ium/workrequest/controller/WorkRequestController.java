package com.example.ium.workrequest.controller;

import com.example.ium._core.exception.IumApplicationException;
import com.example.ium._core.security.CustomUserDetails;
import com.example.ium.workrequest.dto.ExpertDto;
import com.example.ium.workrequest.dto.MatchedDto;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.service.WorkRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class WorkRequestController {

    private final WorkRequestService workRequestService;

    @GetMapping("/workrequest")
    public String showWorkRequest(Model model) {
        List<WorkRequestEntity> allRequests = workRequestService.getAllRequests();
        WorkRequestEntity workRequest = workRequestService.getLatestRequest();
        model.addAttribute("requests", allRequests);

        // 아직 DB에 없는 aiExperts 는 더미 데이터로
        List<ExpertDto> experts = List.of(
                new ExpertDto("전문가1", "통역/번역 전문가", 50000, "소소핑", "image1.jpg"),
                new ExpertDto("전문가2", "통역/번역 전문가", 50000, "소소핑", "image2.jpg"),
                new ExpertDto("전문가3", "통역/번역 전문가", 50000, "소소핑", "image3.jpg")
        );
        model.addAttribute("request", workRequest);
        model.addAttribute("aiExperts", experts);
        model.addAttribute("targetUser", workRequest.getCreatedBy());

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

    @GetMapping("/workrequest/{id}/resultUpload")
    public String showResultUploadPage(Model model, @PathVariable("id") Long workRequestId) {
        model.addAttribute("workRequestId", workRequestId);
        return "/request/resultUpload";
    }

    @PostMapping("/workrequest/resultUpload")
    public String uploadResult(@RequestParam("file") MultipartFile file,
                               @RequestParam("workRequestId") Long workRequestId,
                               Principal principal) {
        workRequestService.uploadFile(file, workRequestId, principal.getName());
        return "redirect:/workrequest/" + workRequestId;
    }

    @GetMapping("/workrequest/{id}")
    public String showWorkRequestDetail(@PathVariable Long id, Model model) {
        try {
            WorkRequestEntity workRequest = workRequestService.getRequest(id);
            model.addAttribute("request", workRequest);
            model.addAttribute("targetUser", workRequest.getCreatedBy());

            // 수주된 상태라면 matched 페이지로
            if (workRequest.getStatus() == WorkRequestEntity.Status.MATCHED) {
                MatchedDto expertDto = workRequestService.getMatchedExpert(workRequest.getExpert());
                model.addAttribute("expert", expertDto);
                return "request/matched"; // <- matched.html 경로
            }

            return "request/workrequest"; // 일반 상세 페이지
        } catch (IumApplicationException e) {
            model.addAttribute("error", "의뢰를 찾을 수 없습니다.");
            return "common/error";
        }
    }

    @GetMapping("/workrequest/{id}/matched")
    public String showMatchedPage(@PathVariable Long id, Model model) {
        WorkRequestEntity request = workRequestService.getRequest(id);
        MatchedDto expertDto = workRequestService.getMatchedExpert(request.getExpert());

        model.addAttribute("request", request);
        model.addAttribute("expert", expertDto);
        return "request/workrequest/" + id + "/matched";
    }

    @PostMapping("/workrequest/{id}/matched")
    @ResponseBody
    public String matchRequest(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long expertId = userDetails.getMemberId();
        return workRequestService.matchExpertToWorkRequest(id, expertId); // "success" or "already"
    }
}