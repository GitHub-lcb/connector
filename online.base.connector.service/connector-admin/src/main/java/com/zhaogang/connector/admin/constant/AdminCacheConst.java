package com.zhaogang.connector.admin.constant;

import com.zhaogang.connector.base.constant.CacheKeyConst;

/**
 * 缓存 key
 *
 * @Author 连接器-主任:卓大
 * @Date 2022-01-07 18:59:22
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
public class AdminCacheConst extends CacheKeyConst {

    public static class Department {

        /**
         * 部门列表
         */
        public static final String DEPARTMENT_LIST_CACHE = "department_list_cache";

        /**
         * 部门树
         */
        public static final String DEPARTMENT_TREE_CACHE = "department_tree_cache";

        /**
         * 某个部门以及下级的id列表
         */
        public static final String DEPARTMENT_SELF_CHILDREN_CACHE = "department_self_children_cache";

        /**
         * 部门路径 缓存
         */
        public static final String DEPARTMENT_PATH_CACHE = "department_path_cache";

    }

    /**
     * 分类相关缓存
     */
    public static class Category {

        public static final String CATEGORY_ENTITY = "category_cache";

        public static final String CATEGORY_SUB = "category_sub_cache";

        public static final String CATEGORY_TREE = "category_tree_cache";
    }

    /**
     * 登录相关
     */
    public static class Login {

        /**
         * 请求用户信息
         */
        public static final String REQUEST_EMPLOYEE = "login_request_employee";

        /**
         * 请求用户信息权限
         */
        public static final String USER_PERMISSION = "login_user_permission";
    }

}
