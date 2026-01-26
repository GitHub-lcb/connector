/*
 * 心跳
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-03 21:55:47
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import { postRequest } from '/@/lib/axios';

export const heartBeatApi = {
  // 分页查询 @author 卓大
  queryList: (param) => {
    return postRequest('/support/heartBeat/query', param);
  },
};
