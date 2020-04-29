<template>
  <section class="messages">
      <transition-group name="messages-list" tag="div">
        <div v-for="(message, index) in feed" :key="index" class="messages-list-item">
          <message :own="authorId == message.id" :date="message.date" :author="message.displayName" :contents="message.contents"/>
        </div>
      </transition-group>
  </section>
</template>

<script>
import Message from "./Message.vue";


export default {
  name: "MessagesList",
  components: {
    Message
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
