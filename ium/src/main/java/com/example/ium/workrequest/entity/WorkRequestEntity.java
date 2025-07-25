package com.example.ium.workrequest.entity;

import com.example.ium._core.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "WorkRequest_tb")
public class WorkRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String category;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Type type;

    private Long expert;

    private int price;

    private String fileUrl;
    private String fileName;

    private String resultFileUrl;
    private String resultFileName;

    private int adPoint;

    public enum Status {
        OPEN, IN_PROGRESS, CANCELED, WAIT, DONE, EXPIRED
    }

    public enum Type {
        FORMAL, INFORMAL
    }
}