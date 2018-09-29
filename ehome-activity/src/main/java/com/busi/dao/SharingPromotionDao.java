package com.busi.dao;

import com.busi.entity.ShareRedPacketsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 新人分享Dao
 * author：zhaojiajie
 * create time：2018-9-28 12:55:11
 */
@Mapper
@Repository
public interface SharingPromotionDao {

    /***
     * 新增记录
     * @param shareRedPacketsInfo
     * @return
     */
    @Insert("insert into ShareRedPacketsInfo(shareUserId,beSharedUserId,redPacketsMoney,time) " +
            "values (#{shareUserId},#{beSharedUserId},#{redPacketsMoney},#{time})")
    @Options(useGeneratedKeys = true)
    int add(ShareRedPacketsInfo shareRedPacketsInfo);


    /***
     * 返回红包总数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShareRedPacketsInfo" +
            " where shareUserId=#{userId}" +
            "</script>")
    long findNum(@Param("userId") long userId);

    /***
     * 返回红包总金额
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select sum(redPacketsMoney) from ShareRedPacketsInfo" +
            " where shareUserId=#{userId}" +
            "</script>")
    double findSum(@Param("userId") long userId);

    /***
     * 分页查询砸蛋记录 默认按时间降序排序
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from ShareRedPacketsInfo" +
            " where shareUserId=#{userId}" +
            "</script>")
    List<ShareRedPacketsInfo> findList(@Param("userId") long userId);

    /***
     * 返回分享者分享人数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShareRedPacketsInfo" +
            " where shareUserId=#{userId}" +
            "</script>")
    long findPeople(@Param("userId") long userId);

}
