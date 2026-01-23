<template>
  <a-card title="连接器路由测试" :bordered="false">
    <a-form layout="vertical">
      <a-alert message="提示：选择已有路由可自动填充请求参数" type="info" show-icon style="margin-bottom: 16px" />
      
      <a-form-item label="选择路由 (可选)">
        <a-select
          v-model:value="selectedRouteId"
          show-search
          placeholder="请选择路由以自动填充参数"
          option-filter-prop="label"
          @change="handleRouteChange"
          allowClear
          :options="routeOptions"
        />
      </a-form-item>

      <a-row :gutter="16">
        <a-col :span="16">
          <a-form-item label="请求地址 (URL)">
            <a-input v-model:value="form.url" placeholder="请输入请求地址，例如: /proxy/your-path">
              <template #addonBefore>
                <a-select v-model:value="form.method" style="width: 100px">
                  <a-select-option value="GET">GET</a-select-option>
                  <a-select-option value="POST">POST</a-select-option>
                  <a-select-option value="PUT">PUT</a-select-option>
                  <a-select-option value="DELETE">DELETE</a-select-option>
                </a-select>
              </template>
            </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="操作">
            <a-button type="primary" @click="sendRequest" :loading="loading" block>
              <template #icon><SendOutlined /></template>
              发送请求
            </a-button>
          </a-form-item>
        </a-col>
      </a-row>

      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="params" tab="Query Params">
          <div v-for="(item, index) in form.params" :key="index" class="param-row">
            <a-input v-model:value="item.key" placeholder="Key" style="width: 200px" />
            <span class="separator">=</span>
            <a-input v-model:value="item.value" placeholder="Value" style="width: 200px" />
            <a-button type="link" danger @click="removeParam(index)"><DeleteOutlined /></a-button>
          </div>
          <a-button type="dashed" @click="addParam" block style="margin-top: 8px">
            <PlusOutlined /> 添加参数
          </a-button>
        </a-tab-pane>
        
        <a-tab-pane key="headers" tab="Headers">
          <div v-for="(item, index) in form.headers" :key="index" class="param-row">
            <a-input v-model:value="item.key" placeholder="Key" style="width: 200px" />
            <span class="separator">:</span>
            <a-input v-model:value="item.value" placeholder="Value" style="width: 200px" />
            <a-button type="link" danger @click="removeHeader(index)"><DeleteOutlined /></a-button>
          </div>
          <a-button type="dashed" @click="addHeader" block style="margin-top: 8px">
            <PlusOutlined /> 添加 Header
          </a-button>
        </a-tab-pane>

        <a-tab-pane key="body" tab="Body" v-if="['POST', 'PUT'].includes(form.method)">
          <div style="margin-bottom: 8px">
            <a-space>
              <a-select
                v-model:value="selectedModel"
                style="width: 200px"
                placeholder="选择AI模型"
                :options="modelOptions"
                size="small"
              />
              <a-button type="dashed" size="small" @click="generateBodyByAI" :loading="aiLoading">
                <template #icon><RobotOutlined /></template>
                AI 自动生成测试数据
              </a-button>
              <span v-if="aiStatus" style="color: #1890ff; font-size: 12px; margin-left: 8px">
                <a-spin size="small" style="margin-right: 4px"/> {{ aiStatus }}
              </span>
              <a-button size="small" @click="formatJsonBody">
                <template #icon><FormatPainterOutlined /></template>
                JSON 美化
              </a-button>
            </a-space>
          </div>
          <a-textarea
            ref="bodyTextareaRef"
            v-model:value="form.body"
            placeholder="请输入 JSON 格式的请求体"
            :rows="12"
          />
        </a-tab-pane>
      </a-tabs>
    </a-form>

    <a-divider orientation="left">响应结果</a-divider>

    <div v-if="response" class="response-area">
      <a-tag :color="response.status >= 200 && response.status < 300 ? 'success' : 'error'">
        Status: {{ response.status }} {{ response.statusText }}
      </a-tag>
      <a-tag color="blue">Time: {{ response.time }}ms</a-tag>
      
      <div class="response-body">
        <json-viewer
          :value="response.data"
          :expand-depth="5"
          copyable
          boxed
          sort
        ></json-viewer>
      </div>
    </div>
    <a-empty v-else description="暂无响应数据" />
  </a-card>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import { SendOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import axios from 'axios';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { connectorApi } from '/@/api/business/connector/connector-api';
import { localRead } from '/@/utils/local-util';
import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';

import { RobotOutlined, FormatPainterOutlined } from '@ant-design/icons-vue';

const loading = ref(false);
const aiLoading = ref(false);
const aiStatus = ref('');
const activeTab = ref('params');
const response = ref(null);
const bodyTextareaRef = ref(null);

const selectedModel = ref('doubao-seed-1-6-flash-250828');
const modelOptions = [
  { value: 'doubao-seed-1-8-251228', label: 'Doubao Seed 1.8' },
  { value: 'glm-4-7-251222', label: 'GLM-4' },
  { value: 'deepseek-v3-2-251201', label: 'DeepSeek V3' },
  { value: 'doubao-seed-code-preview-251028', label: 'Doubao Code Preview' },
  { value: 'doubao-seed-1-6-lite-251015', label: 'Doubao Seed 1.6 Lite' },
  { value: 'kimi-k2-thinking-251104', label: 'Kimi K2 Thinking' },
  { value: 'doubao-seed-1-6-flash-250828', label: 'Doubao Seed 1.6 Flash' }
];

const selectedRouteId = ref(undefined);
const routeOptions = ref([]);
const routeMap = new Map();

const form = reactive({
  method: 'GET',
  url: '',
  params: [{ key: '', value: '' }],
  headers: [{ key: '', value: '' }],
  body: ''
});

async function loadRoutes() {
  try {
    const res = await connectorApi.queryRoutePage({
      pageNum: 1,
      pageSize: 500
    });
    const list = res.data.list || [];
    routeOptions.value = list.map(item => ({
      label: `【${item.method}】${item.name} (${item.sourcePath})`,
      value: item.id,
      ...item
    }));
    
    list.forEach(item => routeMap.set(item.id, item));
  } catch (e) {
    console.error(e);
  }
}

function getAuthHeader() {
  const token = localRead(LocalStorageKeyConst.USER_TOKEN);
  if (token) {
    return { key: 'Authorization', value: `Bearer ${token}` };
  }
  return null;
}

function resetHeaders() {
  const authHeader = getAuthHeader();
  if (authHeader) {
    form.headers = [authHeader, { key: '', value: '' }];
  } else {
    form.headers = [{ key: '', value: '' }];
  }
}

function handleRouteChange(value) {
  if (!value) return;
  
  const route = routeMap.get(value);
  if (route) {
    form.method = route.method;
    
    const path = route.sourcePath.startsWith('/') ? route.sourcePath : '/' + route.sourcePath;
    form.url = `/connector/proxy${path}`;
    
    if (['POST', 'PUT'].includes(route.method)) {
      try {
        // Try to be smart if mappingConfig has body structure (future enhancement)
        form.body = JSON.stringify({
          mock: true,
          timestamp: Date.now()
        }, null, 2);
      } catch (e) {
        form.body = '{}';
      }
    } else {
      form.body = '';
    }
    
    // Reset params and headers
    form.params = [{ key: '', value: '' }];
    resetHeaders();
    
    message.success(`已自动填充路由【${route.name}】参数`);
  }
}

async function generateBodyByAI() {
  if (!selectedRouteId.value) {
    message.warning('请先选择一个路由');
    return;
  }

  const route = routeMap.get(selectedRouteId.value);
  if (!route) return;

  aiLoading.value = true;
  aiStatus.value = '准备连接 AI...';
  form.body = ''; // 清空当前内容，准备接收流式数据
  
  try {
    const prompt = `你是一个专业的Mock数据生成器。请根据以下API路由信息，生成一个合法的JSON请求体。
    路由名称: ${route.name}
    源路径: ${route.sourcePath}
    方法: ${route.method}
    映射配置: ${JSON.stringify(route.mappingConfig || {})}
    
    要求：
    1. 必须只返回纯JSON字符串，严禁包含Markdown代码块标记（如 \`\`\`json）或任何解释性文字。
    2. 数据生成的丰富性要求：
       - 字符串：使用真实的业务场景词汇，避免 meaningless 的 "test" 或 "string"。
       - 数字：根据字段含义生成合理的范围值（如价格保留2位小数，ID为正整数）。
       - 数组/列表：如果遇到数组字段，请至少生成 3-5 条不同的明细数据，以覆盖列表场景。
       - 日期：使用当前或未来一年的 ISO8601 格式时间。
       - 枚举：根据字段名推测可能的枚举值（如 status: "ACTIVE", "PENDING"）。
    3. 字段推断：如果映射配置中没有明确字段定义，请根据源路径和路由名称推断可能需要的业务字段（如订单接口包含 items, totalAmount 等）。
    `;

    // 使用 fetch API 进行流式请求
    const response = await fetch('https://ark.cn-beijing.volces.com/api/v3/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer 5696e3b5-03d6-406d-ba46-daec2c8a05c9'
      },
      body: JSON.stringify({
        model: selectedModel.value,
        stream: true, // 开启流式传输
        messages: [
          { role: 'system', content: '你是一个严格遵循指令的JSON数据生成助手，只输出JSON。' },
          { role: 'user', content: prompt }
        ]
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let fullContent = '';
    let fullReasoning = '';
    let buffer = '';

    aiStatus.value = 'AI 正在生成...';

    const updateBody = () => {
      if (fullReasoning) {
        form.body = `> AI 思考过程:\n${fullReasoning.split('\n').map(l => `> ${l}`).join('\n')}\n\n${fullContent}`;
      } else {
        form.body = fullContent;
      }
      
      // 自动滚动到底部
      nextTick(() => {
        const textarea = bodyTextareaRef.value?.$el?.querySelector('textarea') || bodyTextareaRef.value?.$el;
        if (textarea && textarea.scrollTop !== undefined) {
          textarea.scrollTop = textarea.scrollHeight;
        }
      });
    };

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value, { stream: true });
      buffer += chunk;
      
      const lines = buffer.split('\n');
      // 保留最后一行，因为它可能不完整，等待下一个 chunk
      buffer = lines.pop() || '';

      for (const line of lines) {
        const trimmedLine = line.trim();
        if (!trimmedLine || !trimmedLine.startsWith('data:')) continue;
        
        const dataStr = trimmedLine.slice(5).trim(); // 兼容 'data:' 和 'data: '
        if (dataStr === '[DONE]') continue;
        
        try {
          const data = JSON.parse(dataStr);
          const delta = data.choices[0]?.delta || {};
          const content = delta.content || '';
          const reasoning = delta.reasoning_content || '';
          
          let updated = false;
          if (reasoning) {
             aiStatus.value = 'AI 正在思考...';
             fullReasoning += reasoning;
             updated = true;
          }

          if (content) {
            aiStatus.value = 'AI 正在生成...';
            fullContent += content;
            updated = true;
          }

          if (updated) {
            updateBody();
          }
        } catch (e) {
          // 忽略解析错误，可能是因为数据包不完整
          console.debug('JSON parse error in stream:', e);
        }
      }
    }

    // 处理剩余的 buffer（如果有）
    if (buffer && buffer.startsWith('data:')) {
      try {
        const dataStr = buffer.slice(5).trim();
        if (dataStr !== '[DONE]') {
          const data = JSON.parse(dataStr);
          const delta = data.choices[0]?.delta || {};
          const content = delta.content || '';
          const reasoning = delta.reasoning_content || '';
          
          if (reasoning) fullReasoning += reasoning;
          if (content) fullContent += content;
          updateBody();
        }
      } catch (e) {}
    }

    // 流式传输结束后，尝试美化 JSON
    if (fullContent) {
      aiStatus.value = '生成完成，正在美化...';
      let cleanContent = fullContent.replace(/^```json\s*/, '').replace(/\s*```$/, '');
      try {
        const jsonObj = JSON.parse(cleanContent);
        form.body = JSON.stringify(jsonObj, null, 2);
        message.success('AI 生成数据成功');
      } catch (e) {
        console.warn('生成的最终内容不是有效的 JSON，无法自动格式化');
        // 如果格式化失败，保留原始内容（包含思考过程吗？通常我们只想要结果）
        // 但如果只有思考过程没有结果，那就保留思考过程
        form.body = cleanContent;
      }
    }

  } catch (e) {
    console.error(e);
    message.error('AI生成失败: ' + e.message);
  } finally {
    aiLoading.value = false;
    aiStatus.value = '';
  }
}

function formatJsonBody() {
  if (!form.body) return;
  try {
    const jsonObj = JSON.parse(form.body);
    form.body = JSON.stringify(jsonObj, null, 2);
    message.success('格式化成功');
  } catch (e) {
    message.error('JSON 格式错误，无法格式化');
  }
}

onMounted(() => {
  loadRoutes();
  resetHeaders();
});

const addParam = () => form.params.push({ key: '', value: '' });
const removeParam = (index) => form.params.splice(index, 1);

const addHeader = () => form.headers.push({ key: '', value: '' });
const removeHeader = (index) => form.headers.splice(index, 1);

const sendRequest = async () => {
  if (!form.url) {
    message.warning('请输入请求地址');
    return;
  }

  loading.value = true;
  const startTime = Date.now();
  
  try {
    // Construct params object
    const params = {};
    form.params.forEach(p => {
      if (p.key) params[p.key] = p.value;
    });

    // Construct headers object
    const headers = {};
    form.headers.forEach(h => {
      if (h.key) headers[h.key] = h.value;
    });
    // Add special header to indicate test mode if needed
    headers['X-Connector-Test'] = 'true';

    // Parse body if present
    let data = null;
    if (form.body && ['POST', 'PUT'].includes(form.method)) {
      try {
        data = JSON.parse(form.body);
      } catch (e) {
        message.error('Body JSON 格式错误');
        loading.value = false;
        return;
      }
    }

    // Use a direct axios instance or the project's request utility
    // Here we use the project's configured axios base URL implicitly if we use relative path
    // But for "proxy" calls, we might want to go through /proxy/...
    // If the user inputs "/proxy/test", and base URL is "/api", it becomes "/api/proxy/test"
    
    // We'll use a fresh axios call to allow more flexibility, but using relative URL to respect proxy
    const res = await axios({
      method: form.method,
      url: form.url.startsWith('http') ? form.url : import.meta.env.VITE_APP_API_URL + form.url,
      params,
      headers,
      data
    });

    response.value = {
      status: res.status,
      statusText: res.statusText,
      data: res.data,
      headers: res.headers,
      time: Date.now() - startTime
    };
    
    message.success('请求成功');
  } catch (err) {
    console.error(err);
    response.value = {
      status: err.response?.status || 500,
      statusText: err.response?.statusText || 'Error',
      data: err.response?.data || err.message,
      headers: err.response?.headers || {},
      time: Date.now() - startTime
    };
    message.error('请求失败: ' + (err.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.param-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.separator {
  margin: 0 8px;
  font-weight: bold;
}
.response-area {
  margin-top: 16px;
}
.response-body {
  margin-top: 16px;
}
</style>
