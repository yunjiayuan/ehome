package com.busi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作工具类
 * author：SunTianJie
 * create time：2018/6/5 9:30
 */
@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;//处理字符串
    @Autowired
    private RedisTemplate redisTemplate;//处理对象类型

    /**
     * String Key 添加到缓存（永不失效）
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key,String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * String Key 添加到缓存 可以到期设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key,String value,long time){
        try {
            if(time>0){
                stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取缓存中指定key对应的value
     * @param key 键
     * @return 值
     */
    public Object getKey(String key){
        if(CommonUtils.checkFull(key)){
            return null;
        }
        return stringRedisTemplate.opsForValue().get(key);
    }
    /**
     * 删除指定缓存中指定的key
     * @param key 可以传一个值 或多个
     */
    public void delKey(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                stringRedisTemplate.delete(key[0]);
            }else{
                stringRedisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean isExistKey(String key){
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置key的失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key,long time){
        try {
            if(CommonUtils.checkFull(key)){
                return false;
            }
            if(time>=0){
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取指定key的过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key){
        if(CommonUtils.checkFull(key)){
            return 0;
        }
        return stringRedisTemplate.getExpire(key,TimeUnit.SECONDS);
    }
    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * HashGet 获取指定key对应value
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的hashMap
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取对象key 中的多个hash key 对应的value
     * @param key
     * @param key2
     * @return
     */
    public List<Object> multiGet(String key, String ... key2){
        return redisTemplate.opsForHash().multiGet(key,CollectionUtils.arrayToList(key2));
    }

    /**
     * HashSet 存储hashMap集合 （数据永不过期）
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 存储hashMap集合 带设置过期时间参数
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建，存在则覆盖（数据永不过期）
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建，存在则覆盖 （带设置过期时间）
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  time要大于0 如果time小于等于0 将设置无限期 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public long hashIncr(String key, String item,long by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public long hashDecr(String key, String item,long by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    //============================list=============================

    /**
     * 获取list列表的内容
     * @param key 键
     * @param start 开始  从0开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List getList(String key,long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long getListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object getListValueByIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将一个元素放入list中 （永不失效）
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean addList(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将一个元素放入list中 （可以设置失效时间）
     * @param key 键
     * @param value 值
     * @param time 时间(秒)  0 永不失效
     * @return
     */
    public boolean addList(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将整个list集合放入redis缓存中 （永不失效）
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean pushList(String key, List value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将整个list集合放入redis缓存中 （可设置失效时间）
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean pushList(String key, List value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean updateListByIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除List中的N个值为value的重复元素
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long removeList(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * 添加用户位置信息
     * @param key 存储的key  此处固定值 user_position
     * @param userId 用户ID
     * @param lat 纬度 小数点后6位
     * @param lon 经度 小数点后6位
     * @return
     */
    public void addPosition(String key,String userId,double lat,double lon){
        redisTemplate.opsForGeo().add(key,new Point(lat,lon),userId);
//        if(time>0)expire(key,time);
    }

    /***
     * 移出位置信息
     * @param key
     * @param userId
     * @return
     */
    public long delPosition(String key,String userId){
        return redisTemplate.opsForGeo().remove(key,userId);
    }

    /***
     * 查找以指定坐标为中心附近范围内的位置信息（支持设置范围半径、位置信息排序和条数限制）
     * @param key 存储的key 此处固定值 user_position
     * @param lat 纬度 小数点后6位
     * @param lon 经度 小数点后6位
     * @param radius 半径范围
     * @param direction 0升序 1倒序
     * @param limit
     * @return
     */
    public GeoResults<GeoLocation<String>> getPosition(String key,  double lat,double lon, double radius, int direction, int limit){
        Circle within = new Circle(lat,lon,radius);
        //设置geo查询参数
        GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
        geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();//查询返回结果包括距离和坐标
        if (direction==1) {//按查询出的坐标距离中心坐标的距离进行排序 倒序
            geoRadiusArgs.sortDescending();
        } else {//默认升序
            geoRadiusArgs.sortAscending();
        }
        geoRadiusArgs.limit(limit);//查询数量

        return redisTemplate.opsForGeo().radius(key,within,geoRadiusArgs);
    }

    /***
     * 计算两个用户之间的距离
     * @param key
     * @param userId1 此处是用户ID
     * @param userId2 此处是用户ID
     * @return
     */
    public Distance distanceGeo(String key, long userId1, long userId2) {
        return redisTemplate.opsForGeo().distance(key, userId1, userId2);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> getSet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean isMember(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long addSet(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long addSetAndTime(String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long getSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long removeSetByValues(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
