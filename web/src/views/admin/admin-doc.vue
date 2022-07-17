<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form layout="inline">
        <a-form-item>
          <a-button type="primary" @click="handleQueryDoc()">
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
            <a-button type="danger" @click="del(record.id)">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>

    <!-- 编辑表单 -->
    <a-modal
        title="文档表单"
        v-model:visible="modalVisible"
        :confirm-loading="modalLoading"
        @ok="modalHandleOk"
    >
      <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="名称">
          <a-input v-model:value="doc.name" />
        </a-form-item>
        <a-form-item label="父文档">
          <a-tree-select
              v-model:value="doc.parentId"
              style="width: 100%"
              :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
              :tree-data="treeSelectData"
              placeholder="请选择父文档"
              tree-default-expand-all
              :replaceFields="{title: 'name', key: 'id', value: 'id'}"
          >
          </a-tree-select>
        </a-form-item>
        <a-form-item label="顺序">
          <a-input v-model:value="doc.sort" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout>

</template>
<script lang="ts">
import {createVNode, defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message, Modal} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";

export default defineComponent({
  name: "AdminDoc",
  setup() {
    const route = useRoute() // 路由信息

    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父文档',
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
    const loading = ref(false);

    const level1 = ref(); // 一级目录
    level1.value = [];
    /**
     * 向后端查询数据
     */
    const handleQueryDoc = () => {
      loading.value = true;
      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          level1.value = Tool.array2Tree(data.data, 0);
        } else {
          message.error(data.message);
        }
      });
    };

    const treeSelectData = ref(); // 树形目录
    treeSelectData.value = [];
    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const children = treeSelectData[i].children;
        if (treeSelectData[i].id === id) {
          // 将目标节点设置为disabled
          treeSelectData[i].disabled = true;
          // 遍历所有子节点，将所有子节点全部都加上disabled
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };

    let ids: Array<string> = [];
    let idsName: Array<string> = [];
    /**
     * 查找所有子树节点
     */
    const getDeleteIds = (level1: any, id: any) => {
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < level1.length; i++) {
        const children = level1[i].children;
        if (level1[i].id === id) {
          ids.push(id);
          idsName.push(level1[i].name);
          // 遍历所有子节点
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    };

    // --- 表单信息 ---
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalHandleOk = () => {
      modalLoading.value = true;
      axios.post("/doc/save", doc.value).then((response) => {
        modalVisible.value = false;
        const data = response.data;
        if (data.success) {
          modalLoading.value = false;
          // 重新加载列表
          handleQueryDoc();
        } else {
          message.error(data.message);
        }
      });
    };

    const doc = ref({});
    /**
     * 修改文档
     */
    const edit = (record: any) => {
      modalVisible.value = true;
      doc.value = Tool.copy(record);

      // 父文档不能选择当前节点及其子节点
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);
      treeSelectData.value.unshift({"id": 0, "name": '无'});
    };
    /**
     * 添加文档
     */
    const add = () => {
      console.log(route.query.ebookId);
      modalVisible.value = true;
      doc.value = {ebookId: route.query.ebookId};
      treeSelectData.value = Tool.copy(level1.value);
      treeSelectData.value.unshift({"id": 0, "name": '无'});
    };
    /**
     * 删除文档
     */
    const del = (id: number) => {
      loading.value = true;
      ids = []
      idsName = []
      getDeleteIds(level1.value, id);
      Modal.confirm({
        title: '重要提醒',
        icon: createVNode(ExclamationCircleOutlined),
        content: '将删除：【' + idsName.join("，") + "】删除后不可恢复，确认删除？",
        onOk() {
          axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
            const data = response.data;
            if (data.success) {
              // 重新加载列表
              handleQueryDoc();
            } else {
              message.error(data.message);
            }
          });
        },
      });
    }

    onMounted(() => {
      handleQueryDoc();
    });

    return {
      columns,
      loading,

      level1,
      handleQueryDoc,
      treeSelectData,

      modalVisible,
      modalLoading,
      modalHandleOk,
      doc,
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
