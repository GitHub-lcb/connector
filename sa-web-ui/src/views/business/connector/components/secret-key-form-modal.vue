<template>
  <a-modal
    :title="form.keyId ? '编辑密钥' : '添加密钥'"
    :visible="visible"
    :confirm-loading="confirmLoading"
    @ok="onSubmit"
    @cancel="onClose"
  >
    <a-form :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" ref="formRef">
      <a-form-item label="标题" name="title" :rules="[{ required: true, message: '请输入标题' }]">
        <a-input v-model:value="form.title" placeholder="请输入标题" />
      </a-form-item>
      
      <a-form-item label="状态" name="status" :rules="[{ required: true, message: '请选择状态' }]">
        <a-radio-group v-model:value="form.status">
          <a-radio :value="1">启用</a-radio>
          <a-radio :value="0">禁用</a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="form.remark" placeholder="备注" :rows="3" />
      </a-form-item>

      <a-form-item v-if="form.keyId" label="重置密钥" name="resetSecret">
        <a-checkbox v-model:checked="form.resetSecret">重新生成 Secret Key</a-checkbox>
        <div style="color: #999; font-size: 12px;">勾选后将生成新的 Secret Key，旧的将失效</div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { connectorSecretKeyApi } from '/@/api/business/connector/connector-secret-key-api';

const visible = ref(false);
const confirmLoading = ref(false);
const formRef = ref();

const form = reactive({
  keyId: null,
  title: '',
  status: 1,
  remark: '',
  resetSecret: false
});

const emit = defineEmits(['reloadList']);

function show(record) {
  visible.value = true;
  form.resetSecret = false;
  if (record) {
    form.keyId = record.keyId;
    form.title = record.title;
    form.status = record.status;
    form.remark = record.remark;
  } else {
    form.keyId = null;
    form.title = '';
    form.status = 1;
    form.remark = '';
  }
}

function onClose() {
  visible.value = false;
}

async function onSubmit() {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    
    if (form.keyId) {
      await connectorSecretKeyApi.update(form);
    } else {
      await connectorSecretKeyApi.add(form);
    }
    
    message.success(form.keyId ? '更新成功' : '添加成功');
    visible.value = false;
    emit('reloadList');
  } catch (error) {
    // validation failed or api error
  } finally {
    confirmLoading.value = false;
  }
}

defineExpose({
  show
});
</script>
