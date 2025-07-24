package com.example.ium.member.domain.service;

import com.example.ium.member.domain.model.expert.Attachment;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.infrastructure.service.FireBaseFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AttachmentService {

    private final FireBaseFileService fireBaseFileService;

    public List<Attachment> saveAttachments(List<MultipartFile> attachments, ExpertProfile expertProfile) {

        List<Attachment> newAttachments = new ArrayList<>();

        if (attachments == null || attachments.isEmpty()) {
            return newAttachments;
        }

        for (MultipartFile file : attachments) {
            if (file.isEmpty()) {
                continue;
            }

            String fileUrl = fireBaseFileService.uploadFile(file, file.getOriginalFilename());
            Attachment attachment = Attachment.createAttachment(expertProfile, file.getOriginalFilename(), fileUrl);
            newAttachments.add(attachment);
        }

        return newAttachments;
    }
}
