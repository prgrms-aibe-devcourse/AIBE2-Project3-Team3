package com.example.ium.specialization.application.service;

import com.example.ium.specialization.application.dto.response.SpecializationDto;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpecializationService {

    private final SpecializationJPARepository specializationJPARepository;

    @Transactional(readOnly = true)
    public List<SpecializationDto> getAllSpecializations() {
        return specializationJPARepository.findAll()
                .stream()
                .map(SpecializationDto::from)
                .toList();
    }
}
