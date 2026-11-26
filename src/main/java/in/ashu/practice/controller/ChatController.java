package in.ashu.practice.controller;

import in.ashu.practice.dto.Answer;
import in.ashu.practice.dto.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatClient chatClient;

    @PostMapping
    public Answer chat(@RequestBody Question question, Authentication user) {
        log.debug("user={} asked question={}", user.getPrincipal(), question);
        return chatClient.prompt()
                .user(question.question())
                .advisors(SessionAdvisors.session(user.getName()))
                .call()
                .entity(Answer.class);
    }
}