/*
 * 所有路由入口
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-06 20:52:26
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */
import { homeRouters } from './system/home';
import { loginRouters } from './system/login';
import { helpDocRouters } from './support/help-doc';
import { connectorRouters } from './business/connector';
import NotFound from '/@/views/system/40X/404.vue';
import NoPrivilege from '/@/views/system/40X/403.vue';

export const routerArray = [
    ...loginRouters,
     ...homeRouters, 
    ...helpDocRouters, 
    ...connectorRouters,
    { path: '/:pathMatch(.*)*', name: '404', component: NotFound },
    { path: '/403', name: '403', component: NoPrivilege }
];
