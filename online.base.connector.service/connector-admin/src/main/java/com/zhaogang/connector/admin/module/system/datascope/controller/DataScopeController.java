package com.zhaogang.connector.admin.module.system.datascope.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.zhaogang.connector.admin.constant.AdminSwaggerTagConst;
import com.zhaogang.connector.admin.module.system.datascope.domain.DataScopeAndViewTypeVO;
import com.zhaogang.connector.admin.module.system.datascope.service.DataScopeService;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询支持的数据范围类型
 *
 * @Author 连接器: 罗伊
 * @Date 2022-03-18 20:59:17
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_DATA_SCOPE)
public class DataScopeController {

    @Resource
    private DataScopeService dataScopeService;

    @Operation(summary = "获取当前系统所配置的所有数据范围 @author 罗伊")
    @GetMapping("/dataScope/list")
    public ResponseDTO<List<DataScopeAndViewTypeVO>> dataScopeList() {
        return dataScopeService.dataScopeList();
    }


}
