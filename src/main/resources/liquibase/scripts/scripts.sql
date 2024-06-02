-- liquibase formatted sql

-- changeset a.gavrin:1
CREATE TABLE notification_task (
id BIGSERIAL PRIMARY KEY,
chat_id BIGINT NOT NULL,
date_time TIMESTAMP NOT NULL,
task_text TEXT
)

-- changeset a.gavrin:2
ALTER TABLE notification_task ADD COLUMN
is_send BOOLEAN DEFAULT false