<template>
  <a-card title="连接器路由测试" :bordered="false">
    <a-form layout="vertical">
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
          <a-textarea
            v-model:value="form.body"
            placeholder="请输入 JSON 格式的请求体"
            :rows="6"
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
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { SendOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import axios from 'axios';
import { SmartLoading } from '/@/components/framework/smart-loading';

const loading = ref(false);
const activeTab = ref('params');
const response = ref(null);

const form = reactive({
  method: 'GET',
  url: '',
  params: [{ key: '', value: '' }],
  headers: [{ key: '', value: '' }],
  body: ''
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
