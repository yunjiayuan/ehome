package com.busi.service;

import com.busi.dao.EpidemicSituationDao;
import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationAbout;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 疫情
 * @author: ZHaoJiaJie
 * @create: 2020-02-15 11:26:52
 */
@Service
public class EpidemicSituationService {
    @Autowired
    private EpidemicSituationDao epidemicSituationDao;

    /***
     * 新建
     * @param epidemicSituation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(EpidemicSituation epidemicSituation) {
        return epidemicSituationDao.add(epidemicSituation);
    }

    /***
     * 查询列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<EpidemicSituation> findList(int page, int count) {

        List<EpidemicSituation> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findList();

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新建我和疫情
     * @param epidemicSituation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addESabout(EpidemicSituationAbout epidemicSituation) {
        return epidemicSituationDao.addESabout(epidemicSituation);
    }

    /***
     * 更新我和疫情
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeESabout(EpidemicSituationAbout kitchenDishes) {
        return epidemicSituationDao.changeESabout(kitchenDishes);
    }

    /***
     * 根据ID查询我和疫情
     * @param id
     * @return
     */
    public EpidemicSituationAbout findESabout(long id) {
        return epidemicSituationDao.findESabout(id);
    }

}
