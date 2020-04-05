<template>
  <div class="input__field">
    <q-input
      name="message"
      v-model="text"
      label="Type message..."
      aria-placeholder="Type message..."
      autofocus
      @input="$emit('input', $event.target.value)"
      @keyup.enter.prevent="send"
    />
  </div>
</template>

<script>
export default {
  name: "InputField",
  props: {
    value: {
      type: String,
      default: "",
      required: false
    }
  },
  methods: {
    send(event) {
      this.$emit("newOwnMessage");
    }
  }
};
</script>

<style>
</style>