package com.zhaogang.connector.admin.module.system.department.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 部门
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-01-12 20:37:48
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class DepartmentTreeVO extends DepartmentVO {

    @Schema(description = "同级上一个元素id")
    private Long preId;

    @Schema(description = "同级下一个元素id")
    private Long nextId;

    @Schema(description = "子部门")
    private List<DepartmentTreeVO> children;

    @Schema(description = "自己和所有递归子部门的id集合")
    private List<Long> selfAndAllChildrenIdList;

}
