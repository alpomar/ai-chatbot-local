package dev.alpomar.aichatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BotRequest {

    @JsonProperty("prompt_message")
    String promptMessage;

    @JsonProperty("history_id")
    String historyId;

    public BotRequest() {
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getPromptMessage() {
        return promptMessage;
    }

    public void setPromptMessage(String promptMessage) {
        this.promptMessage = promptMessage;
    }
}
