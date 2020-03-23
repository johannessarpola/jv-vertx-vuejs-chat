<template>
  <div
    class="message--own"> <!-- TODO Boolean to check if own --> 
    <div class="message__date">
      {{ date }}
    </div>
    <div class="message__contents">
      {{ contents }}
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
    own: {
      type: Boolean,
      default: false,
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