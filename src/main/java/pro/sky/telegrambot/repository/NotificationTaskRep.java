package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.models.NotificationTask;

public interface NotificationTaskRep extends JpaRepository<NotificationTask, Long> {




}
