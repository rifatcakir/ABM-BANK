package com.abmbank.ai.assistant;

import com.abmbank.ai.tools.AiTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class AiAssistant {
    private static final Logger logger = LoggerFactory.getLogger(AiAssistant.class);
    private final ChatClient chatClient;

    public AiAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory, AiTools aiTools) {
        this.chatClient = modelBuilder
                .defaultSystem("""
                        You are a professional customer support assistant for a banking service.
                        Your role is to assist users with their queries regarding loans, payments, and account-related information.
                        Always provide clear, concise, and accurate responses.
                        If you are unsure about something, politely inform the user and suggest contacting customer support.
                        Today is {{current_date}}.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(), // Chat Memory
                        QuestionAnswerAdvisor.builder(vectorStore) // RAG
                                .searchRequest(
                                        SearchRequest.builder()
                                                .topK(10)
                                                .similarityThreshold(0.7)
                                                .build()
                                )
                                .build()
                )
                .defaultTools(aiTools)
                .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent) {
        return this.chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CONVERSATION_ID, chatId)
                        .param(TOP_K, 100))
                .stream()
                .content()
                .doOnNext(token -> logger.debug("Token: {}", token))
                .doOnComplete(() -> logger.debug("Stream completed."))
                .doOnError(e -> logger.debug("Error in chat stream", e));
    }
} 