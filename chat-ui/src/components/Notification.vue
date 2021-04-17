<template>
  <div class="message q-pa-sm">
      <p> {{ contents }}</p>
  </div>
</template>
<script>
export default {
  name: "Notification",
  components: {},
  props: {
    date: {
      type: String,
      default: "16:30:00",
      required: false
    },
    contents: {
      type: String,
      default: "",
      required: false
    }
  }
};
</script>
