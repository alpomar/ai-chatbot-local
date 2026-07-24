package dev.alpomar.aichatbot.dto;

public class BotHistory {

    private String prompt;
    private String response;

    public BotHistory(String prompt, String response) {
        this.prompt = prompt;
        this.response = response;
    }

    @Override
    public String toString() {
        return String.format(CHAT_HISTORY, prompt, response);
    }


    private static final String CHAT_HISTORY = """
                        `history_entry`:
                            `prompt`: %s
            
                            `response`: %s
                        -----------------
                       \n
            """;
}