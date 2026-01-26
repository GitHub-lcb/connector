<template>
  <a-drawer
    :title="title"
    :width="800"
    :open="visible"
    :body-style="{ paddingBottom: '80px' }"
    @close="onClose"
  >
    <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      layout="vertical"
    >
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="路由名称" name="name">
                <a-input v-model:value="formState.name" placeholder="请输入路由名称" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="渠道" name="channel">
                <a-input v-model:value="formState.channel" placeholder="例如：淘宝、京东" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="16">
            <a-col :span="8">
              <a-form-item label="HTTP 方法" name="method">
                <a-select v-model:value="formState.method">
                  <a-select-option value="GET">GET</a-select-option>
                  <a-select-option value="POST">POST</a-select-option>
                  <a-select-option value="PUT">PUT</a-select-option>
                  <a-select-option value="DELETE">DELETE</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="状态" name="status">
                <a-select v-model:value="formState.status">
                  <a-select-option value="active">启用</a-select-option>
                  <a-select-option value="inactive">停用</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="转发请求" name="forwardFlag">
                <a-switch v-model:checked="formState.forwardFlag" checked-children="开启" un-checked-children="关闭" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="16">
            <a-col :span="24">
              <a-form-item label="源路径 (连接器)" name="sourcePath">
                <a-input v-model:value="formState.sourcePath" placeholder="/api/v1/orders" />
                <div class="tips">连接器监听的请求路径</div>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="16">
            <a-col :span="24" v-if="formState.forwardFlag">
              <a-form-item label="目标地址 (云仓)" name="targetUrl">
                <a-input v-model:value="formState.targetUrl" placeholder="https://cloud-wms.com/api/orders" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-tab-pane>

        <a-tab-pane key="script" tab="脚本处理">
          <a-alert message="脚本将在字段映射之后执行，用于最终的数据处理。" type="info" show-icon style="margin-bottom: 16px" />
          
          <a-form-item label="脚本类型" name="scriptType">
            <a-select v-model:value="formState.scriptType" placeholder="请选择脚本类型" allowClear>
              <a-select-option value="groovy">Groovy (Java)</a-select-option>
              <a-select-option value="python">Python</a-select-option>
            </a-select>
          </a-form-item>
          
          <div v-if="formState.scriptType">
             <a-form-item label="脚本内容" name="scriptContent">
               <a-textarea 
                 v-model:value="formState.scriptContent" 
                 :rows="15" 
                 placeholder="请输入脚本内容..." 
                 style="font-family: monospace;"
               />
             </a-form-item>
             
             <div class="tips" v-if="formState.scriptType === 'groovy'">
               <p>说明：</p>
               <ul>
                 <li>脚本环境已注入变量 <code>data</code> (当前处理结果)。</li>
                 <li>脚本必须返回最终结果对象。</li>
                 <li>支持 Jolt 转换。</li>
               </ul>
               <p>示例 (Jolt):</p>
               <pre style="background: #f5f5f5; padding: 8px; border-radius: 4px;">
import com.bazaarvoice.jolt.Chainr
import com.bazaarvoice.jolt.JsonUtils

def spec = [
  [
    "operation": "shift",
    "spec": [
      "oldKey": "newKey"
    ]
  ]
]

Chainr chainr = Chainr.fromSpec(spec)
return chainr.transform(data)
               </pre>
             </div>
          </div>
        </a-tab-pane>

        <a-tab-pane key="mapping" tab="字段映射">
          <div style="margin-bottom: 16px;">
            <a-space>
              <a-button type="primary" size="small" @click="addMapping">
                <template #icon><PlusOutlined /></template> 添加映射
              </a-button>
              <a-button type="dashed" size="small" @click="openSmartMapping" style="background-color: #f6ffed; border-color: #b7eb8f; color: #52c41a">
                <template #icon><RobotOutlined /></template> AI 智能映射
              </a-button>
              <a-button size="small" @click="clearMappings" danger>
                清空映射
              </a-button>
            </a-space>
          </div>
          
          <div v-for="(mapping, index) in formState.mappingConfig.mappings" :key="index" class="mapping-item">
            <a-space align="start">
              <a-input v-model:value="mapping.source" placeholder="源字段" style="width: 150px" />
              <ArrowRightOutlined />
              <a-input v-model:value="mapping.target" placeholder="目标字段" style="width: 150px" />
              
              <a-select v-model:value="mapping.targetType" style="width: 100px" placeholder="类型">
                <a-select-option value="string">字符串</a-select-option>
                <a-select-option value="integer">整数</a-select-option>
                <a-select-option value="decimal">小数</a-select-option>
                <a-select-option value="boolean">布尔</a-select-option>
                <a-select-option value="array">数组</a-select-option>
                <a-select-option value="object">对象</a-select-option>
              </a-select>
              
              <a-button type="text" danger @click="removeMapping(index)">
                <DeleteOutlined />
              </a-button>
            </a-space>
            
            <div style="margin-top: 8px;">
               <a-input v-model:value="mapping.defaultValue" placeholder="默认值" size="small" style="width: 150px; margin-right: 8px" />
               <a-button size="small" type="link" @click="editTransformations(index)">转换规则 ({{ mapping.transformations?.length || 0 }})</a-button>
            </div>
            <a-divider style="margin: 12px 0" />
          </div>
          
          <div v-if="formState.mappingConfig.mappings.length === 0" class="empty-text">
            暂无映射配置
          </div>
        </a-tab-pane>

        <a-tab-pane key="security" tab="安全配置">
          <a-form-item label="加密方式">
            <a-select v-model:value="formState.mappingConfig.security.type">
              <a-select-option value="NONE">不加密</a-select-option>
              <a-select-option value="RSA">RSA 非对称加密</a-select-option>
            </a-select>
          </a-form-item>
          
          <template v-if="formState.mappingConfig.security.type === 'RSA'">
            <a-form-item label="公钥 (Public Key)">
              <a-textarea 
                v-model:value="formState.mappingConfig.security.publicKey" 
                :rows="4" 
                placeholder="-----BEGIN PUBLIC KEY-----..." 
              />
            </a-form-item>
            <a-form-item label="加密后字段名">
              <a-input v-model:value="formState.mappingConfig.security.encryptedField" placeholder="默认为 data" />
            </a-form-item>
          </template>
        </a-tab-pane>

        <a-tab-pane key="headers" tab="请求头">
          <div style="margin-bottom: 16px;">
            <a-button type="dashed" size="small" @click="addHeader">
              <PlusOutlined /> 添加 Header
            </a-button>
          </div>
          
          <div v-for="(header, index) in formState.mappingConfig.headers" :key="index" style="margin-bottom: 8px">
            <a-space>
              <a-input v-model:value="header.key" placeholder="Key" style="width: 200px" />
              <a-input v-model:value="header.value" placeholder="Value" style="width: 200px" />
              <a-input v-model:value="header.description" placeholder="描述" style="width: 150px" />
              <a-button type="text" danger @click="removeHeader(index)">
                <DeleteOutlined />
              </a-button>
            </a-space>
          </div>
        </a-tab-pane>
        
        <a-tab-pane key="aggregation" tab="聚合配置">
           <a-form-item>
             <a-checkbox v-model:checked="formState.mappingConfig.aggregation.enabled">启用聚合</a-checkbox>
           </a-form-item>
           
           <template v-if="formState.mappingConfig.aggregation.enabled">
             <a-form-item label="数组字段">
               <a-input v-model:value="formState.mappingConfig.aggregation.arrayField" placeholder="例如: detail" />
             </a-form-item>
             <a-form-item label="分组字段">
               <a-select v-model:value="formState.mappingConfig.aggregation.groupByFields" mode="tags" placeholder="输入后回车" />
             </a-form-item>
             <a-form-item label="求和字段">
               <a-select v-model:value="formState.mappingConfig.aggregation.sumFields" mode="tags" placeholder="输入后回车" />
             </a-form-item>
             <a-form-item label="计数保存字段">
               <a-input v-model:value="formState.mappingConfig.aggregation.countField" placeholder="例如: count" />
             </a-form-item>
           </template>
        </a-tab-pane>
      </a-tabs>
    </a-form>

    <template #footer>
      <a-space style="float: right">
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit" :loading="saving">保存</a-button>
      </a-space>
    </template>
    
    <TransformModal ref="transformModalRef" @ok="handleTransformOk" />
    <SmartMappingModal ref="smartMappingModalRef" @ok="handleSmartMappingOk" />
  </a-drawer>
</template>

<script setup>
import { reactive, ref, nextTick, computed } from 'vue';
import { message } from 'ant-design-vue';
import { RobotOutlined } from '@ant-design/icons-vue';
import { connectorApi } from '/@/api/business/connector/connector-api';
import _ from 'lodash';
import TransformModal from './transform-modal.vue';
import SmartMappingModal from './smart-mapping-modal.vue';

const emit = defineEmits(['reloadList']);

const visible = ref(false);
const title = ref('新建路由');
const saving = ref(false);
const activeTab = ref('basic');
const formRef = ref();
const transformModalRef = ref();
const smartMappingModalRef = ref();

const initialState = {
  id: undefined,
  name: '',
  channel: '',
  sourcePath: '',
  targetUrl: '',
  method: 'POST',
  forwardFlag: true,
  status: 'active',
  scriptType: undefined,
  scriptContent: '',
  mappingConfig: {
    mappings: [],
    security: {
      type: 'NONE',
      publicKey: '',
      encryptedField: ''
    },
    headers: [],
    aggregation: {
      enabled: false,
      arrayField: '',
      groupByFields: [],
      sumFields: [],
      keepFields: [],
      avgFields: [],
      countField: ''
    }
  }
};

const formState = reactive(_.cloneDeep(initialState));

const rules = computed(() => ({
  name: [{ required: true, message: '请输入路由名称' }],
  sourcePath: [{ required: true, message: '请输入源路径' }],
  targetUrl: [{ required: formState.forwardFlag, message: '请输入目标地址' }],
  method: [{ required: true, message: '请选择方法' }]
}));

async function showDrawer(id, isCopy = false) {
  visible.value = true;
  activeTab.value = 'basic';
  Object.assign(formState, _.cloneDeep(initialState));
  
  if (id) {
    try {
      const res = await connectorApi.getRouteDetail(id);
      const data = res.data;
      
      // Merge data
      formState.name = data.name;
      formState.channel = data.channel;
      formState.sourcePath = data.sourcePath;
      formState.targetUrl = data.targetUrl;
      formState.method = data.method;
      formState.forwardFlag = data.forwardFlag !== false;
      formState.status = data.status;
      formState.scriptType = data.scriptType;
      formState.scriptContent = data.scriptContent;
      
      // Merge config safely
      if (data.mappingConfig) {
        formState.mappingConfig.mappings = data.mappingConfig.mappings || [];
        if (data.mappingConfig.security) {
          Object.assign(formState.mappingConfig.security, data.mappingConfig.security);
        }
        formState.mappingConfig.headers = data.mappingConfig.headers || [];
        if (data.mappingConfig.aggregation) {
          Object.assign(formState.mappingConfig.aggregation, data.mappingConfig.aggregation);
        }
      }
      
      if (isCopy) {
        title.value = '复制新建路由';
        formState.id = undefined;
        formState.name = `${formState.name} (复制)`;
        formState.sourcePath = `${formState.sourcePath}_copy`;
      } else {
        title.value = '编辑路由';
        formState.id = data.id;
      }
    } catch (e) {
      console.error(e);
      message.error('加载详情失败');
    }
  } else {
    title.value = '新建路由';
  }
}

function onClose() {
  visible.value = false;
}

async function onSubmit() {
  try {
    await formRef.value.validate();
    saving.value = true;
    
    const params = _.cloneDeep(formState);
    if (params.id) {
      await connectorApi.updateRoute(params);
    } else {
      await connectorApi.addRoute(params);
    }
    
    message.success('保存成功');
    visible.value = false;
    emit('reloadList');
  } catch (e) {
    console.error(e);
  } finally {
    saving.value = false;
  }
}

// Mapping Logic
function addMapping() {
  formState.mappingConfig.mappings.push({
    source: '',
    target: '',
    targetType: 'string',
    defaultValue: null,
    transformations: []
  });
}

function removeMapping(index) {
  formState.mappingConfig.mappings.splice(index, 1);
}

function clearMappings() {
  formState.mappingConfig.mappings = [];
}

function editTransformations(index) {
  const mapping = formState.mappingConfig.mappings[index];
  if (transformModalRef.value) {
    transformModalRef.value.openModal(index, mapping.transformations);
  }
}

function handleTransformOk(index, rules) {
  if (index >= 0 && index < formState.mappingConfig.mappings.length) {
    formState.mappingConfig.mappings[index].transformations = rules;
  }
}

// AI Mapping Logic
function openSmartMapping() {
  if (smartMappingModalRef.value) {
    smartMappingModalRef.value.show();
  }
}

function handleSmartMappingOk(newMappings) {
  if (!newMappings || newMappings.length === 0) return;
  
  // 简单的去重添加策略
  // 如果已存在相同的 source，则更新 target；否则追加
  newMappings.forEach(newItem => {
    const existingIndex = formState.mappingConfig.mappings.findIndex(
      m => m.source === newItem.source
    );
    
    if (existingIndex !== -1) {
      // 更新现有映射的目标字段和类型
      formState.mappingConfig.mappings[existingIndex].target = newItem.target;
      formState.mappingConfig.mappings[existingIndex].targetType = newItem.targetType || 'string';
      if (newItem.defaultValue !== undefined) {
        formState.mappingConfig.mappings[existingIndex].defaultValue = newItem.defaultValue;
      }
    } else {
      // 追加新映射
      formState.mappingConfig.mappings.push({
        source: newItem.source,
        target: newItem.target,
        targetType: newItem.targetType || 'string',
        defaultValue: newItem.defaultValue || null,
        transformations: []
      });
    }
  });
  
  message.success(`已应用 ${newMappings.length} 条智能映射规则`);
}

// Header Logic
function addHeader() {
  formState.mappingConfig.headers.push({ key: '', value: '', description: '' });
}

function removeHeader(index) {
  formState.mappingConfig.headers.splice(index, 1);
}

defineExpose({
  showDrawer
});
</script>

<style scoped>
.tips {
  color: #999;
  font-size: 12px;
}
.mapping-item {
  padding: 8px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 8px;
}
.empty-text {
  text-align: center;
  color: #999;
  padding: 20px;
}
</style>
