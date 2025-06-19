package ru.yandex.practicum.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.quiz.config.AppConfig;
import ru.yandex.practicum.quiz.config.QuizConfig;
import ru.yandex.practicum.quiz.model.Question;
import ru.yandex.practicum.quiz.model.QuizLog;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private final QuizLog quizLogger = new QuizLog(20); // Достаточно большой размер
    private final List<Question> questions;
    private final AppConfig appConfig;

    public ConsoleUI(AppConfig appConfig, QuizConfig quizConfig) {
        this.appConfig = appConfig;
        this.questions = quizConfig.getQuestions();
    }

    public QuizLog startQuiz() {
        System.out.println("\nЗдравствуйте, приступаем к тесту " + appConfig.getTitle() + "\n");

        for (int questionIdx = 0; questionIdx < questions.size(); questionIdx++) {
            Question question = questions.get(questionIdx);
            processQuestion(questionIdx + 1, question);
        }
        System.out.println("\n");
        return quizLogger;
    }

    private void processQuestion(int questionNumber, Question question) {
        for(int attemptIdx = 0; attemptIdx < question.getAttempts(); attemptIdx++) {
            System.out.println("\n");
            askQuestion(questionNumber, question, attemptIdx);

            int answerNumber = getAnswer(questionNumber, question);

            if (question.getCorrectAnswerNumber() == answerNumber) {
                break;
            } else {
                if(attemptIdx+1 < question.getAttempts()) {
                    System.out.println("К сожалению ваш ответ неверный, но вы можете попробовать еще раз");
                }
            }
        }
    }

    private void askQuestion(int questionNumber, Question question, int attemptIdx) {
        System.out.printf("Вопрос %d (попытка: %d/%d): %s\n",
            questionNumber, attemptIdx + 1, question.getAttempts(), question.getText());

        for (int optionIdx = 0; optionIdx < question.getOptions().size(); optionIdx++) {
            String option = question.getOptions().get(optionIdx);
            System.out.printf("%d.\t%s\n", optionIdx + 1, option);
        }
    }

    private int getAnswer(int questionNumber, Question question) {
        System.out.print("\nВведите номер правильного ответа или 0 для выхода: ");

        int answerNumber = input.nextInt();

        if (answerNumber == 0) {
            System.exit(0);
        }

        quizLogger.logAnswer(questionNumber, question, answerNumber);
        return answerNumber;
    }
}