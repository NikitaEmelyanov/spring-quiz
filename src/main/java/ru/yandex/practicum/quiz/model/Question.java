package ru.yandex.practicum.quiz.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Question {
    private String text;
    private List<String> options;
    private int correctAnswerNumber;
    private Integer attempts;

    public Question(String text, List<String> options, int correctAnswerNumber, Integer attempts) {
        if (correctAnswerNumber < 1 || correctAnswerNumber > options.size()) {
            throw new IllegalArgumentException("Номер правильного ответа: " + correctAnswerNumber
                                               + ", всего вариантов: " + options.size());
        }
        this.text = text;
        this.options = options;
        this.correctAnswerNumber = correctAnswerNumber;
        this.attempts = attempts;
    }
}