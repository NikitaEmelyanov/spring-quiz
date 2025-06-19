package ru.yandex.practicum.quiz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.quiz.model.QuizLog;

import java.io.PrintWriter;
import java.util.List;

@Component
public class ReportGenerator {
    private final String quizTitle;

    public ReportGenerator(@Value("${spring-quiz.title:}") String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public void generate(QuizLog quizLog) {
        try (PrintWriter writer = new PrintWriter(System.out)) {
            write(quizLog, writer);
        } catch (Exception exception) {
            System.out.println("При генерации отчёта произошла ошибка: " + exception.getMessage());
        }
    }

    private void write(QuizLog quizLog, PrintWriter writer) {
        writer.println("Отчет о прохождении теста " + quizTitle + ".\n");
        for (QuizLog.Entry entry : quizLog) {
            writer.println("Вопрос " + entry.getNumber() + ": " + entry.getQuestion().getText());

            List<String> options = entry.getQuestion().getOptions();
            for (int i = 0; i < options.size(); i++) {
                writer.println((i + 1) + ") " + options.get(i));
            }

            writer.print("Ответы пользователя: ");
            List<Integer> answers = entry.getAnswers();
            for (Integer answer : answers) {
                writer.print(answer + " ");
            }
            writer.println();

            String successFlag = entry.isSuccessful() ? "да" : "нет";
            writer.println("Содержит правильный ответ: " + successFlag);

            writer.println();
        }
        writer.printf("Всего вопросов: %d\nОтвечено правильно: %d\n", quizLog.total(), quizLog.successful());
    }
}