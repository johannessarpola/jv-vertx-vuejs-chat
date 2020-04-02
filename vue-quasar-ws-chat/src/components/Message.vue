<template>
  <div
    class="message"> <!-- TODO Boolean to check if own --> 
    <div class="message-date">
      {{ date }}
    </div>
    <div class="message-contents">
      {{ author }}: {{ contents }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'Message',
  components: {},
  props: {
    date: {
      type: String,
      default: '16:30:00',
      required: false
    },
    author: {
      type: String,
      default: '',
      required: false
    },
    contents: {
      type: String,
      default: '',
      required: false
    }
  }
}
</script>