/*
 *  权限插件
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-06 20:50:46
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import { useUserStore } from '/@/store/modules/system/user';
import _ from 'lodash';

const privilege = (value) => {
  // 超级管理员
  if (useUserStore().administratorFlag) {
    return true;
  }
  // 获取功能点权限
  let userPointsList = useUserStore().getPointList;
  if (!userPointsList) {
    return false;
  }
  return _.some(userPointsList, ['webPerms', value]);
};

export default {
  install: (app) => {
    app.config.globalProperties.$privilege = privilege;
  },
};
