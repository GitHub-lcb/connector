<template>
  <div class="connector-secret-key-list">
    <a-form class="smart-query-form">
      <a-row class="smart-query-form-row">
        <a-form-item label="标题" class="smart-query-form-item">
          <a-input style="width: 200px" v-model:value="queryForm.title" placeholder="请输入标题" />
        </a-form-item>
        <a-form-item label="Access Key" class="smart-query-form-item">
          <a-input style="width: 200px" v-model:value="queryForm.accessKey" placeholder="请输入Access Key" />
        </a-form-item>
        <a-form-item label="状态" class="smart-query-form-item">
          <a-select style="width: 120px" v-model:value="queryForm.status" placeholder="全部" allowClear>
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
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
          <a-button type="primary" @click="addKey">
            <template #icon>
              <PlusOutlined />
            </template>
            新建密钥
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
        rowKey="keyId"
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
            <a-tag :color="text === 1 ? 'success' : 'error'">{{ text === 1 ? '启用' : '禁用' }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button type="link" @click="editKey(record)">编辑</a-button>
              <a-popconfirm title="确定要删除该密钥吗？" @confirm="deleteKey(record.keyId)">
                <a-button danger type="link">删除</a-button>
              </a-popconfirm>
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
    </a-card>

    <SecretKeyFormModal ref="formModal" @reloadList="queryData" />
    
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
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { SearchOutlined, ReloadOutlined, PlusOutlined, DeleteOutlined, ImportOutlined, ExportOutlined, UploadOutlined, DownloadOutlined } from '@ant-design/icons-vue';
import { connectorSecretKeyApi } from '/@/api/business/connector/connector-secret-key-api';
import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import TableOperator from '/@/components/support/table-operator/index.vue';
import SecretKeyFormModal from './components/secret-key-form-modal.vue';
import SmartHeaderCell from '/@/components/support/table-header-cell/index.vue';
import _ from 'lodash';

const queryForm = reactive({
  title: '',
  accessKey: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10,
  sortItemList: []
});

const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);
const formModal = ref();

const columns = ref([
  {
    title: 'ID',
    dataIndex: 'keyId',
    width: 60,
    sorter: true,
    resizable: true,
  },
  {
    title: '标题',
    dataIndex: 'title',
    width: 150,
    sorter: true,
    resizable: true,
  },
  {
    title: 'Access Key',
    dataIndex: 'accessKey',
    width: 200,
    sorter: true,
    resizable: true,
  },
  {
    title: 'Secret Key',
    dataIndex: 'secretKey',
    width: 250,
    ellipsis: true,
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
    title: '备注',
    dataIndex: 'remark',
    ellipsis: true,
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
    width: 120,
    resizable: true,
  },
]);

async function queryData() {
  tableLoading.value = true;
  try {
    const res = await connectorSecretKeyApi.queryPage(queryForm);
    tableData.value = res.data.list;
    total.value = res.data.total;
  } catch (e) {
    console.error(e);
  } finally {
    tableLoading.value = false;
  }
}

function onSearch() {
  queryForm.pageNum = 1;
  queryData();
}

function resetQuery() {
  queryForm.title = '';
  queryForm.accessKey = '';
  queryForm.status = undefined;
  queryForm.sortItemList = [];
  onSearch();
}

function addKey() {
  formModal.value.show();
}

function editKey(record) {
  formModal.value.show(record);
}

async function deleteKey(keyId) {
  try {
    await connectorSecretKeyApi.delete(keyId);
    message.success('删除成功');
    queryData();
  } catch (e) {
    console.error(e);
  }
}

// ---------------------------- 批量删除 ----------------------------
const selectedRowKeyList = ref([]);
function onSelectChange(selectedRowKeys) {
  selectedRowKeyList.value = selectedRowKeys;
}

function confirmBatchDelete() {
  Modal.confirm({
    title: '提示',
    content: '确定要删除选中密钥吗?',
    okText: '删除',
    okType: 'danger',
    onOk: async () => {
      try {
        await connectorSecretKeyApi.batchDelete(selectedRowKeyList.value);
        message.success('批量删除成功');
        queryData();
        selectedRowKeyList.value = [];
      } catch (e) {
        // error
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
    let res = await connectorSecretKeyApi.importKey(formData);
    message.success(res.msg);
    hideImportModal();
    queryData();
  } catch (e) {
    // error
  }
}

async function onExport() {
  try {
    await connectorSecretKeyApi.exportKey();
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
