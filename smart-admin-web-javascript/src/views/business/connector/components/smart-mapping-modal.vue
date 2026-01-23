<template>
  <a-modal
    v-model:open="visible"
    title="AI 智能字段映射助手"
    :width="900"
    @ok="handleOk"
    @cancel="handleCancel"
    :ok-text="aiStatus ? '正在生成...' : '应用映射'"
    :ok-button-props="{ disabled: !generatedMappings.length || !!aiStatus }"
    :maskClosable="false"
  >
    <a-alert
      message="使用说明"
      description="请在左侧粘贴【源数据 JSON】，在右侧粘贴【目标数据 JSON】。AI 将自动分析字段含义并生成映射规则。"
      type="info"
      show-icon
      style="margin-bottom: 16px"
    />

    <a-row :gutter="16">
      <a-col :span="12">
        <div class="label-row">
          <span>源数据示例 (Source JSON)</span>
          <a-button type="link" size="small" @click="formatJson('source')">美化</a-button>
        </div>
        <a-textarea
          v-model:value="sourceJson"
          placeholder='例如: {"userName": "zhangsan", "age": 18}'
          :rows="10"
          style="font-family: monospace"
        />
      </a-col>
      <a-col :span="12">
        <div class="label-row">
          <span>目标数据示例 (Target JSON)</span>
          <a-button type="link" size="small" @click="formatJson('target')">美化</a-button>
        </div>
        <a-textarea
          v-model:value="targetJson"
          placeholder='例如: {"user_name": "", "user_age": 0}'
          :rows="10"
          style="font-family: monospace"
        />
      </a-col>
    </a-row>

    <div class="action-bar">
      <a-space>
        <span>选择模型:</span>
        <a-select v-model:value="selectedModel" style="width: 220px">
          <a-select-option value="doubao-seed-1-6-flash-250828">Doubao Seed 1.6 Flash</a-select-option>
          <a-select-option value="doubao-lite-32k-240828">Doubao Lite 32k</a-select-option>
          <a-select-option value="deepseek-v3-2-251201">DeepSeek V3</a-select-option>
          <a-select-option value="glm-4-7-251222">GLM-4</a-select-option>
        </a-select>
        <a-button type="primary" @click="startGenerate" :loading="!!aiStatus">
          <template #icon><RobotOutlined /></template>
          开始生成映射
        </a-button>
      </a-space>
    </div>

    <!-- AI 生成状态与结果展示 -->
    <div v-if="aiStatus || generatedMappings.length > 0" class="result-area">
      <div v-if="aiStatus" class="status-bar">
        <a-spin size="small" style="margin-right: 8px" />
        <span class="status-text">{{ aiStatus }}</span>
      </div>
      
      <!-- 思考过程展示 -->
      <div v-if="reasoningContent" class="reasoning-box">
        <div class="box-title">AI 思考过程:</div>
        <div class="box-content">{{ reasoningContent }}</div>
      </div>

      <!-- 结果预览 -->
      <div v-if="generatedMappings.length > 0" class="mapping-preview">
        <div class="box-title">
          已生成 {{ generatedMappings.length }} 条映射规则
          <span style="font-weight: normal; font-size: 12px; color: #999; margin-left: 8px">
            (点击"应用映射"按钮保存)
          </span>
        </div>
        <a-table
          size="small"
          :dataSource="generatedMappings"
          :columns="previewColumns"
          :pagination="false"
          :scroll="{ y: 200 }"
          rowKey="source"
        />
      </div>
    </div>

  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { RobotOutlined } from '@ant-design/icons-vue';

const emit = defineEmits(['ok']);

const visible = ref(false);
const sourceJson = ref('');
const targetJson = ref('');
const selectedModel = ref('doubao-seed-1-6-flash-250828');
const aiStatus = ref('');
const reasoningContent = ref('');
const generatedMappings = ref([]);

const previewColumns = [
  { title: '源字段', dataIndex: 'source', width: 150 },
  { title: '目标字段', dataIndex: 'target', width: 150 },
  { title: '类型', dataIndex: 'targetType', width: 80 },
  { title: '默认值', dataIndex: 'defaultValue', width: 100 },
];

function show() {
  visible.value = true;
  sourceJson.value = '';
  targetJson.value = '';
  aiStatus.value = '';
  reasoningContent.value = '';
  generatedMappings.value = [];
}

function handleCancel() {
  visible.value = false;
}

function handleOk() {
  emit('ok', generatedMappings.value);
  visible.value = false;
}

function formatJson(type) {
  try {
    const val = type === 'source' ? sourceJson.value : targetJson.value;
    if (!val) return;
    const obj = JSON.parse(val);
    const formatted = JSON.stringify(obj, null, 2);
    if (type === 'source') sourceJson.value = formatted;
    else targetJson.value = formatted;
  } catch (e) {
    message.error('JSON 格式错误');
  }
}

async function startGenerate() {
  if (!sourceJson.value || !targetJson.value) {
    message.warning('请先填写源数据和目标数据 JSON');
    return;
  }

  aiStatus.value = '准备连接 AI...';
  reasoningContent.value = '';
  generatedMappings.value = [];
  
  let fullContent = '';
  let buffer = '';

  try {
    const prompt = `你是一个数据集成专家。请分析提供的【源数据 JSON】和【目标数据 JSON】，生成字段映射规则。
    
    【源数据 JSON】:
    ${sourceJson.value}

    【目标数据 JSON】:
    ${targetJson.value}

    要求：
    1. 分析两边数据的字段含义，找到最佳匹配。
    2. 如果字段名不同但含义相同（如 userName 和 user_name），请建立映射。
    3. 如果源数据和目标数据包含相同的字段名（如 id 和 id），也必须生成映射规则。
    4. 针对嵌套结构和数组明细的处理规则：
       - 如果是对象中的字段，使用点号连接，例如 "user.name"。
       - 如果是数组中的字段（明细行），必须使用 "[*]" 作为数组占位符，例如 "items[*].price"。
       - 请准确识别数组结构，不要遗漏 "[*]" 标识，这对系统识别明细行至关重要。
    5. 推断目标字段的类型 (string, integer, decimal, boolean, array, object)。
    6. 严格输出为 JSON 数组格式，不要包含 markdown 标记。
    7. 输出结构示例：
    [
      { "source": "userName", "target": "user_name", "targetType": "string", "defaultValue": null },
      { "source": "order.id", "target": "order_info.id", "targetType": "integer", "defaultValue": null },
      { "source": "items[*].price", "target": "details[*].unit_price", "targetType": "decimal", "defaultValue": 0 }
    ]
    `;

    const response = await fetch('https://ark.cn-beijing.volces.com/api/v3/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer 5696e3b5-03d6-406d-ba46-daec2c8a05c9'
      },
      body: JSON.stringify({
        model: selectedModel.value,
        stream: true,
        messages: [
          { role: 'system', content: '你是一个严格的JSON数据处理助手。' },
          { role: 'user', content: prompt }
        ]
      })
    });

    if (!response.ok) throw new Error('API request failed');

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');

    aiStatus.value = 'AI 正在分析...';

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value, { stream: true });
      buffer += chunk;
      
      const lines = buffer.split('\n');
      buffer = lines.pop() || '';

      for (const line of lines) {
        const trimmed = line.trim();
        if (!trimmed.startsWith('data:')) continue;
        const dataStr = trimmed.slice(5).trim();
        if (dataStr === '[DONE]') continue;

        try {
          const data = JSON.parse(dataStr);
          const delta = data.choices[0]?.delta || {};
          const content = delta.content || '';
          const reasoning = delta.reasoning_content || '';

          if (reasoning) {
            aiStatus.value = 'AI 正在思考...';
            reasoningContent.value += reasoning;
          }
          if (content) {
            aiStatus.value = 'AI 正在生成映射...';
            fullContent += content;
          }
        } catch (e) {}
      }
    }

    // 处理剩余 buffer
    if (buffer && buffer.startsWith('data:')) {
       try {
          const dataStr = buffer.slice(5).trim();
          if (dataStr !== '[DONE]') {
            const data = JSON.parse(dataStr);
            const content = data.choices[0]?.delta?.content || '';
            if (content) fullContent += content;
          }
       } catch(e) {}
    }

    aiStatus.value = '解析结果...';
    
    // 清理并解析 JSON
    let cleanJson = fullContent.replace(/^```json\s*/, '').replace(/\s*```$/, '');
    // 有时候 AI 可能会返回解释性文字，尝试提取第一个 [ ... ]
    const arrayMatch = cleanJson.match(/\[[\s\S]*\]/);
    if (arrayMatch) {
      cleanJson = arrayMatch[0];
    }

    try {
      const mappings = JSON.parse(cleanJson);
      if (Array.isArray(mappings)) {
        generatedMappings.value = mappings;
        aiStatus.value = ''; // 完成
        message.success(`成功生成 ${mappings.length} 条映射规则`);
      } else {
        throw new Error('返回的不是数组格式');
      }
    } catch (e) {
      console.error('JSON Parse Error:', cleanJson);
      message.error('AI 生成的数据格式有误，无法解析');
      aiStatus.value = '解析失败';
    }

  } catch (e) {
    console.error(e);
    message.error('AI 请求失败: ' + e.message);
    aiStatus.value = '请求出错';
  }
}

defineExpose({
  show
});
</script>

<style scoped>
.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: 500;
}
.action-bar {
  margin-top: 16px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  text-align: center;
}
.result-area {
  margin-top: 16px;
}
.status-bar {
  margin-bottom: 8px;
  color: #1890ff;
}
.reasoning-box {
  background: #f5f5f5;
  padding: 8px;
  border-radius: 4px;
  margin-bottom: 12px;
  max-height: 150px;
  overflow-y: auto;
  font-size: 12px;
  color: #666;
}
.box-title {
  font-weight: bold;
  margin-bottom: 4px;
}
.box-content {
  white-space: pre-wrap;
}
.mapping-preview {
  border: 1px solid #f0f0f0;
  padding: 8px;
  border-radius: 4px;
}
</style>