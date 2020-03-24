<template>
  <q-page class="flex flex-center">
    <Chat />
  </q-page>

</template>
<script>
import Chat from '../components/Chat';
/* 
  console.log("Started socket");
  const socket = new WebSocket('ws://localhost:9003/chat/room/1');
  // Connection opened
  socket.addEventListener('open', function (event) {
    console.log(event);
    setInterval(() => {
      socket.send(new Date());
    }, 2000);
  });
  // Listen for messages
  socket.onmessage = event => {
    console.log(event);
    const message = JSON.parse(event.data);

    switch (message.type) {
      case "chat":
        console.log(`${message.senderId}: ${message.message}`);
        break;
      case "assigned_id":
        console.log(`Got assigned ID: ${message.id}`);
        break;
      case "user_joined":
        console.log(`New user joined room with id ${message.id}`);
        break;
    }
  }; */

export default {
  name: "PageIndex",
  components: {
    Chat
  }
};
</script>
