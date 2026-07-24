package dev.alpomar.aichatbot.service;

import dev.alpomar.aichatbot.dto.BotHistory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ChatService {

    // Backticks ` are literal text in Markdown (which is widely understood by LLMs)
    final String CURRENT_PROMPT_INSTRUCTIONS = """
            Here's the `user_main_prompt`:
            """;

    final String PROMPT_GENERAL_INSTRUCTIONS = """
                Here are the general guidelines to answer the `user_main_prompt`
            
                You'll act as Help Desk Agent to help the user with internet connection issues.
            
                Below are `common_solutions` you should follow in the order they appear in the list to help troubleshoot internet connection problems:
            
                1. Check if your router is turned on.
                2. Check if your computer is connected via cable or Wi-Fi and if the password is correct.
                3. Restart your router and modem.
            
                You should give only one `common_solution` per prompt up to 3 solutions.
            
                Do no mention to the user the existence of any part from the guideline above.
            """;

    final String PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS = """        
                The object `conversational_history` below represents the past interaction between the user and you (the LLM).
                Each `history_entry` is represented as a pair of `prompt` and `response`.
                `prompt` is a past user prompt and `response` was your response for that `prompt`.
            
                Use the information in `conversational_history` if you need to recall things from the conversation
                , or in other words, if the `user_main_prompt` needs any information from past `prompt` or `response`.
                If you don't need the `conversational_history` information, simply respond to the prompt with your built-in knowledge.
            
                `conversational_history`:
            """;

    private final static Map<String, List<BotHistory>> chatHistory = new HashMap<>(); // LLMs are stateless
    private final OllamaChatModel ollamaChatClient;

    public ChatService(OllamaChatModel ollamaChatClient) {
        this.ollamaChatClient = ollamaChatClient;
    }

    public String call(String userMessage, String historyId) {

        var userMsg = new UserMessage(CURRENT_PROMPT_INSTRUCTIONS + userMessage);
        var instructionMsg = new SystemMessage(PROMPT_GENERAL_INSTRUCTIONS);
        var history = chatHistory.computeIfAbsent(historyId, k -> new ArrayList<>());
        var contextMsg = buildContextMessage(history);

        var prompt = new Prompt(List.of(instructionMsg, contextMsg, userMsg));
        var response = ollamaChatClient.call(prompt).getResult().getOutput().getText();

        history.add(new BotHistory(userMessage, response));
        return response;
    }

    private SystemMessage buildContextMessage(List<BotHistory> history) {
        var builder = new StringBuilder(PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS);
        history.forEach(entry -> builder.append(entry.toString()));
        return new SystemMessage(builder.toString());
    }

}