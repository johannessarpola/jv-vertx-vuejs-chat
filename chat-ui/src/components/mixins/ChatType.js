import { MessageTypeEnum } from "../../util/Enums"

const MessageType = {
  methods: {
    isChat: function (type) {
      return type == MessageTypeEnum.CHAT
    },
    isNotification: function (type) {
      return type == MessageTypeEnum.ASSIGNED_ID ||
        type == MessageTypeEnum.USER_JOINED;
    }
  }
}

export { MessageType }