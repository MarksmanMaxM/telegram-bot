-- liquibase formatted sql

-- changeset mMax:1


CREATE TABLE notification_task
(
    id      SERIAL,
    chat_id int,
    message TEXT,
    time    time
);


-- changeset mMax:2
ALTER TABLE notification_task ADD date DATE;
