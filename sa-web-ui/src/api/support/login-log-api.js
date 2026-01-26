/*
 * 登录日志
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-03 21:56:31
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const loginLogApi = {
  // 分页查询 @author 卓大
  queryList: (param) => {
    return postRequest('/support/loginLog/page/query', param);
  },
  // 分页查询当前登录人信息 @author 善逸
  queryListLogin: (param) => {
    return postRequest('/support/loginLog/page/query/login', param);
  },
};
