package com.zhaogang.connector.admin.module.business.connector.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyAddForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyQueryForm;
import com.zhaogang.connector.admin.module.business.connector.domain.form.ConnectorSecretKeyUpdateForm;
import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorSecretKeyVO;
import com.zhaogang.connector.admin.module.business.connector.service.ConnectorSecretKeyService;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.zhaogang.connector.admin.module.business.connector.domain.vo.ConnectorSecretKeyExcelVO;
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
@Tag(name = "Connector - 密钥管理")
@RequestMapping("/connector/secret-key")
public class ConnectorSecretKeyController {

    @Resource
    private ConnectorSecretKeyService connectorSecretKeyService;

    @Operation(summary = "分页查询密钥")
    @PostMapping("/query")
    public ResponseDTO<PageResult<ConnectorSecretKeyVO>> query(@RequestBody @Valid ConnectorSecretKeyQueryForm queryForm) {
        return connectorSecretKeyService.queryPage(queryForm);
    }

    @Operation(summary = "添加密钥")
    @PostMapping("/add")
    public ResponseDTO<String> add(@RequestBody @Valid ConnectorSecretKeyAddForm addForm) {
        return connectorSecretKeyService.add(addForm);
    }

    @Operation(summary = "更新密钥")
    @PostMapping("/update")
    public ResponseDTO<String> update(@RequestBody @Valid ConnectorSecretKeyUpdateForm updateForm) {
        return connectorSecretKeyService.update(updateForm);
    }

    @Operation(summary = "删除密钥")
    @GetMapping("/delete/{keyId}")
    public ResponseDTO<String> delete(@PathVariable Long keyId) {
        return connectorSecretKeyService.delete(keyId);
    }

    @Operation(summary = "批量删除密钥")
    @PostMapping("/batchDelete")
    public ResponseDTO<String> batchDelete(@RequestBody @Valid ValidateList<Long> idList) {
        return connectorSecretKeyService.batchDelete(idList);
    }

    @Operation(summary = "导入密钥")
    @PostMapping("/import")
    public ResponseDTO<String> importKey(@RequestParam MultipartFile file) {
        return connectorSecretKeyService.importKey(file);
    }

    @Operation(summary = "导出密钥")
    @GetMapping("/export")
    public void exportKey(HttpServletResponse response) throws IOException {
        List<ConnectorSecretKeyExcelVO> list = connectorSecretKeyService.getAllKeys();
        SmartExcelUtil.exportExcel(response, "密钥列表.xlsx", "密钥", ConnectorSecretKeyExcelVO.class, list);
    }
}
