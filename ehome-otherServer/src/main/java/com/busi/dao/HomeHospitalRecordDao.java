package com.busi.dao;


import com.busi.entity.HomeHospitalRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 家医馆咨询相关Dao
 * author：zhaojiajie
 * create time：2020-1-8 11:08:17
 */
@Mapper
@Repository
public interface HomeHospitalRecordDao {

    /***
     * 新增
     * @param kitchen
     * @return
     */
    @Insert("insert into HomeHospitalRecord(userId,doctorId,prescribed,content,addTime,refreshTime,orderNumber,money,type,title,duration)" +
            "values (#{userId},#{doctorId},#{prescribed},#{content},#{addTime},#{refreshTime},#{orderNumber},#{money},#{type},#{title},#{duration})")
    @Options(useGeneratedKeys = true)
    int add(HomeHospitalRecord kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospitalRecord set" +
            " state=#{state}," +
            " refreshTime=#{refreshTime}," +
            " prescribed=#{prescribed}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(HomeHospitalRecord kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospitalRecord set" +
            " consultationStatus=1" +
            " where orderNumber=#{orderNumber} and payState=1" +
            "</script>")
    int upConsultationStatus(HomeHospitalRecord kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospitalRecord set" +
            " actualDuration=#{actualDuration}" +
            " where orderNumber=#{orderNumber} and payState=1 and consultationStatus=1" +
            "</script>")
    int upActualDuration(HomeHospitalRecord kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospitalRecord set" +
            " payState=#{payState}," +
            " time=#{time}" +
            " where orderNumber=#{orderNumber}" +
            "</script>")
    int updateOrders(HomeHospitalRecord kitchen);

    /***
     * 更新删除状态
     * @param userId
     * @return
     */
    @Update("<script>" +
            "update HomeHospitalRecord set" +
            " deleteType=1" +
            " where id = #{id}" +
            " and (userId=#{userId} or doctorId=#{userId})" +
            "</script>")
    int del(@Param("id") long id, @Param("userId") long userId);

    /***
     * 查询列表
     * @param haveDoctor  有无医嘱：0全部 1没有
     * @param identity   身份区分：0用户查 1医师查
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospitalRecord" +
            " where deleteType = 0 " +
            "<if test=\"identity == 0\">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"identity == 1\">" +
            " and doctorId = #{userId}" +
            "</if>" +
            "<if test=\"haveDoctor == 1\">" +
            " and prescribed is null" +
            "</if>" +
            " order by refreshTime desc" +
            "</script>")
    List<HomeHospitalRecord> findList(@Param("userId") long userId, @Param("haveDoctor") int haveDoctor, @Param("identity") int identity);

}
