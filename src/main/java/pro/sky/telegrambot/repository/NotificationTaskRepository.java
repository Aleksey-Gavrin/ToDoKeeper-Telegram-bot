package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    @Query(value = "SELECT * FROM notification_task WHERE date_time < :dateTime AND is_send = false", nativeQuery = true)
    Optional<List<NotificationTask>> findTasksToSend(LocalDateTime dateTime);

    @Query(value = "UPDATE notification_task SET is_send = true WHERE id = :id", nativeQuery = true)
    void setIsSendToTrue(long id);
}
