package com.busi.dao;

import com.busi.entity.UserFriendGroup;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 * 好友分组DAO
 * author：SunTianJie
 * create time：2018/7/16 17:27
 */
@Mapper
@Repository
public interface UserFriendGroupDao {

    /***
     * 新增分组
     * @param userFriendGroup
     * @return
     */
    @Insert("insert into UserFriendGroup(id,userId,groupName,groupType) values (#{id},#{userId},#{groupName},#{groupType})")
    @Options(useGeneratedKeys = true)//keyProperty = "id" 默认为ID 可以不写
    int add(UserFriendGroup userFriendGroup);

    /***
     * 删除分组
     * @param userId
     * @param id
     * @return
     */
    @Delete(("delete from UserFriendGroup where userId=#{userId} and id=#{id}"))
    int del(@Param("userId") long userId, @Param("id") long id);

    /***
     * 查询好友分组列表 默认按名字升序排序
     * @param userId
     * @return
     */
    @Select("select * from UserFriendGroup where userId=#{userId} order by groupName asc")
    List<UserFriendGroup> findList(@Param("userId") long userId);

    /***
     * 更新分组名称
     * @param userFriendGroup
     * @return
     */
    @Update(("update UserFriendGroup set groupName=#{groupName} where userId=#{userId} and id=#{id}"))
    int updateGroupName(UserFriendGroup userFriendGroup);
}
