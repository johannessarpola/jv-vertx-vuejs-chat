<template>
  <transition-group
    name="messages-list"
    tag="div">
    <div
      v-for="(message, index) in feed"
      :key="index"
      class="messages-list-item">
      <message
        :date="message.date"
        :contents="message.contents" />
    </div>
  </transition-group>
</template>
<script>
import Message from './Message.vue'
export default {
  name: 'MessagesList',
  components: {
    Message,
  },
  props: {
    feed: {
      type: Array,
      default: function () {
        return []
      },
      required: false
    },
    authorId: {
      type: String,
      default: '',
      required: false
    }
  }
}
</script>