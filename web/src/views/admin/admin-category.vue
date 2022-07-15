<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form layout="inline">
        <a-form-item>
          <a-button type="primary" @click="handleQuery()">
            查询
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="add()">
            新增
          </a-button>
        </a-form-item>
      </a-form>
      <!-- 表格 -->
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="del(record.id)"
            >
              <a-button type="danger" >
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>

    <!-- 编辑表单 -->
    <a-modal
        title="分类表单"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="modalHandleOk"
    >
      <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="名称">
          <a-input v-model:value="category.name" />
        </a-form-item>
        <a-form-item label="父分类">
          <a-input v-model:value="category.parentId" />
          <a-select
              v-model:value="category.parentId"
              ref="select"
          >
            <a-select-option :value="0">
              无
            </a-select-option>
            <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
              {{c.name}}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="顺序">
          <a-input v-model:value="category.sort" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout>

</template>
<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

  export default defineComponent({
    name: "AdminCategory",
    setup() {
      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
          key: 'parentId',
          dataIndex: 'parentId'
        },
        {
          title: '排序',
          dataIndex: 'sort'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];
      const level1 = ref(); // 一级目录
      level1.value = [];

      const loading = ref(false);
      /**
       * 修改图书
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        category.value = Tool.copy(record);
      };
      /**
       * 添加图书
       */
      const add = () => {
        modalVisible.value = true;
        category.value = {};
      };
      /**
       * 删除图书
       */
      const del = (id: number) => {
        loading.value = true;
        axios.delete("/category/delete/" + id).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            // 重新加载列表
            handleQuery();
          }
        });
      }

      /**
       * 向后端查询数据
       */
      const handleQuery = () => {
        loading.value = true;
        axios.get("/category/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            level1.value = Tool.array2Tree(data.data, 0);
          } else {
            message.error(data.message);
          }
        });
      };

      // --- 表单信息 ---
      const category = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const modalHandleOk = () => {
        modalLoading.value = true;
        axios.post("/category/save", category.value).then((response) => {
          modalVisible.value = false;
          const data = response.data;
          if (data.success) {
            modalLoading.value = false;
            // 重新加载列表
            handleQuery();
          } else {
            message.error(data.message);
          }
        });
      };

      onMounted(() => {
        handleQuery();
      });

      return {
        columns,
        level1,
        loading,
        edit,
        add,
        del,

        handleQuery,

        category,
        modalVisible,
        modalLoading,
        modalHandleOk,
      };
    }
  });
</script>

<style scoped>
  img {
    width: 50px;
    height: 50px;
  }
</style>
