package com.busi.dao;

import com.busi.entity.Task;
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
     * 新增其他公告
     * @param task
     * @return
     */
    @Insert("insert into task(userId,taskStatus,taskType,sortTask,time) " +
            "values (#{userId},#{taskStatus},#{taskType},#{sortTask},#{sortTask})")
    @Options(useGeneratedKeys = true)
    int add(Task task);

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
            " and taskType=0" +
            "</if>" +
            "<if test=\"taskType == 1\">" +
            " and trunc(time) = trunc(sysdate) and taskType=1" +
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
            " and taskType=0" +
            "</if>" +
            "<if test=\"taskType == 1\">" +
            " and trunc(time) = trunc(sysdate) and taskType=1" +
            "</if>" +
            " and sortTask=#{sortTask} " +
            " and userId=#{userId}" +
            "</script>")
    Task findUserById(@Param("userId") long userId, @Param("taskType") int taskType, @Param("sortTask") long sortTask);

    /***
     * 分页查询
     * @param userId  用户ID
     * @param taskType  任务类型：0、一次性任务   1 、每日任务
     * @return
     */
    @Select("<script>" +
            "select * from task" +
            " where 1=1" +
            "<if test=\"taskType == -1\">" +
            " and ((trunc(time) = trunc(sysdate) and taskType=1) or (taskType = 0))" +
            "</if>" +
            "<if test=\"taskType == 0\">" +
            " and taskType=0" +
            "</if>" +
            "<if test=\"taskType == 1\">" +
            " and trunc(time) = trunc(sysdate) and taskType=1" +
            "</if>" +
            " and userId=#{userId}" +
            "</script>")
    List<Task> findList(@Param("userId") long userId,@Param("taskType") int taskType);

    /***
     * 分页查询
     * @return
     */
    @Select("<script>" +
            "select * from taskList" +
            "</script>")
    List<Task> findTaskList();

}
