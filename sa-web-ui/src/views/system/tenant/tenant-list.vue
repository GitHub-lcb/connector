<template>
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="租户名称" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.tenantName" placeholder="请输入租户名称" />
      </a-form-item>
      <a-form-item label="联系人" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.contactPerson" placeholder="请输入联系人" />
      </a-form-item>
      <a-form-item label="联系电话" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.contactPhone" placeholder="请输入联系电话" />
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
        <a-button type="primary" @click="addTenant">
          <template #icon>
            <PlusOutlined />
          </template>
          新建租户
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
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.SYSTEM.TENANT" :refresh="queryPage" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <a-table
      size="small"
      :loading="tableLoading"
      :columns="columns"
      :dataSource="tableData"
      :pagination="false"
      rowKey="tenantId"
      bordered
      :scroll="{ x: 1000, y: yHeight }"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
      @change="onChange"
      @resizeColumn="handleResizeColumn"
    >
      <template #headerCell="{ column }">
        <SmartHeaderCell v-model:value="queryForm[column.filterOptions?.key || column.dataIndex]" :column="column" @change="queryPage" />
      </template>
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'status'">
          <a-tag color="success" v-if="text === 1">正常</a-tag>
          <a-tag color="error" v-else>禁用</a-tag>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="updateTenant(record)" type="link">编辑</a-button>
            <a-button @click="deleteTenant(record)" type="link" danger>删除</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="['10', '20', '30', '40', '50']"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryPage"
        @showSizeChange="queryPage"
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <TenantForm ref="tenantFormRef" @refresh="queryPage" />

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
  import { tenantApi } from '/@/api/system/tenant-api';
  import TenantForm from './components/tenant-form.vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import SmartHeaderCell from '/@/components/support/table-header-cell/index.vue';
  import _ from 'lodash';

  // ---------------------------- 表格列 ----------------------------
  const columns = ref([
    {
      title: '租户ID',
      dataIndex: 'tenantId',
      width: 80,
      sorter: true,
      resizable: true,
    },
    {
      title: '租户名称',
      dataIndex: 'tenantName',
      resizable: true,
      filterOptions: {
        type: 'input',
        key: 'tenantName',
      },
    },
    {
      title: '联系人',
      dataIndex: 'contactPerson',
      resizable: true,
      filterOptions: {
        type: 'input',
        key: 'contactPerson',
      },
    },
    {
      title: '联系电话',
      dataIndex: 'contactPhone',
      resizable: true,
      filterOptions: {
        type: 'input',
        key: 'contactPhone',
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      resizable: true,
    },
    {
      title: '过期时间',
      dataIndex: 'expireTime',
      sorter: true,
      resizable: true,
      width: 150,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      resizable: true,
      width: 150,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 150,
      resizable: true,
    },
  ]);

  const tableLoading = ref(false);
  const tenantFormRef = ref();
  const tableData = ref([]);
  const total = ref(0);

  // ---------------------------- 查询表单 ----------------------------
  const queryFormState = {
    tenantName: '',
    contactPerson: '',
    contactPhone: '',
    pageNum: 1,
    pageSize: 10,
    sortItemList: [],
  };
  const queryForm = reactive(_.cloneDeep(queryFormState));

  async function queryPage() {
    try {
      tableLoading.value = true;
      const res = await tenantApi.queryPage(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      // smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function onSearch() {
    queryForm.pageNum = 1;
    queryPage();
  }

  function resetQuery() {
    let pageSize = queryForm.pageSize;
    Object.assign(queryForm, _.cloneDeep(queryFormState));
    queryForm.pageSize = pageSize;
    onSearch();
  }

  // ---------------------------- 增删改 ----------------------------
  function addTenant() {
    tenantFormRef.value.showModal();
  }

  function updateTenant(record) {
    tenantFormRef.value.showModal(record);
  }

  function deleteTenant(record) {
    Modal.confirm({
      title: '提示',
      content: `确定要删除租户【${record.tenantName}】吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          SmartLoading.show();
          await tenantApi.delete(record.tenantId);
          message.success('删除成功');
          queryPage();
        } catch (e) {
          // error
        } finally {
          SmartLoading.hide();
        }
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
      content: '确定要删除选中租户吗?',
      okText: '删除',
      okType: 'danger',
      onOk: async () => {
        try {
          SmartLoading.show();
          await tenantApi.batchDelete(selectedRowKeyList.value);
          message.success('批量删除成功');
          queryPage();
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
      let res = await tenantApi.importTenant(formData);
      message.success(res.msg);
      hideImportModal();
      queryPage();
    } catch (e) {
      // error
    } finally {
      SmartLoading.hide();
    }
  }

  async function onExport() {
    try {
      await tenantApi.exportTenant();
    } catch (e) {
      // error
    }
  }

  // ---------------------------- 表格通用功能 (排序、列宽、高度) ----------------------------
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
      queryPage();
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
    // 搜索部分高度
    let doc = document.querySelector('.ant-form');
    // 按钮部分高度
    let btn = document.querySelector('.smart-table-btn-block');
    // 表格头高度
    let tableCell = document.querySelector('.ant-table-cell');
    // 分页高度
    let page = document.querySelector('.smart-query-table-page');
    // 内容区总高度
    let box = document.querySelector('.admin-content');
    
    if (!doc || !btn || !tableCell || !page || !box) return;

    setTimeout(() => {
      let dueHeight = doc.offsetHeight + 10 + 24 + btn.offsetHeight + 15 + tableCell.offsetHeight + page.offsetHeight + 20;
      yHeight.value = box.offsetHeight - dueHeight;
    }, 100);
  }

  onMounted(() => {
    queryPage();
    resetGetHeight();
    window.addEventListener('resize', _.throttle(resetGetHeight, 200));
  });
</script>