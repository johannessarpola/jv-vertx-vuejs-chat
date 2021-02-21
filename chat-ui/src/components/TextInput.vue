<template>
  <div class="row">
    <slot name="input-field col-8">
      <q-input
        name="message"
        v-model="message"
        label="Type message..."
        aria-placeholder="Type message..."
        autofocus
        v-on:keyup.enter="onNewOwnMessage"
      />
    </slot>
    <slot name="input-button col">
      <q-btn color="white" icon="mail" v-on:click="onNewOwnMessage" text-color="black" label="Send" />
    </slot>
  </div>
</template>

<script>
export default {
  name: 'TextInput',
  components: {
  },
  props: {},
  data: function () {
    return {
      message: ''
    }
  },
  methods: {
    onNewOwnMessage () {
      console.log("button.onNewOwnMessage")
      if ((!this.message || this.message === '')) {
        return
      }
      this.$emit('newOwnMessage', this.message)
      this.message = ''
    }
  }
}
</script>
