/*
 * 登录页面
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-06 20:51:50
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */

export const loginRouters = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('/@/views/system/login3/login.vue'),
    meta: {
      title: '登录',
      hideInMenu: true,
    },
  },
];
