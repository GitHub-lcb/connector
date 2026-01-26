import SmartLayout from '/@/layout/index.vue';

export const connectorRouters = [
  {
    path: '/connector',
    name: 'connector',
    component: SmartLayout,
    meta: {
      title: '连接器管理',
      icon: 'LinkOutlined',
    },
    children: [
      {
        path: '/connector/route-list',
        name: 'ConnectorRouteList',
        component: () => import('/@/views/business/connector/route-list.vue'),
        meta: {
          title: '路由配置',
          icon: 'NodeIndexOutlined',
        },
      },
      {
        path: '/connector/route-log-list',
        name: 'ConnectorRouteLogList',
        component: () => import('/@/views/business/connector/route-log-list.vue'),
        meta: {
          title: '请求日志',
          icon: 'FileTextOutlined',
        },
      },
      {
        path: '/connector/route-test',
        name: 'ConnectorRouteTest',
        component: () => import('/@/views/business/connector/route-test.vue'),
        meta: {
          title: '路由测试',
          icon: 'ExperimentOutlined',
        },
      },
      {
        path: '/connector/secret-key-list',
        name: 'ConnectorSecretKeyList',
        component: () => import('/@/views/business/connector/secret-key-list.vue'),
        meta: {
          title: '密钥管理',
          icon: 'KeyOutlined',
        },
      },
    ],
  },
];
