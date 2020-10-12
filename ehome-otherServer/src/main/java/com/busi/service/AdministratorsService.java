package com.busi.service;

import com.busi.dao.AdministratorsDao;
import com.busi.entity.Administrators;
import com.busi.entity.AdministratorsAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 管理员
 * @author: ZHaoJiaJie
 * @create: 2020-09-27 16:52:57
 */
@Service
public class AdministratorsService {

    @Autowired
    private AdministratorsDao epidemicSituationDao;

    /***
     * 新增管理员
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addAdministrator(Administrators selectionActivities) {
        return epidemicSituationDao.addAdministrator(selectionActivities);
    }

    /***
     * 更新管理员
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAdministrator(Administrators communityHouse) {
        return epidemicSituationDao.changeAdministrator(communityHouse);
    }

    /***
     * 查询管理员
     * @param userId
     * @return
     */
    public Administrators findByUserId(long userId) {
        return epidemicSituationDao.findByUserId(userId);
    }

    /***
     * 查询管理员权限
     * @param levels
     * @return
     */
    public AdministratorsAuthority findUserId(int levels) {
        return epidemicSituationDao.findUserId(levels);
    }

    /***
     * 查询管理员列表
     * @param levels
     * @return
     */
    public List<Administrators> findAdministratorlist(int levels) {
        List<Administrators> list = null;
        list = epidemicSituationDao.findAdministratorlist(levels);
        return list;
    }

    /***
     * 删除管理员
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delAdministrator(long userId) {
        return epidemicSituationDao.delAdministrator(userId);
    }
}
