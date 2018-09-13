package com.busi.dao;

import com.busi.entity.RedBagRain;
import com.busi.entity.Task;
import com.busi.entity.TaskList;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务Dao
 * author：zhaojiajie
 * create time：2018-8-15 16:57:56
 */
@Mapper
@Repository
public interface TaskDao {

    /***
     * 新增任务
     * @param task
     * @return
     */
    @Insert("insert into task(userId,taskStatus,taskType,sortTask,time) " +
            "values (#{userId},#{taskStatus},#{taskType},#{sortTask},#{time})")
    @Options(useGeneratedKeys = true)
    int add(Task task);

    /***
     * 新增红包雨记录
     * @param redBagRain
     * @return
     */
    @Insert("insert into RedBagRain(userId,pizeType,quota,time) " +
            "values (#{userId},#{pizeType},#{quota},#{time})")
    @Options(useGeneratedKeys = true)
    int addRain(RedBagRain redBagRain);

    /***
     * 更新任务状态
     * @param task
     * @return
     */
    @Update("<script>" +
            "update task set" +
            " taskStatus=#{taskStatus}" +
            " where 1=1" +
            "<if test=\"taskType == 0\">" +
            " and taskType=#{taskType}" +
            "</if>" +
            "<if test=\"taskType == 1\">" +
            " and TO_DAYS(time)=TO_DAYS(NOW()) and taskType=#{taskType}" +
            "</if>" +
            " and sortTask=#{sortTask} " +
            " and taskStatus=1" +
            " and userId=#{userId}" +
            "</script>")
    int update(Task task);

    /***
     * 查询是否完成过任务
     * @param userId  用户ID
     * @param taskType  任务类型：0、一次性任务   1 、每日任务
     * @param sortTask  任务id
     */
    @Select("<script>" +
            "select * from task" +
            " where 1=1" +
            "<if test=\"taskType == 0\">" +
            " and taskType=#{taskType}" +
            "</if>" +
            "<if test=\"taskType == 1\">" +
            " and TO_DAYS(time)=TO_DAYS(NOW()) and taskType=#{taskType}" +
            "</if>" +
            " and sortTask=#{sortTask} " +
            " and userId=#{userId}" +
            "</script>")
    Task findUserById(@Param("userId") long userId, @Param("taskType") int taskType, @Param("sortTask") long sortTask);

    /***
     * 分页查询
     * @param userId  用户ID
     * @param taskType  任务类型：-1默认全部 0、一次性任务   1 、每日任务
     * @return
     */
    @Select("<script>" +
            "select * from task" +
            " where 1=1" +
            "<if test=\"taskType == -1\">" +
            " and ((TO_DAYS(time)=TO_DAYS(NOW()) and taskType=1) or taskType = 0)" +
            "</if>" +
            "<if test=\"taskType == 0\">" +
            " and taskType=#{taskType}" +
            "</if>" +
            "<if test=\"taskType == 1\">" +
            " and TO_DAYS(time)=TO_DAYS(NOW()) and taskType=#{taskType}" +
            "</if>" +
            " and userId=#{userId}" +
            "</script>")
    List<Task> findList(@Param("userId") long userId, @Param("taskType") int taskType);

    /***
     * 分页查询
     * @return
     */
    @Select("<script>" +
            "select * from taskList" +
            " where 1=1" +
            "<if test=\"taskType == -1\">" +
            " and taskType=1 or taskType = 0" +
            "</if>" +
            "<if test=\"taskType > -1\">" +
            " and taskType=#{taskType}" +
            "</if>" +
            "</script>")
    List<TaskList> findTaskList(@Param("taskType") int taskType);

    /***
     * 查询任务完成数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from Task" +
            " where userId=#{userId}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())" +
            "</script>")
    int findNum(@Param("userId") long userId);

    /***
     * 分页查询奖品列表
     * @param userId  用户ID
     * @return
     */
    @Select("<script>" +
            "select * from RedBagRain" +
            " where userId=#{userId}" +
            "</script>")
    List<RedBagRain> findPrizeList(@Param("userId") long userId);

    /***
     * 分页查询中奖人员列表
     * @return
     */
    @Select("<script>" +
            "select * from RedBagRain order by quota desc" +
            "</script>")
    List<RedBagRain> findRedBagList();

}
