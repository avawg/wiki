<template>
  <a-layout-footer style="text-align: center">
    {{user.name}} wiki ©2022
  </a-layout-footer>
</template>

<script lang="ts">
import {computed, defineComponent, onMounted} from 'vue';
import store from "@/store";
import {Tool} from "@/util/tool";
import {message, notification} from "ant-design-vue";
  export default defineComponent({
    name: 'the-footer',
    setup() {
      const user = computed(() => store.state.user);

      let websocket: any;
      let token: any;

      const onOpen = () => {
        console.log('WebSocket连接成功，状态码：', websocket.readyState)
      };

      const onMessage = (event: any) => {
        notification['info']({
          message: '收到消息:',
          description: event.data,
        });
      };

      const onError = () => {
        console.log('WebSocket连接错误，状态码：', websocket.readyState)
      };

      const onClose = () => {
        console.log('WebSocket连接关闭，状态码：', websocket.readyState)
      };

      const initWebSocket = () => {
        websocket.onopen = onOpen;
        websocket.onmessage = onMessage;
        websocket.onerror = onError;
        websocket.onclose = onClose;
      };

      onMounted(() => {
        if ('WebSocket' in window) {
          token = Tool.uuid(10);
          // 连接地址：ws://127.0.0.1:8880/ws/xxx
          websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
          initWebSocket()
        } else {
          alert('当前浏览器 不支持')
        }
      });

      return {
        user,
      }
    }
  });
</script>
