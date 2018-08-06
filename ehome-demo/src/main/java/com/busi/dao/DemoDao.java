package com.busi.dao;

import com.busi.entity.Demo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/6/11 16:27
 */
@Mapper
@Repository
public interface DemoDao {
    /***
     * 新增demo
     * @param demo
     * @return
     */
    @Insert("insert into demo(id,name) values (#{id},#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add( Demo demo);

    /***
     * 查询Demo
     * @param id
     */
    @Select("select * from demo where id = #{id}")
    Demo findDemoById(@Param("id") long id);

    /***
     * 更新
     * @param demo
     * @return
     */
    @Update(("update demo set name=#{name} where id=#{id}"))
    int update( Demo demo);

    /***
     * 删除
     * @param id
     * @return
     */
    @Delete(("delete from demo where id=#{id}"))
    int delete( long id);

    /***
     * 分页查询Demo
     */
//    @Select("select * from demo where name like '%孙%' order by name limit 0,5")
    @Select("select * from demo")
    List<Demo> findList();
}
