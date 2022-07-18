<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form layout="inline" :model="param">
        <a-form-item>
          <a-input v-model:value="param.name" placeholder="登录名"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
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
        title="用户表单"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @cancel="modalHandleCancel"
        @ok="modalHandleOk"
    >
      <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登录名">
          <a-input v-model:value="user.loginName" :disabled="user.id"/>
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="user.name" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input v-model:value="user.password" />
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

declare let hexMd5: any;
declare let KEY: any;

  export default defineComponent({
    name: "AdminUser",
    setup() {
      const columns = [
        {
          title: '登录名',
          dataIndex: 'loginName'
        },
        {
          title: '昵称',
          dataIndex: 'name',
        },
        {
          title: '密码',
          dataIndex: 'password'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];
      const users = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
      const loading = ref(false);

      /**
       * 表格点击页码时触发
       */
      const handleTableChange = (pagination: any) => {
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      const param = ref({loginName: null});
      /**
       * 向后端查询数据
       */
      const handleQuery = (params: any) => {
        loading.value = true;
        axios.get("/user/list", {
          params: {
            page: params.page,
            size: params.size,
            loginName: param.value.loginName // 没有值则不传递
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            users.value = data.data.list;
            // 重置分页按钮
            pagination.value.current = params.page;
            pagination.value.total = data.data.total;
          } else {
            message.error(data.message);
          }
        });
      };

      // --- 表单信息 ---
      const user = ref();
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const modalHandleOk = () => {
        modalLoading.value = true;
        user.value.password = hexMd5(user.value.password, KEY);
        axios.post("/user/save", user.value).then((response) => {
          modalVisible.value = false;
          const data = response.data;
          if (data.success) {
            // 重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
          } else {
            message.error(data.message);
          }
        });
      };
      const modalHandleCancel = () => {
        modalLoading.value = false;
      }

      /**
       * 修改用户
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        user.value = Tool.copy(record);
      };
      /**
       * 添加用户
       */
      const add = () => {
        modalVisible.value = true;
        user.value = {};
      };
      /**
       * 删除用户
       */
      const del = (id: number) => {
        loading.value = true;
        axios.delete("/user/delete/" + id).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            // 重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
          }
        });
      }

      onMounted(() => {
        handleQuery({
          page: pagination.value.current,
          size: pagination.value.pageSize
        });
      });

      return {
        columns,
        users,
        pagination,
        loading,
        handleTableChange,

        param,
        handleQuery,

        user,
        modalVisible,
        modalLoading,
        modalHandleOk,
        modalHandleCancel,
        edit,
        add,
        del,
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
