package ru.yandex.practicum.quiz.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.quiz.model.Question;

import java.util.List;

@Getter
@ToString
@ConfigurationProperties("spring-quiz.questions-settings")
public class QuizConfig {
    private final List<Question> questions;

    public QuizConfig(int defaultAttempts, List<Question> questions) {
        this.questions = questions.stream()
            .peek(question -> {
                if (question.getAttempts() == null) {
                    question.setAttempts(defaultAttempts);
                }
            })
            .toList();
    }
}