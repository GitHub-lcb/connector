import { postRequest, getRequest, getDownload } from '/@/lib/axios';

export const connectorSecretKeyApi = {
  // 分页查询密钥
  queryPage: (param) => {
    return postRequest('/connector/secret-key/query', param);
  },
  // 添加密钥
  add: (param) => {
    return postRequest('/connector/secret-key/add', param);
  },
  // 更新密钥
  update: (param) => {
    return postRequest('/connector/secret-key/update', param);
  },
  // 删除密钥
  delete: (keyId) => {
    return getRequest(`/connector/secret-key/delete/${keyId}`);
  },
  // 批量删除
  batchDelete: (idList) => {
    return postRequest('/connector/secret-key/batchDelete', idList);
  },
  // 导入
  importKey: (file) => {
    return postRequest('/connector/secret-key/import', file);
  },
  // 导出
  exportKey: () => {
    return getDownload('/connector/secret-key/export');
  }
};
