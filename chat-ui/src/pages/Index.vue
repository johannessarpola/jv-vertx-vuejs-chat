<template>
  <q-page class="flex flex-center">
    <Chat :room='1' />
  </q-page>

</template>
<script>
import Chat from '../components/Chat';

export default {
  name: "PageIndex",
  components: {
    Chat
  }
};
</script>
