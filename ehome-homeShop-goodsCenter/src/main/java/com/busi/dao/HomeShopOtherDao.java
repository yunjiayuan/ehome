package com.busi.dao;

import com.busi.entity.HomeShopGoodsCollection;
import com.busi.entity.HomeShopGoodsLook;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收藏、浏览Dao
 * author：zhaojiajie
 * create time：2020-07-13 15:34:05
 */
@Mapper
@Repository
public interface HomeShopOtherDao {
    /***
     * 新增浏览记录
     * @param look
     * @return
     */
    @Insert("insert into HomeShopGoodsLook(userId,goodsName,goodsId,price,time,imgUrl,specs,basicDescribe) " +
            "values (#{userId},#{goodsName},#{goodsId},#{price},#{time},#{imgUrl},#{specs},#{basicDescribe})")
    @Options(useGeneratedKeys = true)
    int addLook(HomeShopGoodsLook look);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
    @Delete("<script>" +
            "delete from HomeShopGoodsLook" +
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
            "select * from HomeShopGoodsLook" +
            " where 1=1" +
            "<if test=\"myId > 0\">" +
            " and userId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<HomeShopGoodsLook> findLookList(@Param("myId") long myId);

    /***
     * 新增收藏
     * @param collect
     * @return
     */
    @Insert("insert into HomeShopGoodsCollection(userId,goodsName,goodsId,price,time,imgUrl,specs,basicDescribe) " +
            "values (#{userId},#{goodsName},#{goodsId},#{price},#{time},#{imgUrl},#{specs},#{basicDescribe})")
    @Options(useGeneratedKeys = true)
    int addCollection(HomeShopGoodsCollection collect);

    /***
     * 删除
     * @param ids
     * @param myId
     * @return
     */
    @Delete("<script>" +
            "delete from HomeShopGoodsCollection" +
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
            "select * from HomeShopGoodsCollection" +
            " where 1=1" +
            "<if test=\"myId > 0\">" +
            " and userId=#{myId}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<HomeShopGoodsCollection> findCollectionList(@Param("myId") long myId);

    /***
     * 根据用户&主键ID查询
     * @param id
     */
    @Select("select * from HomeShopGoodsCollection where goodsId=#{id} and userId=#{myId}")
    HomeShopGoodsCollection findUserId(@Param("id") long id, @Param("myId") long myId);

    /***
     * 根据Id统计收藏数量
     * @param userId
     */
    @Select("select COUNT(id) from HomeShopGoodsCollection where userId=#{userId}")
    int findUserCollect(@Param("userId") long userId);

    /***
     * 根据Id统计浏览数量
     * @param userId
     */
    @Select("select COUNT(id) from HomeShopGoodsLook where userId=#{userId}")
    int findUserLook(@Param("userId") long userId);
}
