package com.zhaogang.connector.admin.module.system.role.service;

import com.google.common.collect.Lists;
import com.zhaogang.connector.admin.module.system.menu.dao.MenuDao;
import com.zhaogang.connector.admin.module.system.menu.domain.entity.MenuEntity;
import com.zhaogang.connector.admin.module.system.menu.domain.vo.MenuSimpleTreeVO;
import com.zhaogang.connector.admin.module.system.menu.domain.vo.MenuVO;
import com.zhaogang.connector.admin.module.system.role.dao.RoleDao;
import com.zhaogang.connector.admin.module.system.role.dao.RoleMenuDao;
import com.zhaogang.connector.admin.module.system.role.domain.entity.RoleEntity;
import com.zhaogang.connector.admin.module.system.role.domain.entity.RoleMenuEntity;
import com.zhaogang.connector.admin.module.system.role.domain.form.RoleMenuUpdateForm;
import com.zhaogang.connector.admin.module.system.role.domain.vo.RoleMenuTreeVO;
import com.zhaogang.connector.admin.module.system.role.manager.RoleMenuManager;
import com.zhaogang.connector.base.common.code.UserErrorCode;
import com.zhaogang.connector.base.common.domain.ResponseDTO;
import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色-菜单
 *
 * @Author 连接器: 善逸
 * @Date 2021-10-22 23:17:47
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Service
public class RoleMenuService {

    @Resource
    private RoleDao roleDao;
    @Resource
    private RoleMenuDao roleMenuDao;
    @Resource
    private RoleMenuManager roleMenuManager;
    @Resource
    private MenuDao menuDao;

    /**
     * 更新角色权限
     *
     */
    public ResponseDTO<String> updateRoleMenu(RoleMenuUpdateForm roleMenuUpdateForm) {
        //查询角色是否存在
        Long roleId = roleMenuUpdateForm.getRoleId();
        RoleEntity roleEntity = roleDao.selectById(roleId);
        if (null == roleEntity) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }
        List<RoleMenuEntity> roleMenuEntityList = Lists.newArrayList();
        RoleMenuEntity roleMenuEntity;
        for (Long menuId : roleMenuUpdateForm.getMenuIdList()) {
            roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setRoleId(roleId);
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntityList.add(roleMenuEntity);
        }
        roleMenuManager.updateRoleMenu(roleMenuUpdateForm.getRoleId(), roleMenuEntityList);
        return ResponseDTO.ok();
    }

    /**
     * 根据角色id集合，查询其所有的菜单权限
     *
     */
    public List<MenuVO> getMenuList(List<Long> roleIdList, Boolean administratorFlag) {
        //管理员返回所有菜单
        if(administratorFlag){
            List<MenuEntity> menuEntityList = roleMenuDao.selectMenuListByRoleIdList(Lists.newArrayList(), false);
            return SmartBeanUtil.copyList(menuEntityList, MenuVO.class);
        }
        //非管理员 无角色 返回空菜单
        if (CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        List<MenuEntity> menuEntityList = roleMenuDao.selectMenuListByRoleIdList(roleIdList, false);
        return SmartBeanUtil.copyList(menuEntityList, MenuVO.class);
    }


    /**
     * 获取角色关联菜单权限
     *
     */
    public ResponseDTO<RoleMenuTreeVO> getRoleSelectedMenu(Long roleId) {
        RoleMenuTreeVO res = new RoleMenuTreeVO();
        res.setRoleId(roleId);
        //查询角色ID选择的菜单权限
        List<Long> selectedMenuId = roleMenuDao.queryMenuIdByRoleId(roleId);
        res.setSelectedMenuId(selectedMenuId);
        //查询菜单权限
        List<MenuVO> menuVOList = menuDao.queryMenuList(Boolean.FALSE, Boolean.FALSE, null);
        Map<Long, List<MenuVO>> parentMap = menuVOList.stream().collect(Collectors.groupingBy(MenuVO::getParentId, Collectors.toList()));
        List<MenuSimpleTreeVO> menuTreeList = this.buildMenuTree(parentMap, NumberUtils.LONG_ZERO);
        res.setMenuTreeList(menuTreeList);
        return ResponseDTO.ok(res);
    }

    /**
     * 构建菜单树
     *
     */
    private List<MenuSimpleTreeVO> buildMenuTree(Map<Long, List<MenuVO>> parentMap, Long parentId) {
        // 获取本级菜单树List
        List<MenuSimpleTreeVO> res = parentMap.getOrDefault(parentId, Lists.newArrayList()).stream()
                .map(e -> SmartBeanUtil.copy(e, MenuSimpleTreeVO.class)).collect(Collectors.toList());
        // 循环遍历下级菜单
        res.forEach(e -> {
            e.setChildren(this.buildMenuTree(parentMap, e.getMenuId()));
        });
        return res;
    }
}
