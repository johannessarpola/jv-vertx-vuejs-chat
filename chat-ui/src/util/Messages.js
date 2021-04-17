import {MessageTypeEnum } from './Enums'
import moment from "moment";

function newMessage(id, displayName, contents, type, date) {
    return {
        id: id,
        displayName: displayName,
        contents: contents,
        date: date,
        type: type
    };
}


function newMessageNow(id, displayName, contents, type) {
    const date = moment().format("HH:mm:ss");
    return newMessage(id, displayName, contents, type, date);
}

function newChatMessage(id, displayName, contents) {
    return newMessageNow(id, displayName, contents, MessageTypeEnum.CHAT)
}

function newAssignedIdMessage(id, displayName) {
    const msg = `Assigned id of ${displayName}`;
    return newMessageNow(id, displayName, msg, MessageTypeEnum.ASSIGNED_ID);
}

function newUserJoinedMessage(id, displayName) {
    const msg = `New user joined room with display name of ${message.displayName}`;
    return newMessageNow(id, displayName, msg, MessageTypeEnum.USER_JOINED);
}



export {
    newChatMessage, newAssignedIdMessage, newUserJoinedMessage
}