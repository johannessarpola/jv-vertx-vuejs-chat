<template>
  <section class="messages">
    <transition-group name="messages-list" tag="div">
      <div
        v-for="(message, index) in feed"
        :key="index"
        class="messages-list-item">
          <message v-if="isChat(message.type)"
            :own="authorId == message.id"
            :date="message.date"
            :author="message.displayName"
            :contents="message.contents"
          />
          <notification v-if="isNotification(message.type)"
            :date="message.date"
            :contents="message.contents"
          />
      </div>
    </transition-group>
  </section>
</template>

<script>
import Message from "./Message.vue";
import { MessageTypeEnum } from "../util/Enums";
import Notification from './Notification.vue';
import { MessageType } from "./mixins/ChatType";

export default {
  name: "MessagesList",
  mixins: [MessageType],
  components: {
    Message, Notification
  },
  props: {
    feed: {
      type: Array,
      default: function() {
        return [];
      },
      required: false
    },
    authorId: {
      type: String,
      default: "",
      required: false
    }
  }
};
</script>
