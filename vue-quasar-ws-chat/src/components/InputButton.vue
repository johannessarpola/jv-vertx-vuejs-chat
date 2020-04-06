<template>
  <div>
    <q-btn color="white"  icon="mail"  @click="send" text-color="black" label="Send" />
  </div>
</template>

<script>
export default {
  name: "InputButton",
  methods: {
    send(event) {
      this.$emit("newOwnMessage");
    }
  }
};
</script>

<style>
</style>