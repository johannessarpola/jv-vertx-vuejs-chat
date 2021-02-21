<template>
  <div class="message q-pa-sm">
        <q-chat-message v-if="own"
        name="You"
        :text="[contents]"
        :stamp="date"
        sent
      />
      <q-chat-message v-else
        :name="author"
        :text="[contents]"
        :stamp="date"
      />
  </div>
</template>

<script>
export default {
  name: "Message",
  components: {},
  props: {
    own: {
      type: Boolean,
      default: true,
      required: false,
    },
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
