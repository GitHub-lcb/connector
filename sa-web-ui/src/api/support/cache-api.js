/*
 * 缓存
 *
 * @Author:    罗伊
 * @Date:      2022-09-03 21:51:34
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import {getRequest} from '/@/lib/axios';

export const cacheApi = {
  // 获取某个缓存的所有key @author 罗伊
  getKeys: (cacheName) => {
    return getRequest(`/support/cache/keys/${cacheName}`);
  },
  // 移除某个缓存 @author 罗伊
  remove: (cacheName) => {
    return getRequest(`/support/cache/remove/${cacheName}`);
  },
  // 获取所有缓存 @author 罗伊
  getAllCacheNames: () => {
    return getRequest('/support/cache/names');
  },
};
