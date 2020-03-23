
<template>
  <div class="vue-chat">
    <section class="window">
      <header class="window__header__container">
        <slot name="header">Chat</slot>
      </header>
      <section id="window__messages__container" class="window__messages__container">
        <slot>
          <messages-list :feed="feed" :author-id="authorId" class="messages-list" />
        </slot>
      </section>
      <div class="window__input__container">
        <slot name="input-container">
          <input-container @newOwnMessage="onNewOwnMessage" @openEmojiPicker="onOpenEmojiPicker" />
        </slot>
      </div>
    </section>
  </div>
</template>


<script>
import "./MessagesList";

console.log("Started socket");
const socket = new WebSocket("ws://localhost:9003/chat/room/1");
// Connection opened
socket.addEventListener("open", function(event) {
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
};

export default {
  name: "Chat",
  components: {
    MessagesList
  },
  props: {
    address: {
      type: String,
      required: true
    },
    initialFeed: {
      type: Array,
      default: function() {
        return [];
      },
      required: false
    },
    newMessage: {
      type: Object,
      default: function() {
        return {};
      },
      required: false
    }
  },
  data: function() {
    return {
      feed: [],
      authorId: 0,
      toggleEmojiPicker: false
    };
  },
  watch: {
    newMessage: function(newValue, oldValue) {
      this.pushToFeed(newValue);
      // scrollToBottom()
    }
  },
  mounted() {
    /*  
    if (this.attachMock) {
      import('./mocks/mock-messages-list.js')
        .then(mockData => {
          this.feed = mockData.default.feed
          this.setAuthorId(mockData.default.authorId)
        })
        .catch(error => {
          console.error('Failed to load mock data from file. ', error)
        })
    } else { */
    this.feed = this.initialFeed;
    this.authorId = this.initialAuthorId;
    /* } */
  },
  methods: {
    pushToFeed(element) {
      this.feed.push(element);
    },
    onNewOwnMessage(message) {
      const newOwnMessage = {
        id: this.authorId,
        contents: message,
        date: moment().format("HH:mm:ss")
      };
      this.pushToFeed(newOwnMessage);
      scrollToBottom();
      this.$emit("newOwnMessage", message);
    }
  }
};
</script>
