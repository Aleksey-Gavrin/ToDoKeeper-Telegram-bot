package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskService service;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService service) {
        this.telegramBot = telegramBot;
        this.service = service;
    }

    private final String greetingMessage = "Вас приветствует чат-бот. Для регистрации задачи отправьте сообщение в формате: " +
            "ДД.ММ.ГГГГ ЧЧ:ММ - текст задачи.";
    private final String successMessage = "Задача успешно зарегистрирована. Текст задачи будет прислан сообщением в " +
            "указанную дату и время, если оно еще не наступило.";
    private final String invalidTaskFormatMessage = "Формат сообщения не соответствует ожидаемому. Ожидаемый формат: " +
            "ДД.ММ.ГГГГ ЧЧ:ММ - текст задачи.";

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                logger.info("Processing update: {}", update);
                if (update.message() == null) {
                    return;
                }
                String messageText = update.message().text();
                Long chatId = update.message().chat().id();
                if (messageText.equals("/start")) {
                    SendMessage message = new SendMessage(chatId, greetingMessage);
                    SendResponse response = telegramBot.execute(message);
                } else try {
                    service.createTask(chatId, messageText);
                    SendMessage message = new SendMessage(chatId, successMessage);
                    SendResponse response = telegramBot.execute(message);
                    logger.debug("Task successfully created and added to DB");
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                    SendMessage message = new SendMessage(chatId, invalidTaskFormatMessage);
                    SendResponse response = telegramBot.execute(message);
                }
            } catch (Exception e) {
                logger.error("Failed to process {}", update, e);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(fixedDelay = 60000)
    public void sendTasks() {
        try {
            List<NotificationTask> listOfTasksToSend = service.findTasksToSend();
            if (listOfTasksToSend.isEmpty()) {
                return;
            }
            listOfTasksToSend.forEach(task -> {
                SendMessage message = new SendMessage(task.getChatID(), task.getTaskText());
                SendResponse response = telegramBot.execute(message);
                if (response.isOk()) {
                    service.changeIsSendToTrue(task.getId());
                }
            });
        } catch (JpaSystemException e) {
            logger.error("There is no available task to send in DB: " + e.getMessage());
        }
    }

}
