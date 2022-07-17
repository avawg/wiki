<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h3 v-if="level1.length === 0">对不起，找不到相关文档！</h3>
      <a-row :gutter="24">
        <a-col :span="6">
          <a-tree
              v-if="level1.length > 0"
              :tree-data="level1"
              @select="onSelect"
              :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              defaultExpandAll="true"
              :defaultSelectedKeys="defaultSelectedKeys"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
          <div class="wangeditor" :innerHTML="html"></div>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>

</template>
<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";

export default defineComponent({
  name: "Doc",
  setup() {
    const route = useRoute() // 路由信息

    const level1 = ref(); // 一级目录
    level1.value = [];

    const html = ref();
    /**
     * 向后端查询数据
     */
    const handleQueryDoc = () => {
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          level1.value = Tool.array2Tree(data.data, 0);
          if (Tool.isNotEmpty(level1)) {
            defaultSelectedKeys.value = [level1.value[0].id];
            handleQueryContent(level1.value[0].id)
          }
        } else {
          message.error(data.message);
        }
      });
    };

    const handleQueryContent = (id: any) => {
      axios.get("/doc/content/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          html.value = data.data;
        } else {
          message.error(data.message);
        }
      });
    };

    const defaultSelectedKeys = ref();
    defaultSelectedKeys.value = []

    const onSelect = (selectedKeys: any, info: any) => {
      if (Tool.isNotEmpty(selectedKeys[0])) {
        handleQueryContent(selectedKeys[0]);
      }
    }

    onMounted(() => {
      handleQueryDoc();
    });

    return {
      level1,
      onSelect,
      html,
      defaultSelectedKeys,
    };
  }
});
</script>


<style>
  /* 全局 */
  /* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
  /* table 样式 */
  .wangeditor table {
    border-top: 1px solid #ccc;
    border-left: 1px solid #ccc;
  }
  .wangeditor table td,
  .wangeditor table th {
    border-bottom: 1px solid #ccc;
    border-right: 1px solid #ccc;
    padding: 3px 5px;
  }
  .wangeditor table th {
    border-bottom: 2px solid #ccc;
    text-align: center;
  }

  /* blockquote 样式 */
  .wangeditor blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

  /* code 样式 */
  .wangeditor code {
    display: inline-block;
    *display: inline;
    *zoom: 1;
    background-color: #f1f1f1;
    border-radius: 3px;
    padding: 3px 5px;
    margin: 0 3px;
  }
  .wangeditor pre code {
    display: block;
  }

  /* ul ol 样式 */
  .wangeditor ul, ol {
    margin: 10px 0 10px 20px;
  }

  /* 和antdv p冲突，覆盖掉 */
  .wangeditor blockquote p {
    font-family:"YouYuan";
    margin: 20px 10px !important;
    font-size: 16px !important;
    font-weight:600;
  }

  /* 点赞 */
  .vote-div {
    padding: 15px;
    text-align: center;
  }

  /* 图片自适应 */
  .wangeditor img {
    max-width: 100%;
    height: auto;
  }

  /* 视频自适应 */
  .wangeditor iframe {
    width: 100%;
    height: 400px;
  }
</style>