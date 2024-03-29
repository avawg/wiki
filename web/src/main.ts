import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 使用ant-design-vue组件库
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from "axios";
import {Tool} from "@/util/tool";

// axios默认地址
axios.defaults.baseURL = process.env.VUE_APP_SERVER;

const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');

// 使用ant-design-vue全局图标
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

// axios拦截器打印日志
axios.interceptors.request.use(
    function (config) {
        console.log('请求参数：', config);
        const token = store.state.user.token;
        if (Tool.isNotEmpty(token)) {
            config.headers.token = token;
            console.log("请求header中添加token:", token);
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

axios.interceptors.response.use(
    function (response) {
        console.log('返回结果：', response);
        return response;
    },
    error => {
        return Promise.reject(error);
    }
);

console.log("环境:", process.env.NODE_ENV);
console.log("服务器端:", process.env.VUE_APP_SERVER);
