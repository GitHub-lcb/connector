package com.zhaogang.connector.base.module.support.reload;

import com.zhaogang.connector.base.common.util.SmartBeanUtil;
import com.zhaogang.connector.base.module.support.reload.core.AbstractSmartReloadCommand;
import com.zhaogang.connector.base.module.support.reload.core.domain.SmartReloadItem;
import com.zhaogang.connector.base.module.support.reload.core.domain.SmartReloadResult;
import com.zhaogang.connector.base.module.support.reload.dao.ReloadItemDao;
import com.zhaogang.connector.base.module.support.reload.dao.ReloadResultDao;
import com.zhaogang.connector.base.module.support.reload.domain.ReloadItemEntity;
import com.zhaogang.connector.base.module.support.reload.domain.ReloadResultEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * reload 操作
 *
 * @Author 连接器-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Component
public class ReloadCommand extends AbstractSmartReloadCommand {

    @Resource
    private ReloadItemDao reloadItemDao;

    @Resource
    private ReloadResultDao reloadResultDao;

    /**
     * 读取数据库中SmartReload项
     *
     * @return List<ReloadItem>
     */
    @Override
    public List<SmartReloadItem> readReloadItem() {
        List<ReloadItemEntity> reloadItemEntityList = reloadItemDao.selectList(null);
        return SmartBeanUtil.copyList(reloadItemEntityList, SmartReloadItem.class);
    }


    /**
     * 保存reload结果
     *
     * @param smartReloadResult
     */
    @Override
    public void handleReloadResult(SmartReloadResult smartReloadResult) {
        ReloadResultEntity reloadResultEntity = SmartBeanUtil.copy(smartReloadResult, ReloadResultEntity.class);
        reloadResultDao.insert(reloadResultEntity);
    }
}
