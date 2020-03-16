package com.busi.dao;

import com.busi.entity.ShopFloorGoodsCollection;
import com.busi.entity.ShopFloorGoodsLook;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收藏Dao
 * author：zhaojiajie
 * create time：2020-03-10 16:37:45
 */
@Mapper
@Repository
public interface ShopFloorOtherDao {
    /***
     * 新增浏览记录
     * @param look
     * @return
     */
    @Insert("insert into ShopFloorGoodsLook(userId,goodsName,goodsId,price,time,imgUrl) " +
            "values (#{userId},#{goodsName},#{goodsId},#{price},#{time},#{imgUrl})")
    @Options(useGeneratedKeys = true)
    int addLook(ShopFloorGoodsLook look);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
    @Delete("<script>" +
            "delete from ShopFloorGoodsLook" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{myId}" +
            "</script>")
    int delLook(@Param("ids") String[] ids, @Param("myId") long myId);

    /***
     * 分页查询 默认按时间降序排序
     * @param myId
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoodsLook" +
            " where 1=1" +
            "<if test=\"myId > 0\">" +
            " and userId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<ShopFloorGoodsLook> findLookList(@Param("myId") long myId);

    /***
     * 新增收藏
     * @param collect
     * @return
     */
    @Insert("insert into ShopFloorGoodsCollection(userId,goodsName,goodsId,price,time,imgUrl) " +
            "values (#{userId},#{goodsName},#{goodsId},#{price},#{time},#{imgUrl})")
    @Options(useGeneratedKeys = true)
    int addCollection(ShopFloorGoodsCollection collect);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
    @Delete("<script>" +
            "delete from ShopFloorGoodsCollection" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{myId}" +
            "</script>")
    int delCollection(@Param("ids") String[] ids, @Param("myId") long myId);

    /***
     * 分页查询 默认按时间降序排序
     * @param myId
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorGoodsCollection" +
            " where 1=1" +
            "<if test=\"myId > 0\">" +
            " and userId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<ShopFloorGoodsCollection> findCollectionList(@Param("myId") long myId);

    /***
     * 根据用户&主键ID查询
     * @param id
     */
    @Select("select * from ShopFloorGoodsCollection where goodsId=#{id} and userId=#{myId}")
    ShopFloorGoodsCollection findUserId(@Param("id") long id, @Param("myId") long myId);

    /***
     * 根据Id统计收藏数量
     * @param userId
     */
    @Select("select COUNT(id) from ShopFloorGoodsCollection where userId=#{userId}")
    int findUserCollect(@Param("userId") long userId);

    /***
     * 根据Id统计浏览数量
     * @param userId
     */
    @Select("select COUNT(id) from ShopFloorGoodsLook where userId=#{userId}")
    int findUserLook(@Param("userId") long userId);
}
