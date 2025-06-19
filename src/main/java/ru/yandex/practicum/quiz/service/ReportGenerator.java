package ru.yandex.practicum.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.quiz.config.AppConfig;
import ru.yandex.practicum.quiz.model.QuizLog;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.quiz.config.AppConfig.ReportMode.VERBOSE;
import static ru.yandex.practicum.quiz.config.AppConfig.ReportOutputMode.CONSOLE;

@Component
@RequiredArgsConstructor
public class ReportGenerator {
    private final AppConfig appConfig;

    public void generate(QuizLog quizLog) {
        if (!appConfig.getReport().isEnabled()) {
            return;
        }

        AppConfig.ReportOutputSettings outputSettings = appConfig.getReport().getOutput();
        try {
            boolean isConsole = outputSettings.getMode() == CONSOLE;
            try (PrintWriter writer = isConsole ?
                new PrintWriter(System.out) :
                new PrintWriter(outputSettings.getPath())) {
                write(quizLog, writer);
            }
        } catch (Exception exception) {
            System.out.println("При генерации отчёта произошла ошибка: " + exception.getMessage());
        }
    }

    private void write(QuizLog quizLog, PrintWriter writer) {
        writer.println("Отчёт о прохождении теста " + appConfig.getTitle() + "\n");
        for (QuizLog.Entry entry : quizLog) {
            if (appConfig.getReport().getMode() == VERBOSE) {
                writeVerbose(writer, entry);
            } else {
                writeConcise(writer, entry);
            }
        }
        writer.printf("Всего вопросов: %d\nОтвечено правильно: %d\n", quizLog.total(), quizLog.successful());
    }

    private void writeVerbose(PrintWriter writer, QuizLog.Entry entry) {
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

    private void writeConcise(PrintWriter writer, QuizLog.Entry entry) {
        char successSign = entry.isSuccessful() ? '+' : '-';
        String answers = entry.getAnswers().stream()
            .map(Object::toString)
            .collect(Collectors.joining(","));
        writer.printf("%d(%s): %s\n", entry.getNumber(), successSign, answers);
    }
}