
<template>
  <div class="vue-chat">
    <header class>
      <h2>Chat</h2>
    </header>
    <div>
      <p class="text-body1">Write a room you would like join to</p>
      <div class="row">
        <q-input
          borderless
          v-model="room"
          label="Room"
          class="q-mr-sm col-7"
          :rules="[val => !!val || 'Room is required.']"
        />
        <q-btn label="Start to chat" class="col-4" color="positive" @click="chatDialog = true" />
      </div>
    </div>
    <q-dialog v-model="chatDialog" @hide="close" @show="open" full-height>
      <q-card class="full-height column" style="width: 700px;">
        <q-card-section>
          <div class="text-h6">Chatting in room {{ this.room }}</div>
        </q-card-section>

        <q-card-section class="col q-pt-none">
          <slot>
            <messages-list id="messages-area" :feed="feed" :author-id="authorId" />
          </slot>
        </q-card-section>
        <q-card-actions align="right" class="q-mx-lg bg-white text-teal">
          <text-input class="col-8" @newOwnMessage="onNewOwnMessage" />
          <q-btn class="col-4" flat label="Close" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<style scoped>
.vue-chat {
  min-width: 420px;
}
</style>
<script>
import moment from "moment";
import MessagesList from "./MessagesList";
import TextInput from "./TextInput";
import { scroll } from "quasar";
const { getScrollHeight, getScrollTarget, setScrollPosition } = scroll;

export default {
  name: "Chat",
  components: {
    MessagesList,
    TextInput
  },
  props: {
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
      room: '',
      chatDialog: false,
      feed: [],
      socket: {},
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
    console.log("mounted");
    this.feed = this.initialFeed;
    this.authorId = this.initialAuthorId;
  },
  methods: {
    connectToSocket() {
      const socket = new WebSocket("ws://localhost:9003/chat/room/" + this.room);
      socket.onmessage = this.messageHandler;
      this.socket = socket;
    },
    messageHandler(event) {
      console.log(event);
      const message = JSON.parse(event.data);

      switch (message.type) {
        case "chat":
          console.log(`${message.senderId}: ${message.message}`);
          this.receiveMessage(message);
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
      const newMesssage = {
        id: message.senderId,
        contents: message.message,
        date: moment().format("HH:mm:ss")
      };

      this.pushToFeed(newMesssage);
    },
    open(ev) {
      console.log("open");
      this.connectToSocket()
    },
    close(ev) {
      console.log("clear");
      this.socket.close();
      this.feed = [];
    },
    onNewOwnMessage(message) {
      const newOwnMessage = {
        id: this.authorId,
        contents: message,
        date: moment().format("HH:mm:ss")
      };

      const json = JSON.stringify(newOwnMessage);
      console.log("onNewOwnMessage");
      console.log(json);
      this.socket.send(json);
      this.pushToFeed(newOwnMessage);
    }
  }
};
</script>
