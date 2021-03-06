package com.github.zuihou.authority.service.common.impl;

import cn.hutool.core.convert.Convert;
import com.github.zuihou.authority.dao.common.AreaMapper;
import com.github.zuihou.authority.entity.common.Area;
import com.github.zuihou.authority.service.common.AreaService;
import com.github.zuihou.base.service.SuperCacheServiceImpl;
import com.github.zuihou.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.zuihou.common.constant.CacheKey.AREA;

/**
 * <p>
 * 业务实现类
 * 地区表
 * </p>
 *
 * @author zuihou
 * @date 2019-07-02
 */
@Slf4j
@Service
@CacheConfig(cacheNames = AREA)
public class AreaServiceImpl extends SuperCacheServiceImpl<AreaMapper, Area> implements AreaService {

    @Override
    protected String getRegion() {
        return AREA;
    }

    protected AreaService currentProxy() {
        return ((AreaService) AopContext.currentProxy());
    }

    @Override
    public boolean recursively(List<Long> ids) {
        boolean removeFlag = currentProxy().removeByIds(ids);
        delete(ids);
        return removeFlag;
    }

    private void delete(List<Long> ids) {
        // 查询子节点
        List<Long> childIds = super.listObjs(Wraps.<Area>lbQ().in(Area::getParentId, ids), Convert::toLong);
        if (!childIds.isEmpty()) {
            currentProxy().removeByIds(childIds);
            delete(childIds);
        }
        log.info("退出");
    }
}
