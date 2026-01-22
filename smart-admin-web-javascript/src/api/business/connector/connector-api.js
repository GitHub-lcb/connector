/*
 * 连接器路由 API
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const connectorApi = {
  // 分页查询路由
  queryRoutePage: (param) => {
    return postRequest('/connector/route/query', param);
  },
  
  // 查询路由详情
  getRouteDetail: (id) => {
    return getRequest(`/connector/route/detail/${id}`);
  },
  
  // 添加路由
  addRoute: (param) => {
    return postRequest('/connector/route/add', param);
  },
  
  // 更新路由
  updateRoute: (param) => {
    return postRequest('/connector/route/update', param);
  },
  
  // 删除路由
  deleteRoute: (id) => {
    return getRequest(`/connector/route/delete/${id}`);
  },
  
  // 分页查询日志
  queryRouteLogPage: (param) => {
    return postRequest('/connector/route/log/query', param);
  },
  
  // 查询配置历史
  queryHistory: (routeId) => {
    return getRequest(`/connector/route/history/${routeId}`);
  },
  
  // 获取统计数据
  getStats: () => {
    return getRequest('/connector/route/stats');
  }
};
