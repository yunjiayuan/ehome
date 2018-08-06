package com.busi.service;

import com.busi.dao.VersionDao;
import com.busi.entity.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 版本号信息Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class VersionService {

    @Autowired
    private VersionDao versionDao;

    /***
     * 更新
     * @param version
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(Version version){
        return  versionDao.update(version);
    }

    /***
     * 更新
     * @param clientType 客户端类型 1：安卓 2 ：IOS
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public Version findVersion(int clientType){
        return  versionDao.findVersion(clientType);
    }

}
