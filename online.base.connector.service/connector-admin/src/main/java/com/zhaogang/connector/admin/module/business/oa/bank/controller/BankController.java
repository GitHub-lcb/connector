package com.zhaogang.connector.admin.module.business.oa.bank.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.zhaogang.connector.admin.constant.AdminSwaggerTagConst;
import com.zhaogang.connector.admin.module.business.oa.bank.service.BankService;
import com.zhaogang.connector.admin.module.business.oa.bank.domain.BankCreateForm;
import com.zhaogang.connector.admin.module.business.oa.bank.domain.BankQueryForm;
import com.zhaogang.connector.admin.module.business.oa.bank.domain.BankUpdateForm;
import com.zhaogang.connector.admin.module.business.oa.bank.domain.BankVO;
import com.zhaogang.connector.base.common.domain.PageResult;
import com.zhaogang.connector.base.common.domain.RequestUser;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import com.zhaogang.connector.base.common.util.SmartRequestUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * OA办公-OA银行信息
 *
 * @Author 连接器:善逸
 * @Date 2022/6/23 21:59:22
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.OA_BANK)
public class BankController {

    @Resource
    private BankService bankService;

    @Operation(summary = "分页查询银行信息 @author 善逸")
    @PostMapping("/oa/bank/page/query")
    @SaCheckPermission("oa:bank:query")
    public ResponseDTO<PageResult<BankVO>> queryByPage(@RequestBody @Valid BankQueryForm queryForm) {
        return bankService.queryByPage(queryForm);
    }

    @Operation(summary = "根据企业ID查询银行信息列表 @author 善逸")
    @GetMapping("/oa/bank/query/list/{enterpriseId}")
    @SaCheckPermission("oa:bank:query")
    public ResponseDTO<List<BankVO>> queryList(@PathVariable Long enterpriseId) {
        return bankService.queryList(enterpriseId);
    }

    @Operation(summary = "查询银行信息详情 @author 善逸")
    @GetMapping("/oa/bank/get/{bankId}")
    @SaCheckPermission("oa:bank:query")
    public ResponseDTO<BankVO> getDetail(@PathVariable Long bankId) {
        return bankService.getDetail(bankId);
    }

    @Operation(summary = "新建银行信息 @author 善逸")
    @PostMapping("/oa/bank/create")
    @SaCheckPermission("oa:bank:add")
    public ResponseDTO<String> createBank(@RequestBody @Valid BankCreateForm createVO) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        createVO.setCreateUserId(requestUser.getUserId());
        createVO.setCreateUserName(requestUser.getUserName());
        return bankService.createBank(createVO);
    }

    @Operation(summary = "编辑银行信息 @author 善逸")
    @PostMapping("/oa/bank/update")
    @SaCheckPermission("oa:bank:update")
    public ResponseDTO<String> updateBank(@RequestBody @Valid BankUpdateForm updateVO) {
        return bankService.updateBank(updateVO);
    }

    @Operation(summary = "删除银行信息 @author 善逸")
    @GetMapping("/oa/bank/delete/{bankId}")
    @SaCheckPermission("oa:bank:delete")
    public ResponseDTO<String> deleteBank(@PathVariable Long bankId) {
        return bankService.deleteBank(bankId);
    }
}
