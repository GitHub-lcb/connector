import { getRequest, postRequest, getDownload } from '/@/lib/axios';

export const tenantApi = {
  queryPage: (param) => {
    return postRequest('/tenant/query', param);
  },
  add: (param) => {
    return postRequest('/tenant/add', param);
  },
  update: (param) => {
    return postRequest('/tenant/update', param);
  },
  delete: (tenantId) => {
    return getRequest(`/tenant/delete/${tenantId}`);
  },
  batchDelete: (idList) => {
    return postRequest('/tenant/batchDelete', idList);
  },
  importTenant: (file) => {
    return postRequest('/tenant/import', file);
  },
  exportTenant: () => {
    return getDownload('/tenant/export');
  }
};
