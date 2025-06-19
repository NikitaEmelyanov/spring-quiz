package ru.yandex.practicum.quiz.model;

import lombok.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Класс для хранения лога прохождения квиза
 */
public class QuizLog implements Iterable<QuizLog.Entry> {
    private final List<Entry> log;

    public QuizLog(int capacity) {
        this.log = new ArrayList<>(capacity);
    }

    /**
     * Метод для записи ответа на очередной вопрос в лог прохождения квиза
     * @param number   номер вопроса
     * @param question вопрос
     * @param answer   ответ пользователя
     */
    public void logAnswer(int number, Question question, int answer) {
        if (log.size() < number) {
            log.add(new Entry(number, question));
        }
        log.getLast().addAnswer(answer);
    }

    // реализация метода iterator интерфейса Iterable
    // позволяет итерироваться по списку записей лога
    @Override
    public Iterator<Entry> iterator() {
        return log.iterator();
    }

    public long total() {
        return log.size();
    }

    public long successful() {
        return log.stream().filter(Entry::isSuccessful).count();
    }
    /**
     * Клас, представляющий запись лога прохождения квиза
     */
    @Value
    public static class Entry {
        int number;
        Question question;
        List<Integer> answers;

        public Entry(int number, Question question) {
            this.number = number;
            this.question = question;
            this.answers = new ArrayList<>();
        }

        public void addAnswer(int answer) {
            answers.add(answer);
        }

        public boolean isSuccessful() {
            return answers.contains(question.getCorrectAnswerNumber());
        }
    }
}
