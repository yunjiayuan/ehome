package com.busi.dao;

import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationTianqi;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 疫情相关Dao
 * author：zhaojiajie
 * create time：2020-02-15 11:30:28
 */
@Mapper
@Repository
public interface EpidemicSituationDao {

    /***
     * 新增
     * @param epidemicSituation
     * @return
     */
    @Insert("insert into EpidemicSituation(modifyTime,createTime,imgUrl,dailyPic,summary,deleted,countRemark,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,seriousCount,remark1,remark2,remark3," +
            "remark4,remark5,note1,note2,note3,generalRemark,abroadRemark,quanguoTrendCharts,hbFeiHbTrendCharts,listByArea,listByOther,time)" +
            "values (#{modifyTime},#{createTime},#{imgUrl},#{dailyPic},#{summary},#{deleted},#{countRemark},#{currentConfirmedCount},#{confirmedCount},#{suspectedCount},#{curedCount},#{deadCount},#{seriousCount},#{remark1},#{remark2},#{remark3}" +
            ",#{remark4},#{remark5},#{note1},#{note2},#{note3},#{generalRemark},#{abroadRemark},#{quanguoTrendCharts},#{hbFeiHbTrendCharts},#{listByArea},#{listByOther},#{time})")
    @Options(useGeneratedKeys = true)
    int add(EpidemicSituation epidemicSituation);

    /***
     * 新增
     * @param epidemicSituation
     * @return
     */
    @Insert("insert into EpidemicSituationTianqi(date,diagnosed,suspect,death,cured,serious,diagnosedIncr,suspectIncr,deathIncr,curedIncr,seriousIncr,list,history,area,time)" +
            "values (#{date},#{diagnosed},#{suspect},#{death},#{cured},#{serious},#{diagnosedIncr},#{suspectIncr},#{deathIncr},#{curedIncr},#{seriousIncr},#{list},#{history},#{area},#{time})")
    @Options(useGeneratedKeys = true)
    int addTianQi(EpidemicSituationTianqi epidemicSituation);

    /***
     * 根据更新时间查疫情
     * @param modifyTime
     * @return
     */
    @Select("select * from EpidemicSituation where modifyTime=#{modifyTime}")
    EpidemicSituation findEpidemicSituation(@Param("modifyTime") long modifyTime);

    /***
     * 根据更新时间查疫情(天气平台)
     * @param modifyTime
     * @return
     */
    @Select("select * from EpidemicSituationTianqi where date=#{modifyTime}")
    EpidemicSituationTianqi findEStianQi(@Param("modifyTime") String modifyTime);
}
