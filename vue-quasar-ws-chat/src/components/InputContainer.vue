<template>
  <div class="input__container">
    <slot name="input-field">
      <input-field
        v-model="message"
        @newOwnMessage="onNewOwnMessage" />
    </slot>
    <div class="features__container">
      <slot name="features" />
    </div>
    <slot name="input-button">
      <input-button @newOwnMessage="onNewOwnMessage" />
    </slot>
  </div>
</template>

<script>
import InputField from './InputField.vue'
import InputButton from './InputButton.vue'
export default {
  name: 'InputContainer',
  components: {
    InputField,
    InputButton,
  },
  props: {
  },
  data: function () {
    return {
      message: '',
      file: undefined,
    }
  },
  methods: {
    onNewOwnMessage () {
      if ((!this.message || this.message === '') && !this.file) {
        return
      }
      this.$emit('newOwnMessage', this.message)
      this.message = ''
    },
  }
}
</script>

<style lang="scss" scoped>
.visible {
  display: none;
}
</style>