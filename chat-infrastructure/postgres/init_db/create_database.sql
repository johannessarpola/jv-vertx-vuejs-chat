create table "message_history" (
  id          serial primary key
, stamp       timestamp
, message     jsonb                -- Straight up JSON!
);

CREATE USER message_history_user WITH PASSWORD 'secretpassword';
GRANT ALL PRIVILEGES ON TABLE  message_history TO message_history_user;
GRANT USAGE, SELECT ON SEQUENCE message_history_id_seq TO message_history_user;
