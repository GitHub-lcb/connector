<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="路由名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.name" placeholder="路由名称" />
      </a-form-item>

      <a-form-item label="状态" class="smart-query-form-item">
        <a-select v-model:value="queryForm.status" style="width: 120px" placeholder="全部">
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="active">启用</a-select-option>
          <a-select-option value="inactive">停用</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item class="smart-query-form-item">
        <a-button-group>
          <a-button type="primary" @click="onSearch">
            <template #icon>
              <SearchOutlined />
            </template>
            查询
          </a-button>
          <a-button @click="resetQuery">
            <template #icon>
              <ReloadOutlined />
            </template>
            重置
          </a-button>
        </a-button-group>
      </a-form-item>
    </a-row>
  </a-form>
  <!---------- 查询表单form end ----------->

  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="addRoute" type="primary">
          <template #icon>
            <PlusOutlined />
          </template>
          新建路由
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :refresh="queryData" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="id"
      bordered
      :loading="tableLoading"
      :pagination="false"
    >
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'status'">
          <a-tag :color="text === 'active' ? 'success' : 'error'">{{ text === 'active' ? '启用' : '停用' }}</a-tag>
        </template>
        <template v-if="column.dataIndex === 'method'">
          <a-tag>{{ text }}</a-tag>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="editRoute(record)" type="link">编辑</a-button>
            <a-button @click="copyRoute(record)" type="link">复制</a-button>
            <a-button @click="showHistory(record)" type="link">历史</a-button>
            <a-button @click="deleteRoute(record)" danger type="link">删除</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryData"
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <RouteFormDrawer ref="formDrawer" @reloadList="queryData" />
    <RouteHistoryModal ref="historyModal" @reloadList="queryData" />
  </a-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { connectorApi } from '/@/api/business/connector/connector-api';
import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import TableOperator from '/@/components/support/table-operator/index.vue';
import RouteFormDrawer from './components/route-form-drawer.vue';
import RouteHistoryModal from './components/route-history-modal.vue';
import _ from 'lodash';

const columns = ref([
  {
    title: '路由名称',
    dataIndex: 'name',
    width: 150,
  },
  {
    title: '渠道',
    dataIndex: 'channel',
    width: 100,
  },
  {
    title: '源路径',
    dataIndex: 'sourcePath',
    width: 200,
  },
  {
    title: '目标地址',
    dataIndex: 'targetUrl',
    width: 250,
    ellipsis: true,
  },
  {
    title: '方法',
    dataIndex: 'method',
    width: 80,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 150,
  },
  {
    title: '操作',
    dataIndex: 'action',
    fixed: 'right',
    width: 200,
  },
]);

const queryFormState = {
  name: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10,
};

const queryForm = reactive(_.cloneDeep(queryFormState));
const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);
const formDrawer = ref();
const historyModal = ref();

function resetQuery() {
  Object.assign(queryForm, _.cloneDeep(queryFormState));
  queryData();
}

function onSearch() {
  queryForm.pageNum = 1;
  queryData();
}

async function queryData() {
  tableLoading.value = true;
  try {
    const res = await connectorApi.queryRoutePage(queryForm);
    tableData.value = res.data.list;
    total.value = res.data.total;
  } catch (e) {
    console.error(e);
  } finally {
    tableLoading.value = false;
  }
}

function addRoute() {
  formDrawer.value.showDrawer();
}

function editRoute(record) {
  formDrawer.value.showDrawer(record.id);
}

function copyRoute(record) {
  formDrawer.value.showDrawer(record.id, true);
}

function showHistory(record) {
  historyModal.value.showModal(record.id);
}

function deleteRoute(record) {
  Modal.confirm({
    title: '提示',
    content: `确定要删除路由【${record.name}】吗?`,
    okText: '删除',
    okType: 'danger',
    onOk() {
      singleDelete(record);
    },
  });
}

async function singleDelete(record) {
  try {
    SmartLoading.show();
    await connectorApi.deleteRoute(record.id);
    message.success('删除成功');
    queryData();
  } catch (e) {
    console.error(e);
  } finally {
    SmartLoading.hide();
  }
}

onMounted(queryData);
</script>
