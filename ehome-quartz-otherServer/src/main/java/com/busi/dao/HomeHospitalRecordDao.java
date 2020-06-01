package com.busi.dao;


import com.busi.entity.HomeHospitalRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

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
     * 更新咨询状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospitalRecord set" +
            " consultationStatus=2" +
            " where orderNumber=#{orderNumber} and payState=1 and consultationStatus=1" +
            "</script>")
    int upConsultationStatus(HomeHospitalRecord kitchen);

    /***
     * 查询所有咨询中数据
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospitalRecord" +
            " where deleteType = 0 and payState=1 and consultationStatus=1" +
            "</script>")
    List<HomeHospitalRecord> findList();

}
