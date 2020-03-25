
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
          <input-container @newOwnMessage="onNewOwnMessage" />
        </slot>
      </div>
    </section>
  </div>
</template>


<script>
import moment from 'moment';
import MessagesList from './MessagesList';
import InputContainer from './InputContainer';

export default {
  name: "Chat",
  components: {
    MessagesList,
    InputContainer
  },
  props: {
    address: {
      type: String,
      default: "ws://localhost:9003/chat/room/1",
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
    const socket = new WebSocket("ws://localhost:9003/chat/room/1");
    return {
      feed: [],
      socket: socket,
      authorId: "0"
    };
  },
  watch: {
    newMessage: function(newValue, oldValue) {
      this.pushToFeed(newValue);
      // scrollToBottom()
    }
  },
  mounted() {

    console.log("Started socket");
    this.socket.onmessage = this.messageHandler;
    this.feed = this.initialFeed;
    this.authorId = this.initialAuthorId;
  },
  methods: {
    messageHandler(event) {
      console.log(event);
      const message = JSON.parse(event.data);

      switch (message.type) {
        case "chat":
          console.log(`${message.senderId}: ${message.message}`);
          this.onNewOwnMessage(message.message);
          break;
        case "assigned_id":
          console.log(`Got assigned ID: ${message.id}`);
          this.assignedId(message);
          break;
        case "user_joined":
          this.welcome(message);
          console.log(`New user joined room with id ${message.id}`);
          break;
      }
    },
    assignedId(m) {
      console.log("assignedId");
      this.authorId = m.id;
    },
    welcome(m) {
      console.log("welcome");
      const message = {
        id: "Server",
        contents: `New user joined room with ID: ${m.id}`,
        date: moment().format("HH:mm:ss")
      };
      this.pushToFeed(message);
    },
    pushToFeed(element) {
      this.feed.push(element);
    },
    receiveMessage(message) {
      const obj = JSON.parse(message);
      const newMesssage = {
        id: obj.senderId,
        contents: obj.message,
        date: moment().format("HH:mm:ss")
      };

      this.pushToFeed(newMesssage);
    },
    onNewOwnMessage(message) {
      
      const newOwnMessage = {
        id: this.authorId,
        contents: message,
        date: moment().format("HH:mm:ss")
      };
    
      const json = JSON.stringify(newOwnMessage);
      console.log(json);
      this.socket.send( JSON.stringify ({ senderId: this.authorId, message: 'message' }) );
      this.pushToFeed(newOwnMessage);
    }
  }
};
</script>
