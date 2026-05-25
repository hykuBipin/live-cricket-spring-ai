package com.example.livecricket.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CommentaryService {

    private final ChatClient chatClient;

    public CommentaryService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generate(String text) {

        String prompt = """
                Generate ONE short professional live cricket commentary line.

                Style:
                - Like Cricbuzz or Cricinfo
                - Maximum 20 words
                - Exciting
                - Realistic
                - No long paragraphs
                - No introductions
                - No explanations

                Score:
                %s
                """.formatted(text);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}