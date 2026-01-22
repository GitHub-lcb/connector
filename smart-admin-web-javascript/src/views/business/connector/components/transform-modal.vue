<template>
  <a-modal
    :title="title"
    :open="visible"
    :width="600"
    @ok="onOk"
    @cancel="onCancel"
    destroyOnClose
  >
    <div style="margin-bottom: 16px">
      <a-alert message="转换规则将按照顺序依次执行，上一个规则的输出作为下一个规则的输入。" type="info" show-icon />
    </div>

    <div class="transform-list">
      <div v-for="(item, index) in transformations" :key="index" class="transform-item">
        <div class="item-header">
          <span class="step-badge">{{ index + 1 }}</span>
          <a-select 
            v-model:value="item.type" 
            style="width: 180px" 
            placeholder="选择转换类型"
            @change="handleTypeChange(item)"
          >
            <a-select-opt-group label="字符串操作">
              <a-select-option value="uppercase">转大写 (Uppercase)</a-select-option>
              <a-select-option value="lowercase">转小写 (Lowercase)</a-select-option>
              <a-select-option value="trim">去空格 (Trim)</a-select-option>
              <a-select-option value="substring">截取 (Substring)</a-select-option>
              <a-select-option value="concat">拼接 (Concat)</a-select-option>
              <a-select-option value="replace">替换 (Replace)</a-select-option>
              <a-select-option value="split">分割 (Split)</a-select-option>
            </a-select-opt-group>
            <a-select-opt-group label="编码/解码">
              <a-select-option value="base64_encode">Base64 编码</a-select-option>
              <a-select-option value="base64_decode">Base64 解码</a-select-option>
            </a-select-opt-group>
            <a-select-opt-group label="类型转换">
              <a-select-option value="number">转数字</a-select-option>
              <a-select-option value="string">转字符串</a-select-option>
              <a-select-option value="boolean">转布尔值</a-select-option>
              <a-select-option value="json_parse">JSON 解析</a-select-option>
              <a-select-option value="json_stringify">JSON 序列化</a-select-option>
            </a-select-opt-group>
          </a-select>
          
          <a-space class="action-btns">
            <a-button type="text" size="small" :disabled="index === 0" @click="moveUp(index)">
              <ArrowUpOutlined />
            </a-button>
            <a-button type="text" size="small" :disabled="index === transformations.length - 1" @click="moveDown(index)">
              <ArrowDownOutlined />
            </a-button>
            <a-button type="text" danger size="small" @click="remove(index)">
              <DeleteOutlined />
            </a-button>
          </a-space>
        </div>

        <div class="item-params" v-if="hasParams(item.type)">
          <!-- Substring Params -->
          <template v-if="item.type === 'substring'">
            <a-input-number v-model:value="item.params[0]" placeholder="开始索引" style="width: 120px" />
            <span class="separator">-</span>
            <a-input-number v-model:value="item.params[1]" placeholder="结束索引" style="width: 120px" />
          </template>

          <!-- Concat Params -->
          <template v-if="item.type === 'concat'">
            <a-input v-model:value="item.params[0]" placeholder="追加的字符串" />
          </template>

          <!-- Replace Params -->
          <template v-if="item.type === 'replace'">
            <a-input v-model:value="item.params[0]" placeholder="查找内容 (支持正则)" style="width: 45%" />
            <span class="separator">→</span>
            <a-input v-model:value="item.params[1]" placeholder="替换为" style="width: 45%" />
          </template>

          <!-- Split Params -->
          <template v-if="item.type === 'split'">
            <a-input v-model:value="item.params[0]" placeholder="分隔符" />
          </template>
          
          <!-- Join Params -->
          <template v-if="item.type === 'join'">
            <a-input v-model:value="item.params[0]" placeholder="连接符" />
          </template>
        </div>
      </div>

      <div class="add-btn-wrapper">
        <a-button type="dashed" block @click="add">
          <PlusOutlined /> 添加转换规则
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { 
  ArrowUpOutlined, 
  ArrowDownOutlined, 
  DeleteOutlined, 
  PlusOutlined 
} from '@ant-design/icons-vue';
import _ from 'lodash';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'ok']);

const visible = ref(false);
const title = ref('编辑转换规则');
const transformations = ref([]);
const currentMappingIndex = ref(-1);

// Initialize params based on type
const handleTypeChange = (item) => {
  item.params = [];
  if (item.type === 'substring') {
    item.params = [0, 1];
  } else if (item.type === 'replace') {
    item.params = ['', ''];
  } else if (['concat', 'split', 'join'].includes(item.type)) {
    item.params = [''];
  }
};

const hasParams = (type) => {
  return ['substring', 'concat', 'replace', 'split', 'join'].includes(type);
};

const openModal = (mappingIndex, rules) => {
  currentMappingIndex.value = mappingIndex;
  // Deep copy rules to avoid direct mutation
  transformations.value = _.cloneDeep(rules || []);
  visible.value = true;
};

const onOk = () => {
  emit('ok', currentMappingIndex.value, _.cloneDeep(transformations.value));
  visible.value = false;
};

const onCancel = () => {
  visible.value = false;
};

const add = () => {
  transformations.value.push({
    type: undefined,
    params: []
  });
};

const remove = (index) => {
  transformations.value.splice(index, 1);
};

const moveUp = (index) => {
  if (index > 0) {
    const temp = transformations.value[index];
    transformations.value[index] = transformations.value[index - 1];
    transformations.value[index - 1] = temp;
  }
};

const moveDown = (index) => {
  if (index < transformations.value.length - 1) {
    const temp = transformations.value[index];
    transformations.value[index] = transformations.value[index + 1];
    transformations.value[index + 1] = temp;
  }
};

defineExpose({
  openModal
});
</script>

<style scoped>
.transform-list {
  max-height: 400px;
  overflow-y: auto;
}

.transform-item {
  background-color: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
}

.item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.step-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #1890ff;
  color: #fff;
  border-radius: 50%;
  margin-right: 12px;
  font-size: 12px;
}

.item-params {
  margin-top: 12px;
  padding-left: 36px;
  display: flex;
  align-items: center;
}

.separator {
  margin: 0 8px;
  color: #999;
}

.action-btns {
  margin-left: auto;
}

.add-btn-wrapper {
  margin-top: 16px;
}
</style>
