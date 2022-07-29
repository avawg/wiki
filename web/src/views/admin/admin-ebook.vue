<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form layout="inline" :model="param">
        <a-form-item>
          <a-input v-model:value="param.name" placeholder="书名"></a-input>
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
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="danger" >
              <router-link :to="'/admin/doc?ebookId=' + record.id">文档管理</router-link>
            </a-button>
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
        title="电子书表单"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="modalHandleOk"
    >
      <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="封面">
          <a-input v-model:value="ebook.cover" />
        </a-form-item>
        <a-form-item label="名称">
          <a-input v-model:value="ebook.name" />
        </a-form-item>
        <a-form-item label="分类">
          <a-cascader
              v-model:value="categoryIds"
              :field-names="{label: 'name', value: 'id', children: 'children'}"
              :options="level1"
          />
        </a-form-item>
        <a-form-item label="描述">
          <a-input v-model:value="ebook.description" type="textarea"/>
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
    name: "AdminEbook",
    setup() {
      const columns = [
        {
          title: '封面',
          dataIndex: 'cover',
          slots: { customRender: 'cover' }
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类',
          slots: { customRender: 'category' }
        },
        {
          title: '文档数',
          dataIndex: 'docCount'
        },
        {
          title: '阅读数',
          dataIndex: 'viewCount'
        },
        {
          title: '点赞数',
          dataIndex: 'voteCount'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];
      const ebooks = ref();
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

      const param = ref({name: null});
      /**
       * 向后端查询数据
       */
      const handleQuery = (params: any) => {
        loading.value = true;
        axios.get("/ebook/list", {
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name // 没有值则不传递
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            ebooks.value = data.data.list;
            // 重置分页按钮
            pagination.value.current = params.page;
            pagination.value.total = data.data.total;
          } else {
            message.error(data.message);
          }
        });
      };

      let categorys: any;

      /**
       * 查询所有分类数据
       **/
      const handleQueryCategory = () => {
        axios.get("/category/all").then((response) => {
          const data = response.data;
          if (data.success) {
            categorys = data.data;
            level1.value = Tool.array2Tree(data.data, 0);
            handleQuery({
              page: 1,
              size: pagination.value.pageSize
            });
          } else {
            message.error(data.message);
          }
        });
      };

      const getCategoryName = (categoryId: number) => {
        let result = "";
        categorys.forEach((item: any) => {
          if (item.id === categoryId) {
            // return item.name; // 注意，这里直接return不起作用
            result = item.name;
          }
        });
        return result;
      };

      // --- 表单信息 ---
      const ebook = ref();
      const level1 =  ref();
      const categoryIds = ref();
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const modalHandleOk = () => {
        modalLoading.value = true;
        ebook.value.category1Id = categoryIds.value[0];
        ebook.value.category2Id = categoryIds.value[1];
        axios.post("/ebook/save", ebook.value).then((response) => {
          modalVisible.value = false;
          modalLoading.value = false;
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

      /**
       * 修改图书
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        ebook.value = Tool.copy(record);
        categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
      };
      /**
       * 添加图书
       */
      const add = () => {
        modalVisible.value = true;
        ebook.value = {};
      };
      /**
       * 删除图书
       */
      const del = (id: number) => {
        loading.value = true;
        axios.post("/ebook/delete/" + id).then((response) => {
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
        handleQueryCategory();
      });

      return {
        columns,
        ebooks,
        pagination,
        loading,
        handleTableChange,

        param,
        handleQuery,

        categoryIds,
        level1,
        getCategoryName,

        ebook,
        modalVisible,
        modalLoading,
        modalHandleOk,
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
