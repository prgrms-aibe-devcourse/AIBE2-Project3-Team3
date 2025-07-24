package com.example.ium.member.domain.model.expert;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attachment_tb")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_profile_id", nullable = false)
    private ExpertProfile expertProfile;

    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;
    @Column
    private String fileType;

    @Builder
    private Attachment(ExpertProfile expertProfile, String fileName, String fileUrl, String fileType) {
        this.expertProfile = expertProfile;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    public static Attachment createAttachment(ExpertProfile expertProfile, String fileName, String fileUrl, String fileType) {
        return Attachment.builder()
                .expertProfile(expertProfile)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .build();
    }
}
