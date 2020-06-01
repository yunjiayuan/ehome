package com.busi.dao;

import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.LawyerCircle;
import com.busi.entity.LawyerCircleRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 律师圈相关Dao
 * author：zhaojiajie
 * create time：2020-03-03 19:06:06
 */
@Mapper
@Repository
public interface LawyerCircleDao {

    /***
     * 更新咨询状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update LawyerCircleRecord set" +
            " consultationStatus=2" +
            " where orderNumber=#{orderNumber} and payState=1 and consultationStatus=1" +
            "</script>")
    int upConsultationStatus(LawyerCircleRecord kitchen);

    /***
     * 查询所有咨询中数据
     * @return
     */
    @Select("<script>" +
            "select * from LawyerCircleRecord" +
            " where deleteType = 0 and payState=1 and consultationStatus=1" +
            "</script>")
    List<LawyerCircleRecord> findList();
}
