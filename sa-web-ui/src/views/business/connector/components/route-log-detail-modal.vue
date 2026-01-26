<template>
  <a-modal
    v-model:visible="visible"
    title="请求日志详情"
    width="800px"
    :footer="null"
  >
    <a-descriptions bordered :column="1" size="small">
      <a-descriptions-item label="请求时间">{{ detail.createTime }}</a-descriptions-item>
      <a-descriptions-item label="路由名称">{{ detail.routeName }}</a-descriptions-item>
      <a-descriptions-item label="渠道">{{ detail.channel }}</a-descriptions-item>
      <a-descriptions-item label="请求路径">{{ detail.requestPath }}</a-descriptions-item>
      <a-descriptions-item label="状态码">
        <a-tag :color="detail.statusCode >= 200 && detail.statusCode < 300 ? 'success' : 'error'">
          {{ detail.statusCode }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="耗时">{{ detail.latencyMs }} ms</a-descriptions-item>
      <a-descriptions-item label="错误信息" v-if="detail.errorMsg">
        <span style="color: red">{{ detail.errorMsg }}</span>
      </a-descriptions-item>
    </a-descriptions>

    <a-tabs v-model:activeKey="activeTab" style="margin-top: 20px">
      <a-tab-pane key="original" tab="原始入参">
        <div class="code-action">
          <a-button type="link" size="small" @click="onCopy(detail.originalParams)">
            <template #icon><CopyOutlined /></template>
            复制JSON
          </a-button>
        </div>
        <div class="code-box">
          <pre>{{ formatJson(detail.originalParams) }}</pre>
        </div>
      </a-tab-pane>
      <a-tab-pane key="transformed" tab="转换后入参">
        <div class="code-action">
          <a-button type="link" size="small" @click="onCopy(detail.transformedParams)">
            <template #icon><CopyOutlined /></template>
            复制JSON
          </a-button>
        </div>
        <div class="code-box">
          <pre>{{ formatJson(detail.transformedParams) }}</pre>
        </div>
      </a-tab-pane>
      <a-tab-pane key="response" tab="响应数据">
        <div class="code-action">
          <a-button type="link" size="small" @click="onCopy(detail.responseData)">
            <template #icon><CopyOutlined /></template>
            复制JSON
          </a-button>
        </div>
        <div class="code-box">
          <pre>{{ formatJson(detail.responseData) }}</pre>
        </div>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { message } from 'ant-design-vue';

const visible = ref(false);
const detail = ref({});
const activeTab = ref('response');

function show(record) {
  detail.value = record;
  visible.value = true;
  activeTab.value = 'response';
}

function formatJson(str) {
  if (!str) return '无';
  try {
    const obj = JSON.parse(str);
    return JSON.stringify(obj, null, 2);
  } catch (e) {
    return str;
  }
}

function onCopy(content) {
  if (!content) {
    message.warning('内容为空，无法复制');
    return;
  }
  const text = formatJson(content);
  // 创建临时textarea元素
  const textarea = document.createElement('textarea');
  textarea.value = text;
  document.body.appendChild(textarea);
  textarea.select();
  try {
    document.execCommand('copy');
    message.success('复制成功');
  } catch (err) {
    message.error('复制失败');
  }
  document.body.removeChild(textarea);
}

defineExpose({
  show,
});
</script>

<style scoped>
.code-action {
  text-align: right;
  margin-bottom: 5px;
}
.code-box {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  max-height: 400px;
  overflow: auto;
}
pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
