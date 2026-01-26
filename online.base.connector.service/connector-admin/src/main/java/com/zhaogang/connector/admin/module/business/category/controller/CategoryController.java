package com.zhaogang.connector.admin.module.business.category.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.zhaogang.connector.admin.constant.AdminSwaggerTagConst;
import com.zhaogang.connector.admin.module.business.category.domain.form.CategoryAddForm;
import com.zhaogang.connector.admin.module.business.category.domain.form.CategoryTreeQueryForm;
import com.zhaogang.connector.admin.module.business.category.domain.form.CategoryUpdateForm;
import com.zhaogang.connector.admin.module.business.category.domain.vo.CategoryTreeVO;
import com.zhaogang.connector.admin.module.business.category.domain.vo.CategoryVO;
import com.zhaogang.connector.admin.module.business.category.service.CategoryService;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 类目
 *
 * @Author 连接器: 胡克
 * @Date 2021/08/05 21:26:58
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.MANAGER_CATEGORY)
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "添加类目 @author 胡克")
    @PostMapping("/category/add")
    @SaCheckPermission("category:add")
    public ResponseDTO<String> add(@RequestBody @Valid CategoryAddForm addForm) {
        return categoryService.add(addForm);
    }

    @Operation(summary = "更新类目 @author 胡克")
    @PostMapping("/category/update")
    @SaCheckPermission("category:update")
    public ResponseDTO<String> update(@RequestBody @Valid CategoryUpdateForm updateForm) {
        return categoryService.update(updateForm);
    }

    @Operation(summary = "查询类目详情 @author 胡克")
    @GetMapping("/category/{categoryId}")
    public ResponseDTO<CategoryVO> queryDetail(@PathVariable Long categoryId) {
        return categoryService.queryDetail(categoryId);
    }

    @Operation(summary = "查询类目层级树 @author 胡克")
    @PostMapping("/category/tree")
    @SaCheckPermission("category:tree")
    public ResponseDTO<List<CategoryTreeVO>> queryTree(@RequestBody @Valid CategoryTreeQueryForm queryForm) {
        return categoryService.queryTree(queryForm);
    }

    @Operation(summary = "删除类目 @author 胡克")
    @GetMapping("/category/delete/{categoryId}")
    @SaCheckPermission("category:delete")
    public ResponseDTO<String> delete(@PathVariable Long categoryId) {
        return categoryService.delete(categoryId);
    }
}
