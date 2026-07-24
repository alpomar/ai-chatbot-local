package dev.alpomar.aichatbot.controller;

import dev.alpomar.aichatbot.dto.BotRequest;
import dev.alpomar.aichatbot.dto.BotResponse;
import dev.alpomar.aichatbot.service.VectorStoreService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
public class ChatRagController {

    private ChatClient chatclient;
    private VectorStoreService vectorStoreService;

    public ChatRagController(VectorStoreService vectorStoreService, ChatClient.Builder chatClientBuilder) {

        this.vectorStoreService = vectorStoreService;
        SimpleVectorStore vectorStore = vectorStoreService.getVectorStore();

        chatclient = chatClientBuilder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    @PostMapping("/chat-rag")
    public ResponseEntity<BotResponse> chatWithRAG(@RequestBody BotRequest botRequest) {

        var message = botRequest.getPromptMessage();
        var chatResponse = this.chatclient.prompt().user(message).call().content().toString();

        return new ResponseEntity<>(new BotResponse(chatResponse), HttpStatus.OK);
    }
}