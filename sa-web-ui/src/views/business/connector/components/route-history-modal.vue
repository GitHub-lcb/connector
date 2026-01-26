<template>
  <a-modal
    v-model:open="visible"
    title="配置变更历史"
    width="1200px"
    :footer="null"
    @cancel="onClose"
  >
    <div class="history-container">
      <div class="history-list">
        <a-list :data-source="historyList" bordered size="small">
          <template #renderItem="{ item }">
            <a-list-item 
              class="history-item" 
              :class="{ active: selectedHistory?.id === item.id }"
              @click="selectHistory(item)"
            >
              <div class="history-item-content">
                <span class="time">{{ item.createTime }}</span>
                <span class="user">修改人: {{ item.changedBy || '未知' }}</span>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </div>
      <div class="history-detail">
        <div v-if="selectedHistory" class="diff-viewer">
          <div class="diff-header">
            <div class="header-title">配置对比</div>
            <div class="header-actions">
               <a-space>
                  <a-radio-group v-model:value="diffMode" size="small" button-style="solid">
                    <a-radio-button value="line-by-line">行内对比</a-radio-button>
                    <a-radio-button value="side-by-side">左右对比</a-radio-button>
                  </a-radio-group>
                  <a-button type="primary" size="small" danger @click="handleRollback">回滚至此版本</a-button>
               </a-space>
            </div>
          </div>
          <div class="diff-content" v-html="diffHtml"></div>
        </div>
        <div v-else class="empty-select">
          请选择左侧历史记录查看详情
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, computed } from 'vue';
import { connectorApi } from '/@/api/business/connector/connector-api';
import { message, Modal } from 'ant-design-vue';
import * as Diff from 'diff';
import { html } from 'diff2html';
import 'diff2html/bundles/css/diff2html.min.css';

const visible = ref(false);
const historyList = ref([]);
const selectedHistory = ref(null);
const diffMode = ref('side-by-side');

const diffHtml = computed(() => {
  if (!selectedHistory.value) return '';
  
  const oldConfig = selectedHistory.value.oldConfig || {};
  const newConfig = selectedHistory.value.newConfig || {};
  
  const oldStr = JSON.stringify(oldConfig, null, 2);
  const newStr = JSON.stringify(newConfig, null, 2);
  
  const patch = Diff.createTwoFilesPatch(
    '旧版本',
    '新版本',
    oldStr,
    newStr
  );
  
  return html(patch, {
    drawFileList: false,
    matching: 'lines',
    outputFormat: diffMode.value,
  });
});

async function showModal(routeId) {
  visible.value = true;
  selectedHistory.value = null;
  historyList.value = [];
  try {
    const res = await connectorApi.queryHistory(routeId);
    historyList.value = res.data;
    if (historyList.value.length > 0) {
      selectedHistory.value = historyList.value[0];
    }
  } catch (e) {
    console.error(e);
  }
}

function selectHistory(item) {
  selectedHistory.value = item;
}

function onClose() {
  visible.value = false;
}

function handleRollback() {
  if (!selectedHistory.value) return;
  
  Modal.confirm({
    title: '确认回滚',
    content: '确定要回滚到此版本吗？这将覆盖当前的配置，并生成一条新的历史记录。',
    okText: '确认回滚',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      try {
        await connectorApi.rollback(selectedHistory.value.id);
         message.success('回滚成功');
         onClose();
         emit('reloadList');
       } catch (e) {
         console.error(e);
      }
    }
  });
}

defineExpose({
  showModal
});
</script>

<style scoped lang="less">
.history-container {
  display: flex;
  height: 600px;
  border: 1px solid #f0f0f0;
}

.history-list {
  width: 250px;
  border-right: 1px solid #f0f0f0;
  overflow-y: auto;
  
  .history-item {
    cursor: pointer;
    padding: 10px;
    border-bottom: 1px solid #f0f0f0;
    
    &:hover {
      background-color: #f5f5f5;
    }
    &.active {
      background-color: #e6f7ff;
    }
    
    .history-item-content {
      display: flex;
      flex-direction: column;
      .time {
        font-weight: bold;
        margin-bottom: 4px;
      }
      .user {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.history-detail {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  
  .empty-select {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    color: #999;
  }
  
  .diff-viewer {
    height: 100%;
    display: flex;
    flex-direction: column;
    
    .diff-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 20px;
      border-bottom: 1px solid #eee;
      background: #fafafa;
      
      .header-title {
        font-weight: bold;
        font-size: 16px;
      }
    }
    
    .diff-content {
      flex: 1;
      overflow: auto;
      padding: 10px;
      
      :deep(.d2h-file-header) {
        display: none;
      }
      
      :deep(.d2h-file-wrapper) {
        border: none;
        margin-bottom: 0;
      }
    }
  }
}
</style>
