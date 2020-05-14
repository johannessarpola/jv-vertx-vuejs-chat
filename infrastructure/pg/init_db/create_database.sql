create table "messages" (
  id          serial primary key
, type        text
, stamp       timestamp
, data        json                -- Straight up JSON!
);