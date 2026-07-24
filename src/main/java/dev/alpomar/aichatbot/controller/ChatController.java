package dev.alpomar.aichatbot.controller;

import dev.alpomar.aichatbot.dto.BotRequest;
import dev.alpomar.aichatbot.dto.BotResponse;

import dev.alpomar.aichatbot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bot")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<BotResponse> chat(@RequestBody BotRequest botRequest) {
        var message = botRequest.getPromptMessage();
        var historyId = botRequest.getHistoryId();
        var chatResponse = chatService.call(message, historyId);
        return new ResponseEntity<>(new BotResponse(chatResponse), HttpStatus.OK);
    }

}