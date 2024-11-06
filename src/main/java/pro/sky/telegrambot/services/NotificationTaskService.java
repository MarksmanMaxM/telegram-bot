package pro.sky.telegrambot.services;


import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRep;

import java.util.List;

@Service
public class NotificationTaskService {
    
    private final NotificationTaskRep notificationTaskRep;


    public NotificationTaskService(NotificationTaskRep notificationTaskRep) {
        this.notificationTaskRep = notificationTaskRep;
    }

    public void NotifTaskSave(NotificationTask notificationTask)
    {
        notificationTaskRep.save(notificationTask);

    }


    public List<NotificationTask> findAllTasks()
    {
        return notificationTaskRep.findAll();
    }


}
