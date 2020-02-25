package com.busi.servive;

import com.busi.dao.EpidemicSituationDao;
import com.busi.entity.CampaignAwardActivity;
import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationTianqi;
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
     * 新建（天气平台）
     * @param epidemicSituation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addTianQi(EpidemicSituationTianqi epidemicSituation) {
        return epidemicSituationDao.addTianQi(epidemicSituation);
    }

    /***
     * 根据更新时间查疫情
     * @param modifyTime
     * @return
     */
    public EpidemicSituation findEpidemicSituation(long modifyTime) {
        return epidemicSituationDao.findEpidemicSituation(modifyTime);
    }

    /***
     * 根据更新时间查疫情(天气平台)
     * @param modifyTime
     * @return
     */
    public EpidemicSituationTianqi findEStianQi(String modifyTime) {
        return epidemicSituationDao.findEStianQi(modifyTime);
    }

    /***
     * 查询真实用户战役作品
     * @return
     */
    public List<EpidemicSituation> getCampaignAward() {
        List<EpidemicSituation> list = null;
        list = epidemicSituationDao.getCampaignAward();
        return list;
    }

    /***
     * 更新投票数
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(CampaignAwardActivity selectionActivities) {
        return epidemicSituationDao.updateNumber(selectionActivities);
    }

}
