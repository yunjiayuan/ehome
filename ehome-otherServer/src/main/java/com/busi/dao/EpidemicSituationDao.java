package com.busi.dao;

import com.busi.entity.EpidemicSituation;
import com.busi.entity.EpidemicSituationAbout;
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
            "remark4,remark5,note1,note2,note3,generalRemark,abroadRemark,quanguoTrendCharts,hbFeiHbTrendCharts,listByArea,listByOther)" +
            "values (#{modifyTime},#{createTime},#{imgUrl},#{dailyPic},#{summary},#{deleted},#{countRemark},#{currentConfirmedCount},#{confirmedCount},#{suspectedCount},#{curedCount},#{deadCount},#{seriousCount},#{remark1},#{remark2},#{remark3}" +
            ",#{remark4},#{remark5},#{note1},#{note2},#{note3},#{generalRemark},#{abroadRemark},#{quanguoTrendCharts},#{hbFeiHbTrendCharts},#{listByArea},#{listByOther})")
    @Options(useGeneratedKeys = true)
    int add(EpidemicSituation epidemicSituation);

    /***
     * 查询列表
     * @return
     */
    @Select("<script>" +
//            "SELECT * FROM EpidemicSituation where id=( SELECT MAX(id) FROM EpidemicSituation )" +
            "SELECT * FROM EpidemicSituation" +
            " order by id desc" +
            "</script>")
    List<EpidemicSituation> findList();

    /***
     * 新增菜品
     * @param dishes
     * @return
     */
    @Insert("insert into EpidemicSituationAbout(userId,lat,lon,address,whatAmIdoing,whatIsDone,shoutSentence,later,addTime) " +
            "values (#{userId},#{lat},#{lon},#{address},#{whatAmIdoing},#{whatIsDone},#{shoutSentence},#{later},#{addTime})")
    @Options(useGeneratedKeys = true)
    int addESabout(EpidemicSituationAbout dishes);

    /***
     * 更新菜品
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update EpidemicSituationAbout set" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " address=#{address}," +
            " whatAmIdoing=#{whatAmIdoing}," +
            " whatIsDone=#{whatIsDone}," +
            " shoutSentence=#{shoutSentence}," +
            " later=#{later}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeESabout(EpidemicSituationAbout kitchenDishes);

    /***
     * 根据ID查询菜品
     * @param id
     * @return
     */
    @Select("select * from EpidemicSituationAbout where userId=#{id}")
    EpidemicSituationAbout findESabout(@Param("id") long id);
}
