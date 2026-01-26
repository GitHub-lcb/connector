package com.zhaogang.connector.admin.module.system.role.domain.vo;

import lombok.Data;

/**
 * 角色的员工
 *
 * @Author 连接器: 罗伊
 * @Date 2022-04-08 21:53:04
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class RoleEmployeeVO {

    private Long roleId;

    private Long employeeId;

    private String roleName;
}
