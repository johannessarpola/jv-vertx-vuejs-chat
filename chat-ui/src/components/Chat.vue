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
          <q-scroll-area class="full-height"
                :thumb-style="thumbStyle"
      :bar-style="barStyle"
      >
            <slot>
              <messages-list id="messages-area" :feed="feed" :author-id="authorId" />
            </slot>
          </q-scroll-area>
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
import MessagesList from "./MessagesList";
import TextInput from "./TextInput";
import { scroll } from "quasar";
import { newChatMessage, newAssignedIdMessage, newUserJoinedMessage } from "../util/Messages.js";

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
      room: "",
      chatDialog: false,
      feed: [],
      socket: {},
      authorId: "0",
      displayName: "undefined",
      thumbStyle: {
        borderRadius: '5px',
        backgroundColor: '#027be3',
        width: '6px',
        opacity: 0.75
      },
      barStyle: {
        backgroundColor: '#027be3',
        width: '6px',
        opacity: 0.2
      }
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
      const socket = new WebSocket(
        "ws://localhost:9003/chat/room/" + this.room
      );
      socket.onmessage = this.messageHandler;
      this.socket = socket;
    },
    messageHandler(event) {
      const message = JSON.parse(event.data);
      console.log(event);

      switch (message.type) {
        case "chat":
          console.log(`${message.senderId}-${message.senderDisplayName}: ${message.message}`);
          this.chatMessage(message);
          break;
        case "assigned_id":
          console.log(`Got assigned ID: ${message.id}: ${message.displayName}`);
          this.chatAssignedId(message);
          break;
        case "user_joined":
          console.log(`New user joined room with id ${message.id}`);
          this.chatUserJoined(message);
          break;
      }
    },
    chatAssignedId(m) {
      this.authorId = m.id;
      this.displayName = m.displayName;
      const message = newAssignedIdMessage(m.id, m.displayName)
      this.pushToFeed(message);
    },
    chatUserJoined(m) {
      const message = newUserJoinedMessage(m.id, m.displayName)
      this.pushToFeed(message);
    },
    pushToFeed(element) {
      this.feed.push(element);
    },
    chatMessage(message) {
      const newMesssage = newChatMessage(message.senderId, message.senderDisplayName, message.message);
      this.pushToFeed(newMesssage);
    },
    open(ev) {
      this.connectToSocket();
    },
    close(ev) {
      this.socket.close();
      this.feed = [];
    },
    onNewOwnMessage(message) {
      const newMesssage = newChatMessage(this.authorId, this.displayName, message);
      const json = JSON.stringify(newMesssage);
      this.socket.send(json);
      this.pushToFeed(newMesssage);
    }
  }
};
</script>
