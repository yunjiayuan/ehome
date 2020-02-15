package com.busi.servive;

import com.busi.dao.EpidemicSituationDao;
import com.busi.entity.EpidemicSituation;
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

}
