<template>
  <a-modal
    v-model:open="visible"
    title="配置变更历史"
    width="1000px"
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
            <span>旧版本</span>
            <span>新版本</span>
          </div>
          <!-- We use a simple JSON viewer for now as diff2html might need more setup -->
          <div class="json-compare">
             <div class="json-box">
               <JsonViewer :value="selectedHistory.oldConfig" copyable sort expanded :expand-depth="3"/>
             </div>
             <div class="json-box">
               <JsonViewer :value="selectedHistory.newConfig" copyable sort expanded :expand-depth="3"/>
             </div>
          </div>
        </div>
        <div v-else class="empty-select">
          请选择左侧历史记录查看详情
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { connectorApi } from '/@/api/business/connector/connector-api';
import { JsonViewer } from 'vue3-json-viewer';
import 'vue3-json-viewer/dist/index.css';

const visible = ref(false);
const historyList = ref([]);
const selectedHistory = ref(null);

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
      padding: 10px;
      border-bottom: 1px solid #eee;
      span {
        flex: 1;
        text-align: center;
        font-weight: bold;
      }
    }
    
    .json-compare {
      flex: 1;
      display: flex;
      overflow: hidden;
      
      .json-box {
        flex: 1;
        overflow: auto;
        padding: 10px;
        border-right: 1px solid #eee;
        &:last-child {
          border-right: none;
        }
      }
    }
  }
}
</style>
