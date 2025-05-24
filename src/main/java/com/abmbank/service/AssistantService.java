package com.abmbank.service;

import com.abmbank.ai.assistant.AiAssistant;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import reactor.core.publisher.Flux;

@BrowserCallable
@AnonymousAllowed
public class AssistantService {

    private final AiAssistant aiAssistant;

    public AssistantService(AiAssistant aiAssistant) {
        this.aiAssistant = aiAssistant;
    }

    public Flux<String> chat(String chatId, String userMessage) {
        return aiAssistant.chat(chatId, userMessage);
    }
} 