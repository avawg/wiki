<template>
    <a-layout>
      <!-- 侧边栏 -->
      <a-layout-sider width="200" style="background: #fff">
        <a-menu
            mode="inline"
            v-model:selectedKeys="selectedKeys2"
            v-model:openKeys="openKeys"
            @click="handleClick"
            :style="{ height: '100%', borderRight: 0 }"
        >
          <a-menu-item key="welcome">
            <MailOutlined/>
            <span>欢迎</span>
          </a-menu-item>
          <a-sub-menu v-for="item in level1" :key="item.id">
            <template v-slot:title>
              <span><user-outlined/>{{item.name}}</span>
            </template>
            <a-menu-item v-for="child in item.children" :key="child.id">
              <MailOutlined/><span>{{child.name}}</span>
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>

      <!-- 内容 -->
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <div class="welcome" v-show="showWelcome">
          <the-statistic></the-statistic>
        </div>
        <!-- 列表组件 -->
        <a-list v-show="!showWelcome" item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3}" :data-source="ebooks">
          <template #renderItem="{ item }">
            <a-list-item key="item.name">
              <template #actions>
                <span>
                  <component v-bind:is="'FileOutlined'" style="margin-right: 8px" />
                  {{ item.docCount }}
                </span>
                <span>
                  <component v-bind:is="'UserOutlined'" style="margin-right: 8px" />
                  {{ item.viewCount }}
                </span>
                <span>
                  <component v-bind:is="'LikeOutlined'" style="margin-right: 8px" />
                  {{ item.voteCount }}
                </span>
              </template>

              <a-list-item-meta :description="item.description">
                <template #title>
                  <router-link :to="'/doc?ebookId=' + item.id"> {{ item.name }} </router-link>
                </template>
                <template #avatar><a-avatar :src="item.cover" /></template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-layout-content>
    </a-layout>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from "axios";
  import {message} from 'ant-design-vue';
  import {Tool} from '@/util/tool';
  import TheStatistic from "@/components/the-statistic.vue";

  export default defineComponent({
    name: 'Home',
    components: {
      TheStatistic
    },

    setup() {
      const level1 = ref();
      const handleQueryCategory = () => {
        axios.get("/category/all").then((response) => {
          const data = response.data;
          if (data.success) {
            level1.value = Tool.array2Tree(data.data, 0);
          } else {
            message.error(data.message);
          }
        });
      };

      const ebooks = ref();
      const handleQueryEbook = () => {
        axios.get("/ebook/list", {
          params: {
            page: 1,
            size: 1000,
            category2Id: category2Id
          }
        }).then((response) => {
          const data = response.data;
          ebooks.value = data.data.list;
        });
      }

      let category2Id = 0;

      const showWelcome = ref(true);
      const handleClick = (value: any) => {
        if (value.key === 'welcome') {
          showWelcome.value = true;
        } else {
          showWelcome.value = false;
          category2Id = value.key;
          handleQueryEbook();
        }
      }

      onMounted(() => {
        handleQueryCategory();
      });

      return {
        showWelcome,
        handleClick,

        level1,
        ebooks,
      }
    }
  });
</script>

<!-- 图标样式 当前页面有效 -->
<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>