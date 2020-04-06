<template>
  <div class="message q-pa-sm">
    <div class="text-weight-regular row">
      <div class="col">{{ author }}</div>
    </div>
    <div class="row">
      <div class="col-10 text-weight-regular text-body1">{{ contents }}</div>
      <div class="col text-weight-light text-caption">{{ date }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Message",
  components: {},
  props: {
    date: {
      type: String,
      default: "16:30:00",
      required: false
    },
    author: {
      type: String,
      default: "user",
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
