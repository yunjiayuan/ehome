package com.busi.dao;

import com.busi.entity.UserInfo;
import com.busi.entity.UserRelationShip;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 好友关系DAO
 * author：SunTianJie
 * create time：2018/7/16 17:27
 */
@Mapper
@Repository
public interface UserRelationShipDao {

    /***
     * 新增好友关系
     * @param userRelationShip
     * @return
     */
    @Insert("insert into UserRelationShip(id,userId,friendId,friendType,remarkName,groupId,time) values (#{id},#{userId},#{friendId},#{friendType},#{remarkName},#{groupId},#{time})")
    @Options(useGeneratedKeys = true)//keyProperty = "id" 默认为ID 可以不写
    int add(UserRelationShip userRelationShip);

    /***
     * 删除好友关系
     * @param userId
     * @param friendId
     * @return
     */
    @Delete(("delete from UserRelationShip where (userId=#{userId} and friendId=#{friendId}) or (userId=#{friendId} and friendId=#{userId})"))
    int del(@Param("userId") long userId,@Param("friendId") long friendId);

    /***
     * 查询好友列表 默认按名字升序排序
     * @param userId
     * @return
     */
    @Select("select * from UserRelationShip where userId=#{userId}")
    List<UserRelationShip> findList(@Param("userId") long userId);

    /***
     * 更新好友备注名
     * @param userRelationShip
     * @return
     */
    @Update(("update UserRelationShip set remarkName=#{remarkName} where userId=#{userId} and friendId=#{friendId}"))
    int updateRemarkName(UserRelationShip userRelationShip);

    /***
     * 将好友移动到指定分组
     * @param userRelationShip
     * @return
     */
    @Update(("update UserRelationShip set groupId=#{groupId} where userId=#{userId} and friendId=#{friendId}"))
    int moveFriend(UserRelationShip userRelationShip);

    /***
     * 验证指定双方是否为好友关系
     * @param userId   登录者ID
     * @param friendId 要验证的用户ID
     * @return
     */
    @Select("select * from UserRelationShip where (userId=#{userId} and friendId=#{friendId}) or (userId=#{friendId} and friendId=#{userId})")
    List<UserRelationShip> checkFriend(@Param("userId") long userId,@Param("friendId") long friendId);
}
