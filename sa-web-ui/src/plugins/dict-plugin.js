/*
 * 字典插件
 * 此插件为 连接器 自创的插件
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2024-09-06 20:51:03
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import { useDictStore } from '/@/store/modules/system/dict.js';

export default {
  install: (app) => {

    const dictPlugin = {};

    /**
     * 根据枚举值获取描述
     * @param {*} dictCode   字典编码
     * @param {*} value     值
     * @returns
     */
    dictPlugin.getDataLabels = function (dictCode, value) {
      return useDictStore().getDataLabels(dictCode, value);
    };

    app.config.globalProperties.$dictPlugin = dictPlugin;
    app.provide('dictPlugin', dictPlugin);
  },
};
