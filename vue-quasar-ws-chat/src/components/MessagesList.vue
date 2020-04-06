<template>
  <section class="messages">
    <q-scroll-area style="height: 330px; max-width: 75%;">
      <transition-group name="messages-list" tag="div">
        <div v-for="(message, index) in feed" :key="index" class="messages-list-item">
          <message :date="message.date" :author="message.id" :contents="message.contents"/>
        </div>
      </transition-group>
    </q-scroll-area>
  </section>
</template>

<script>
import Message from "./Message.vue";
import { scroll } from 'quasar'
const { getScrollHeight } = scroll

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
  },
  methods: {
    onNewMessage () {
      console.log("newMessage");
      this.$refs.scrollArea.scrollTo(getScrollHeight( this.$refs.scrollArea))
    }
  }
};
</script>
