<template>
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="路由名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.name" placeholder="请输入路由名称" />
      </a-form-item>
      <a-form-item label="状态" class="smart-query-form-item">
        <a-select style="width: 120px" v-model:value="queryForm.status" placeholder="全部" allowClear>
          <a-select-option value="active">启用</a-select-option>
          <a-select-option value="inactive">停用</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item class="smart-query-form-item smart-margin-left10">
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
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="addRoute" type="primary">
          <template #icon>
            <PlusOutlined />
          </template>
          新建路由
        </a-button>
        
        <a-button @click="confirmBatchDelete" danger :disabled="selectedRowKeyList.length === 0">
          <template #icon>
            <DeleteOutlined />
          </template>
          批量删除
        </a-button>

        <a-button @click="showImportModal" type="primary">
          <template #icon>
            <ImportOutlined />
          </template>
          导入
        </a-button>

        <a-button @click="onExport" type="primary">
          <template #icon>
            <ExportOutlined />
          </template>
          导出
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
      :scroll="{ x: 1000, y: yHeight }"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
      @change="onChange"
      @resizeColumn="handleResizeColumn"
    >
      <template #headerCell="{ column }">
        <SmartHeaderCell v-model:value="queryForm[column.dataIndex]" :column="column" @change="queryData" />
      </template>
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'status'">
          <a-tag :color="text === 'active' ? 'success' : 'error'">{{ text === 'active' ? '启用' : '停用' }}</a-tag>
        </template>
        <template v-if="column.dataIndex === 'targetUrl'">
          <span v-if="record.forwardFlag === false" style="color: #999; font-style: italic;">(不转发)</span>
          <span v-else>{{ text }}</span>
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
    
    <!---------- 导入弹窗 ----------->
    <a-modal v-model:open="importModalShowFlag" title="导入" @onCancel="hideImportModal" @ok="hideImportModal">
      <div style="text-align: center; width: 400px; margin: 0 auto">
        <a-button @click="downloadExcel"> <download-outlined />第一步：下载模板</a-button>
        <br />
        <br />
        <a-upload
          v-model:fileList="fileList"
          name="file"
          :multiple="false"
          action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
          accept=".xls,.xlsx"
          :before-upload="beforeUpload"
          @remove="handleRemove"
        >
          <a-button>
            <upload-outlined />
            第二步：选择文件
          </a-button>
        </a-upload>

        <br />
        <a-button @click="onImport">
          <ImportOutlined />
          第三步：开始导入
        </a-button>
      </div>
    </a-modal>
  </a-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, DeleteOutlined, ImportOutlined, ExportOutlined, UploadOutlined, DownloadOutlined } from '@ant-design/icons-vue';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { connectorApi } from '/@/api/business/connector/connector-api';
import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import TableOperator from '/@/components/support/table-operator/index.vue';
import RouteFormDrawer from './components/route-form-drawer.vue';
import RouteHistoryModal from './components/route-history-modal.vue';
import SmartHeaderCell from '/@/components/support/table-header-cell/index.vue';
import _ from 'lodash';

const columns = ref([
  {
    title: '路由名称',
    dataIndex: 'name',
    width: 150,
    sorter: true,
    resizable: true,
  },
  {
    title: '渠道',
    dataIndex: 'channel',
    width: 100,
    sorter: true,
    resizable: true,
  },
  {
    title: '源路径',
    dataIndex: 'sourcePath',
    width: 200,
    sorter: true,
    resizable: true,
  },
  {
    title: '目标地址',
    dataIndex: 'targetUrl',
    width: 250,
    ellipsis: true,
    resizable: true,
  },
  {
    title: '方法',
    dataIndex: 'method',
    width: 80,
    sorter: true,
    resizable: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    sorter: true,
    resizable: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 150,
    sorter: true,
    resizable: true,
  },
  {
    title: '操作',
    dataIndex: 'action',
    fixed: 'right',
    width: 200,
    resizable: true,
  },
]);

const queryFormState = {
  name: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10,
  sortItemList: [],
};

const queryForm = reactive(_.cloneDeep(queryFormState));
const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);
const formDrawer = ref();
const historyModal = ref();

function resetQuery() {
  let pageSize = queryForm.pageSize;
  Object.assign(queryForm, _.cloneDeep(queryFormState));
  queryForm.pageSize = pageSize;
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
    content: '确定要删除该路由吗?',
    okText: '删除',
    okType: 'danger',
    onOk() {
      return new Promise(async (resolve, reject) => {
        try {
          await connectorApi.deleteRoute(record.id);
          message.success('删除成功');
          queryData();
          resolve();
        } catch (error) {
          reject(error);
        }
      });
    },
  });
}

// ---------------------------- 批量删除 ----------------------------
const selectedRowKeyList = ref([]);
function onSelectChange(selectedRowKeys) {
  selectedRowKeyList.value = selectedRowKeys;
}

function confirmBatchDelete() {
  Modal.confirm({
    title: '提示',
    content: '确定要删除选中路由吗?',
    okText: '删除',
    okType: 'danger',
    onOk: async () => {
      try {
        SmartLoading.show();
        await connectorApi.batchDelete(selectedRowKeyList.value);
        message.success('批量删除成功');
        queryData();
        selectedRowKeyList.value = [];
      } catch (e) {
        // error
      } finally {
        SmartLoading.hide();
      }
    },
  });
}

// ---------------------------- 导入导出 ----------------------------
const importModalShowFlag = ref(false);
const fileList = ref([]);

function showImportModal() {
  fileList.value = [];
  importModalShowFlag.value = true;
}

function hideImportModal() {
  importModalShowFlag.value = false;
}

function downloadExcel() {
  message.warning('演示环境：暂未提供模板下载');
}

function beforeUpload(file) {
  fileList.value = [...(fileList.value || []), file];
  return false;
}

function handleRemove(file) {
  const index = fileList.value.indexOf(file);
  const newFileList = fileList.value.slice();
  newFileList.splice(index, 1);
  fileList.value = newFileList;
}

async function onImport() {
  if (fileList.value.length === 0) {
    message.warning('请选择文件');
    return;
  }
  
  const formData = new FormData();
  formData.append('file', fileList.value[0].originFileObj);

  try {
    SmartLoading.show();
    let res = await connectorApi.importRoute(formData);
    message.success(res.msg);
    hideImportModal();
    queryData();
  } catch (e) {
    // error
  } finally {
    SmartLoading.hide();
  }
}

async function onExport() {
  try {
    await connectorApi.exportRoute();
  } catch (e) {
    // error
  }
}

// ---------------------------- 表格通用功能 ----------------------------
function onChange(pagination, filters, sorter, { action }) {
  if (action === 'sort') {
    const { order, field } = sorter;
    let column = camelToUnderscore(field);
    let findIndex = queryForm.sortItemList.findIndex((e) => e.column === column);
    if (findIndex !== -1) {
      queryForm.sortItemList.splice(findIndex, 1);
    }
    if (order) {
      let isAsc = order !== 'ascend';
      queryForm.sortItemList.push({
        column,
        isAsc,
      });
    }
    queryData();
  }
}

function camelToUnderscore(str) {
  return str.replace(/([A-Z])/g, '_$1').toLowerCase();
}

function handleResizeColumn(w, col) {
  columns.value.forEach((item) => {
    if (item.dataIndex === col.dataIndex) {
      item.width = Math.floor(w);
      item.dragAndDropFlag = true;
    }
  });
}

const yHeight = ref(0);
function resetGetHeight() {
  let doc = document.querySelector('.ant-form');
  let btn = document.querySelector('.smart-table-btn-block');
  let tableCell = document.querySelector('.ant-table-cell');
  let page = document.querySelector('.smart-query-table-page');
  let box = document.querySelector('.admin-content');
  
  if (!doc || !btn || !tableCell || !page || !box) return;

  setTimeout(() => {
    let dueHeight = doc.offsetHeight + 10 + 24 + btn.offsetHeight + 15 + tableCell.offsetHeight + page.offsetHeight + 20;
    yHeight.value = box.offsetHeight - dueHeight;
  }, 100);
}

onMounted(() => {
  queryData();
  resetGetHeight();
  window.addEventListener('resize', _.throttle(resetGetHeight, 200));
});
</script>
