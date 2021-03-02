package com.busi.service;

import com.busi.dao.PassProveDao;
import com.busi.entity.PassProve;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 社区出入证、证明相关接口
 * author：ZJJ
 * create time：2021-02-03 14:41:16
 */
@Service
public class PassProveService {

    @Autowired
    private PassProveDao passProveDao;

    /***
     * 更新出入证、证明审核状态
     * @param communityEventReporting
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int toExaminePassProve(PassProve communityEventReporting) {
        return passProveDao.toExaminePassProve(communityEventReporting);
    }

    /***
     * 新增出入证、证明
     * @param communityEventReporting
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPassProve(PassProve communityEventReporting) {
        return passProveDao.addPassProve(communityEventReporting);
    }

    /***
     * 根据ID查询出入证、证明
     * @param id
     * @return
     */
    public PassProve findPassProve(long id) {
        return passProveDao.findPassProve(id);
    }

    public PassProve findPassProve2(long communityId, long userId, int type) {
        return passProveDao.findPassProve2(communityId, userId, type);
    }

    /***
     * 查询出入证、证明列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<PassProve> findPassProveList(long communityId, long userId, int type, int auditType, int page, int count) {
        List<PassProve> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = passProveDao.findPassProveList(communityId, userId, type, auditType);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计各类审核数量
     * @return
     */
    public List<PassProve> countAuditType(int type) {
        List<PassProve> list;
        list = passProveDao.countAuditType(type);
        return list;
    }
}
