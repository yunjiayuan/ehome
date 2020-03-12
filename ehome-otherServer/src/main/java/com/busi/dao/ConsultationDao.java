package com.busi.dao;

import com.busi.entity.ConsultationFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 律师医生咨询相关Dao
 * author：zhaojiajie
 * create time：2020-03-12 16:21:24
 */
@Mapper
@Repository
public interface ConsultationDao {

    /***
     * 查询收费信息
     * @param occupation 职业：0医生  1律师
     * @param title 职称
     * @param type     咨询类型：0语音、视频  1图文
     * @return
     */
    @Select("<script>" +
            "select * from ConsultationFee" +
            " where 1=1" +
            " and occupation=#{occupation}" +
            " and type=#{type}" +
            " and title=#{title}" +
            " order by duration asc" +
            "</script>")
    List<ConsultationFee> findList(@Param("occupation") int occupation, @Param("title") int title, @Param("type") int type);
}
