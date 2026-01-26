package com.zhaogang.connector.admin.module.system.tenant.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.zhaogang.connector.base.common.context.TenantContext;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantAddForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantQueryForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.form.TenantUpdateForm;
import com.zhaogang.connector.admin.module.system.tenant.domain.vo.TenantVO;
import com.zhaogang.connector.admin.module.system.tenant.service.TenantService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.zhaogang.connector.admin.module.system.tenant.domain.vo.TenantExcelVO;
import com.zhaogang.connector.base.common.domain.ValidateList;
import com.zhaogang.connector.base.common.util.SmartExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author chenbo.li
 */
@RestController
@Tag(name = "租户管理")
public class TenantController {

    @Resource
    private TenantService tenantService;

    private void checkDefaultTenant() {
        if (TenantContext.getTenantId() != 1L) {
            throw new RuntimeException("非默认租户管理员，无法操作");
        }
    }

    @Operation(summary = "分页查询租户")
    @PostMapping("/tenant/query")
    @SaCheckPermission("system:tenant:query")
    public ResponseDTO<PageResult<TenantVO>> queryPage(@RequestBody @Valid TenantQueryForm queryForm) {
        checkDefaultTenant();
        return tenantService.queryPage(queryForm);
    }

    @Operation(summary = "添加租户")
    @PostMapping("/tenant/add")
    @SaCheckPermission("system:tenant:add")
    public ResponseDTO<String> add(@RequestBody @Valid TenantAddForm addForm) {
        checkDefaultTenant();
        return tenantService.add(addForm);
    }

    @Operation(summary = "更新租户")
    @PostMapping("/tenant/update")
    @SaCheckPermission("system:tenant:update")
    public ResponseDTO<String> update(@RequestBody @Valid TenantUpdateForm updateForm) {
        checkDefaultTenant();
        return tenantService.update(updateForm);
    }
    
    @Operation(summary = "删除租户")
    @GetMapping("/tenant/delete/{tenantId}")
    @SaCheckPermission("system:tenant:delete")
    public ResponseDTO<String> delete(@PathVariable Long tenantId) {
        checkDefaultTenant();
        return tenantService.delete(tenantId);
    }

    @Operation(summary = "批量删除租户")
    @PostMapping("/tenant/batchDelete")
    @SaCheckPermission("system:tenant:batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody @Valid ValidateList<Long> idList) {
        checkDefaultTenant();
        return tenantService.batchDelete(idList);
    }

    @Operation(summary = "导入租户")
    @PostMapping("/tenant/import")
    @SaCheckPermission("system:tenant:import")
    public ResponseDTO<String> importTenant(@RequestParam MultipartFile file) {
        checkDefaultTenant();
        return tenantService.importTenant(file);
    }

    @Operation(summary = "导出租户")
    @GetMapping("/tenant/export")
    @SaCheckPermission("system:tenant:export")
    public void exportTenant(HttpServletResponse response) throws IOException {
        checkDefaultTenant();
        List<TenantExcelVO> list = tenantService.getAllTenant();
        SmartExcelUtil.exportExcel(response, "租户列表.xlsx", "租户", TenantExcelVO.class, list);
    }
}
