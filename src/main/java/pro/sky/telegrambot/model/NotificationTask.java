package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatID;

    @Column(name = "date_time")
    private LocalDateTime taskDateTime;

    @Column(name = "task_text")
    private String taskText;

    @Column(name = "is_send")
    private Boolean isSend = false;

    public NotificationTask(Long id, Long chatID, LocalDateTime taskDateTime, String taskText) {
        this.id = id;
        this.chatID = chatID;
        this.taskDateTime = taskDateTime;
        this.taskText = taskText;
    }

    public NotificationTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }

    public LocalDateTime getTaskDateTime() {
        return taskDateTime;
    }

    public void setTaskDateTime(LocalDateTime taskDateTime) {
        this.taskDateTime = taskDateTime;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public Boolean isSend() {
        return isSend;
    }

    public void setSend(Boolean send) {
        isSend = send;
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id = " + id +
                ", chatID = " + chatID +
                ", taskDateTime = " + taskDateTime +
                ", taskText = '" + taskText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getTaskDateTime(), that.getTaskDateTime()) &&
                Objects.equals(getTaskText(), that.getTaskText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTaskDateTime(), getTaskText());
    }
}
