package com.example.livecricket.controller;

import com.example.livecricket.service.CommentaryService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/score")
public class ScoreController {

    private final SimpMessagingTemplate template;
    private final CommentaryService ai;

    public ScoreController(SimpMessagingTemplate template, CommentaryService ai) {
        this.template = template;
        this.ai = ai;
    }

    @PostMapping
    public Map<String, Object> update(@RequestBody Map<String, Object> score) {

        String text = score.toString();

        String commentary = ai.generate(text);

        Map<String, Object> response = Map.of(
                "score", score,
                "commentary", commentary
        );

        template.convertAndSend("/topic/live", response);

        return response;
    }
}
