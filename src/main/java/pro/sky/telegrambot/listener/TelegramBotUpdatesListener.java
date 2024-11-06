package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.services.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {

        String messageText = "Hello!";
        List<NotificationTask> notificationTask = notificationTaskService.findAllTasks();
        NotificationTask newTask = new NotificationTask();


        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            long chatId = update.message().chat().id();
            SendMessage message = new SendMessage(chatId, messageText);
            if (update.message().text().equals("/start")) {


                SendResponse response = telegramBot.execute(message);
            }

            newTask.setChatId(chatId);
            newTask.setMessage(update.message().text());
            //Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
            //Date.valueOf(String.valueOf(format.parse(strDate)));

            String myString = update.message().text();
            Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4})(\\s+)(.+)");
            Matcher matcher = pattern.matcher(myString);

            String strDate;
            java.sql.Date date;
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            if (matcher.find()) {

                strDate = matcher.group(1);
                strDate = strDate.replace('.', '-');
                if (strDate != null) {
                    // date = Date.valueOf(strDate);
                    try {
                        java.util.Date utilDate = format.parse(strDate);
                        date = new java.sql.Date(utilDate.getTime());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(date.toString());
                    newTask.setDate(date);
                }

                System.out.println(strDate);
            }

            String strTime;
            pattern = Pattern.compile("(\\d{2}:\\d{2})(\\s+)(.+)");
            matcher = pattern.matcher(myString);
            if (matcher.find()) {
                strTime = matcher.group(1);
                strTime += ":00";
                newTask.setTime(Time.valueOf(strTime));
                System.out.println(matcher.group(1));  // Выводит: подстрокой
            }


            // Process your updates here
        });

        //System.out.println(newTask.toString());

        notificationTaskService.NotifTaskSave(newTask);

        List<NotificationTask> nT = notificationTaskService.findAllTasks();

        for (int i = 0; i < nT.size(); i++) {
            System.out.println(nT.get(i).toString());
        }


        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        List<NotificationTask> nT = notificationTaskService.findAllTasks();

        Date dateNow = (Date) Date.valueOf(LocalDate.now());
        Time timeNow = (Time) Time.valueOf(LocalTime.now());

        System.out.println(timeNow);
        System.out.println(dateNow);

        for (int i = 0; i < nT.size(); i++) {
            long chatId = nT.get(i).getChatId();


            if (nT.get(i).getDate().equals(dateNow)) {
                if (nT.get(i).getTime().equals(timeNow)) {
                    String messageText = nT.get(i).getMessage();
                    SendMessage message = new SendMessage(chatId, messageText);
                    SendResponse response = telegramBot.execute(message);
//                    System.out.println(nT.get(i).toString());
                    System.out.println("Отправка");


                }
            }

        }

    }

}
