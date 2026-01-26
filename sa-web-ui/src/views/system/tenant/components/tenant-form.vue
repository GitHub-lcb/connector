<template>
  <a-modal
    :title="form.tenantId ? '编辑租户' : '添加租户'"
    :visible="visible"
    :confirm-loading="confirmLoading"
    @ok="onSubmit"
    @cancel="onClose"
  >
    <a-form :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" ref="formRef" :rules="rules">
      <a-form-item label="租户名称" name="tenantName">
        <a-input v-model:value="form.tenantName" placeholder="请输入租户名称" />
      </a-form-item>
      <a-form-item label="联系人" name="contactPerson">
        <a-input v-model:value="form.contactPerson" placeholder="请输入联系人" />
      </a-form-item>
      <a-form-item label="联系电话" name="contactPhone">
        <a-input v-model:value="form.contactPhone" placeholder="请输入联系电话" />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-radio-group v-model:value="form.status">
          <a-radio :value="1">正常</a-radio>
          <a-radio :value="0">禁用</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="过期时间" name="expireTime">
        <a-date-picker show-time v-model:value="form.expireTime" valueFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import { message } from 'ant-design-vue';
  import { tenantApi } from '/@/api/system/tenant-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';

  const emit = defineEmits(['refresh']);

  const visible = ref(false);
  const confirmLoading = ref(false);
  const formRef = ref();

  const form = reactive({
    tenantId: null,
    tenantName: '',
    contactPerson: '',
    contactPhone: '',
    status: 1,
    expireTime: null,
  });

  const rules = {
    tenantName: [{ required: true, message: '请输入租户名称', trigger: 'blur' }],
  };

  function showModal(record) {
    visible.value = true;
    if (record) {
      Object.assign(form, record);
    } else {
      resetForm();
    }
    nextTick(() => {
        formRef.value.clearValidate();
    });
  }

  function resetForm() {
    form.tenantId = null;
    form.tenantName = '';
    form.contactPerson = '';
    form.contactPhone = '';
    form.status = 1;
    form.expireTime = null;
  }

  function onClose() {
    visible.value = false;
  }

  async function onSubmit() {
    try {
      await formRef.value.validate();
      confirmLoading.value = true;
      if (form.tenantId) {
        await tenantApi.update(form);
      } else {
        await tenantApi.add(form);
      }
      message.success('操作成功');
      visible.value = false;
      emit('refresh');
    } catch (e) {
      // error
    } finally {
      confirmLoading.value = false;
    }
  }

  defineExpose({
    showModal,
  });
</script>
