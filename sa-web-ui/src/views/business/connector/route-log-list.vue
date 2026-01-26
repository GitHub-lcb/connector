<template>
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="路由名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.routeName" placeholder="路由名称" />
      </a-form-item>

      <a-form-item label="渠道" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.channel" placeholder="渠道名称" />
      </a-form-item>

      <a-form-item label="请求URL" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.requestUrl" placeholder="请求URL" />
      </a-form-item>

      <a-form-item label="状态" class="smart-query-form-item">
        <a-select style="width: 100px" v-model:value="queryForm.successFlag" placeholder="全部" allowClear>
          <a-select-option :value="true">成功</a-select-option>
          <a-select-option :value="false">失败</a-select-option>
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

  <a-card size="small" :bordered="false" :hoverable="true">
    <div class="smart-table-operate-block">
        <a-button @click="queryData">
            <template #icon><ReloadOutlined /></template>
            刷新
        </a-button>
    </div>

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
        <template v-if="column.dataIndex === 'statusCode'">
          <a-tag :color="text >= 200 && text < 300 ? 'success' : 'error'">{{ text }}</a-tag>
        </template>
        <template v-if="column.dataIndex === 'latencyMs'">
          <span>{{ text }} ms</span>
        </template>
        <template v-if="column.dataIndex === 'errorMsg'">
          <span style="color: red" v-if="text">{{ text }}</span>
          <span v-else>-</span>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <a-button type="link" @click="showDetail(record)">详情</a-button>
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

    <RouteLogDetailModal ref="detailModal" />
  </a-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { connectorApi } from '/@/api/business/connector/connector-api';
import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import RouteLogDetailModal from './components/route-log-detail-modal.vue';
import _ from 'lodash';

const columns = ref([
  {
    title: '请求时间',
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: '路由名称',
    dataIndex: 'routeName',
    width: 250,
    ellipsis: true,
  },
  {
    title: '渠道',
    dataIndex: 'channel',
    width: 100,
  },
  {
    title: '请求路径',
    dataIndex: 'requestPath',
    width: 250,
    ellipsis: true,
  },
  {
    title: '状态码',
    dataIndex: 'statusCode',
    width: 100,
  },
  {
    title: '耗时',
    dataIndex: 'latencyMs',
    width: 100,
  },
  {
    title: '错误信息',
    dataIndex: 'errorMsg',
    ellipsis: true,
  },
  {
    title: '操作',
    dataIndex: 'action',
    fixed: 'right',
    width: 80,
  },
]);

const queryFormState = {
  routeName: '',
  channel: '',
  requestUrl: '',
  successFlag: undefined,
  pageNum: 1,
  pageSize: 20,
};

const queryForm = reactive(_.cloneDeep(queryFormState));
const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);
const detailModal = ref();

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
    const res = await connectorApi.queryRouteLogPage(queryForm);
    tableData.value = res.data.list;
    total.value = res.data.total;
  } catch (e) {
    console.error(e);
  } finally {
    tableLoading.value = false;
  }
}

async function exportData() {
    try {
        await connectorApi.exportRouteLog(queryForm);
    } catch (e) {
        console.error(e);
    }
}

function showDetail(record) {
  detailModal.value.show(record);
}

onMounted(queryData);
</script>
