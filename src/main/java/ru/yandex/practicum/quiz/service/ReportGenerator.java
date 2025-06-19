package ru.yandex.practicum.quiz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.quiz.config.AppConfig;
import ru.yandex.practicum.quiz.model.QuizLog;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.quiz.config.AppConfig.ReportMode.VERBOSE;
import static ru.yandex.practicum.quiz.config.AppConfig.ReportOutputMode.CONSOLE;

@Slf4j
@Component
public class ReportGenerator {
    private final AppConfig appConfig;

    public ReportGenerator(AppConfig appConfig) {
        this.appConfig = appConfig;
        log.debug("Инициализирован ReportGenerator с конфигурацией: {}", appConfig.getReport());
    }

    public void generate(QuizLog quizLog) {
        if (!appConfig.getReport().isEnabled()) {
            log.warn("Генерация отчета отключена в конфигурации");
            return;
        }

        try {
            boolean isConsole = appConfig.getReport().getOutput().getMode() == AppConfig.ReportOutputMode.CONSOLE;
            String outputDest = isConsole ? "консоль" : "файл: " + appConfig.getReport().getOutput().getPath();

            log.info("Генерация отчета в {}", outputDest);

            try (PrintWriter writer = isConsole ?
                new PrintWriter(System.out) :
                new PrintWriter(appConfig.getReport().getOutput().getPath())) {
                write(quizLog, writer);
            }
        } catch (Exception e) {
            log.error("Ошибка при генерации отчета", e);
            System.out.println("При генерации отчёта произошла ошибка: " + e.getMessage());
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