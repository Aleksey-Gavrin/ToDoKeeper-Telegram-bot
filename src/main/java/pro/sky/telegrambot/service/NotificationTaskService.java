package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository taskRepository;

    private static final Pattern VALID_TASK_PATTERN = Pattern.compile("([0-9.:\\s]{16})(\\s*-\\s*)([\\w\\W]+)");

    public NotificationTaskService(NotificationTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask (long chatId, String receivedMessageText) throws IllegalArgumentException {
        Matcher matcher = VALID_TASK_PATTERN.matcher(receivedMessageText);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String text = matcher.group(3);
            LocalDateTime execDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (execDateTime.isAfter(LocalDateTime.now())) {
                NotificationTask task = new NotificationTask();
                task.setChatID(chatId);
                task.setTaskDateTime(execDateTime);
                task.setTaskText(text);
                taskRepository.save(task);
            }
        } else {
            throw new IllegalArgumentException("Invalid message text");
        }
    }

    public List<NotificationTask> findTasksToSend() {
        return taskRepository.findTasksToSend(LocalDateTime.now()).orElse(new ArrayList<>());
    }

    public void changeIsSendToTrue(long id) {
        taskRepository.setIsSendToTrue(id);
    }
}
