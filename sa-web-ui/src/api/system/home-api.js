/*
 * 首页api
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-03 21:59:39
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import { getRequest } from '/@/lib/axios';

export const homeApi = {
  /**
   * @description: 首页-金额统计（业绩、收款、订单数等） @author 卓大
   */
  homeAmountStatistics: () => {
    return getRequest('/home/amount/statistics');
  },
  /**
   * @description: 首页-待办信息 @author 卓大
   */
  homeWaitHandle: () => {
    return getRequest('home/wait/handle');
  },
};
