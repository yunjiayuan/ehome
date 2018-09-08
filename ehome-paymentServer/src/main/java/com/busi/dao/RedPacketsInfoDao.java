package com.busi.dao;

import com.busi.entity.RedPacketsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 红包信息相关DAO
 * author：SunTianJie
 * create time：2018-8-16 11:38:27
 */
@Mapper
@Repository
public interface RedPacketsInfoDao {

    /***
     * 新增
     * @param redPacketsInfo
     * @return
     */
    @Insert("insert into redPacketsInfo (sendUserId,receiveUserId,redPacketsMoney,sendMessage,receiveMessage,sendTime,receiveTime,delStatus,payStatus,redPacketsStatus) " +
            "values (#{sendUserId},#{receiveUserId},#{redPacketsMoney},#{sendMessage},#{receiveMessage},#{sendTime},#{receiveTime},#{delStatus},#{payStatus},#{redPacketsStatus})")
    @Options(useGeneratedKeys = true)
    int addRedPacketsInfo(RedPacketsInfo redPacketsInfo);

    /***
     * 根据红包ID查询红包信息
     * @param userId
     * @param id
     * @return
     */
    @Select("select * from redPacketsInfo where (sendUserId = #{userId} or receiveUserId = #{userId}) and id = #{id}")
    RedPacketsInfo findRedPacketsInfo(@Param("userId") long userId,@Param("id") long id);

    /**
     * 查询红包列表
     * @param findType 1查询用户类型 1只查当前用户发的 2只查当前用户收的
     * @param userId   查询用户
     * @param time     年份 格式2018 起始值2018
     * @return
     */
    @Select("<script>" +
            "select * from redPacketsInfo" +
            " where 1=1" +
            "<if test=\"time != 0 \">"+
            " and YEAR(sendTime) = #{time}" +
            "</if>" +
            "<if test=\"findType == 1 \">"+
            " and sendUserId = #{userId}" +
            " order by sendTime desc" +
            "</if>" +
            "<if test=\"findType == 2 \">"+
            " and receiveUserId = #{userId}" +
            " order by receiveTime desc" +
            "</if>" +
            "</script>")
    List<RedPacketsInfo> findRedPacketsInfoList(@Param("findType") long findType,@Param("userId") long userId, @Param("time") int time);

    /***
     * 发红包支付成功后更新红包支付状态
     * @param redPacketsInfo
     * @return
     */
    @Update("update redPacketsInfo set payStatus = #{payStatus} where sendUserId = #{sendUserId} and id = #{id}")
    int updateRedPacketsPayStatus(RedPacketsInfo redPacketsInfo);

    /***
     * 拆红包后更新留言
     * @param redPacketsInfo
     * @return
     */
    @Update("update redPacketsInfo set receiveMessage = #{receiveMessage} where receiveUserId = #{receiveUserId} and id = #{id} and redPacketsStatus = 2 and receiveMessage is null")
    int updateRedPacketsReceiveMessage(RedPacketsInfo redPacketsInfo);

    /***
     * 拆红包后更新红包状态
     * @param redPacketsInfo
     * @return
     */
    @Update("update redPacketsInfo set redPacketsStatus = #{redPacketsStatus},receiveTime = #{receiveTime} where receiveUserId = #{receiveUserId} and id = #{id}")
    int updateRedPacketsStatus(RedPacketsInfo redPacketsInfo);

    /***
     * 更改红包删除状态
     * @param userId
     * @param id
     * @return
     */
    @Update("update redPacketsInfo set delStatus = #{delStatus} where  (sendUserId = #{userId} or receiveUserId = #{userId}) and id = #{id}")
    int updateRedPacketsDelStatus(@Param("userId") long userId,@Param("id") long id);
}
