/**
 * 三级等保 api 封装
 *
 * @Author:    连接器-主任-卓大
 * @Date:      2024-07-31 21:02:37
 * @Copyright  连接器
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const level3ProtectApi = {
  /**
   * 查询 三级等保配置 @author 连接器-主任-卓大
   */
  getConfig: () => {
    return getRequest('/support/protect/level3protect/getConfig');
  },

  /**
   * 更新三级等保配置 @author 连接器-主任-卓大
   */
  updateConfig: (form) => {
    return postRequest('/support/protect/level3protect/updateConfig', form);
  },
};
