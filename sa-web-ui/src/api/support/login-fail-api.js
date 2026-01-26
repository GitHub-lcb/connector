/**
 * 登录锁定 api 封装
 *
 * @Author:    连接器-主任-卓大
 * @Date:      2023-10-17 18:02:37
 * @Copyright  连接器
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const loginFailApi = {

  /**
   * 分页查询  @author  连接器-主任-卓大
   */
  queryPage : (param) => {
    return postRequest('/support/protect/loginFail/queryPage', param);
  },

  /**
   * 批量删除  @author  连接器-主任-卓大
   */
  batchDelete: (idList) => {
      return postRequest('/support/protect/loginFail/batchDelete', idList);
  },

};
