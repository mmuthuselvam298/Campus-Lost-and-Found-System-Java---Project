package com.campusfind.service;

import com.campusfind.dto.AiDescriptionDtos;

public interface AiDescriptionService {
    AiDescriptionDtos.DescriptionAssistResponse assistDescription(String naturalLanguageText);
}
