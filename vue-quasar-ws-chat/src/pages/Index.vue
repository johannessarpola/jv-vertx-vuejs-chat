<template>
  <q-page class="flex flex-center">
    <img
      alt="Quasar logo"
      src="~assets/quasar-logo-full.svg"
    >
  </q-page>
</template>
<script>
const SockJS = require('sockjs-client')
const sock = new SockJS('http://localhost:9003/chat/room/1')
sock.onopen = function () {
  console.log('open')
  const obj = { test: 'ok'}
  sock.send(JSON.stringify(obj))
}
sock.onmessage = function (e) {
  console.log('message', e.data)
  sock.close()
}
sock.onclose = function () {
  console.log('close')
}
export default {
  name: 'PageIndex'
}
</script>
