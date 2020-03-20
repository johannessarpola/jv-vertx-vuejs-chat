<template>
  <q-page class="flex flex-center">
    <img
      alt="Quasar logo"
      src="~assets/quasar-logo-full.svg"
    >
  </q-page>
</template>
<script>
console.log("Started socket");
const socket = new WebSocket('ws://localhost:9003/chat/room/1');
// Connection opened
socket.addEventListener('open', function (event) {
  setInterval( () => {
    socket.send(new Date());
  }, 2000);
});

// Listen for messages
socket.addEventListener('message', function (event) {
  const msg = JSON.parse(event.data);
  console.log('Message from server ', msg.user.userId + ":" + msg.message );
});

export default {
  name: 'PageIndex'
}
</script>
