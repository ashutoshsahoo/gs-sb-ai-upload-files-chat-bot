package in.ashu.practice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiChatController {

    private final ChatClient chatClient;

    @Operation(summary = "Tells a joke", description = "Fetch a joke from AI")
    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String response = chatClient.prompt(message).call().content();
        log.info("Response from AI={}", response);
        return Map.of("generation", response);
    }


}
