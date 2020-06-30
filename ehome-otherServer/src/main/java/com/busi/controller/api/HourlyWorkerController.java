package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HourlyWorkerService;
import com.busi.service.UserAccountSecurityService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 小时工
 * @author: ZHaoJiaJie
 * @create: 2019-04-22 16:37
 */
@RestController
public class HourlyWorkerController extends BaseController implements HourlyWorkerApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    HourlyWorkerService hourlyWorkerService;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    /***
     * 新增小时工
     * @param hourlyWorker
     * @return
     */
    @Override
    public ReturnData addHourly(@Valid @RequestBody HourlyWorker hourlyWorker, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        int sex = 0;
        String name = "";
        long userId = hourlyWorker.getUserId();
        //判断是否已是小时工
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> hourlyMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYWORKER + hourlyWorker.getUserId());
        if (hourlyMap == null || hourlyMap.size() <= 0) {
            HourlyWorker worker = hourlyWorkerService.findByUserId(hourlyWorker.getUserId());
            if (worker != null) {
                //放入缓存
                hourlyMap = CommonUtils.objectToMap(worker);
                redisUtils.hmset(Constants.REDIS_KEY_HOURLYWORKER + worker.getUserId(), hourlyMap, Constants.USER_TIME_OUT);
            }
        }
        HourlyWorker ik = (HourlyWorker) CommonUtils.mapToObject(hourlyMap, HourlyWorker.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增小时工失败，该用户已是小时工！", new JSONObject());
        }
        //检测是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + hourlyWorker.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
            if (userAccountSecurity != null) {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
                //放到缓存中
                map = CommonUtils.objectToMap(userAccountSecurity);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
            }
        }
        if (map != null || map.size() > 0) {
            UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
            if (userAccountSecurity != null && !CommonUtils.checkFull(userAccountSecurity.getRealName()) && !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                name = userAccountSecurity.getRealName();//姓名
                sex = CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard());//性别
                Map<String, String> map2 = getBirAgeSex(userAccountSecurity.getIdCard());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = sdf.parse(map2.get("birthday"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                hourlyWorker.setBirthday(date);
                hourlyWorker.setSex(sex);
                hourlyWorker.setName(name);
            } else {//未实名获取缓存信息
                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(hourlyWorker.getUserId());
                if (sendInfoCache != null) {
                    hourlyWorker.setBirthday(sendInfoCache.getBirthday());
                    hourlyWorker.setSex(sendInfoCache.getSex());
                    hourlyWorker.setName(sendInfoCache.getName());
                }
            }
        }
        hourlyWorker.setAuditType(1);
        hourlyWorker.setBusinessStatus(1);//默认关闭
        hourlyWorker.setAddTime(new Date());

        hourlyWorkerService.addHourly(hourlyWorker);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", hourlyWorker.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新小时工
     * @param hourlyWorker
     * @return
     */
    @Override
    public ReturnData updateHourly(@Valid @RequestBody HourlyWorker hourlyWorker, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        hourlyWorkerService.updateHourly(hourlyWorker);
        if (!CommonUtils.checkFull(hourlyWorker.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(hourlyWorker.getUserId(), hourlyWorker.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOURLYWORKER + hourlyWorker.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除小时工
     * @return:
     */
    @Override
    public ReturnData delHourly(@PathVariable long userId, @PathVariable long id) {
        HourlyWorker io = hourlyWorkerService.findByUserId(userId);
        if (io != null) {
            io.setDeleteType(1);
            hourlyWorkerService.updateDel(io);
        }
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_HOURLYWORKER + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新小时工营业状态
     * @param hourlyWorker
     * @return
     */
    @Override
    public ReturnData updBusinessStatus(@Valid @RequestBody HourlyWorker hourlyWorker, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + hourlyWorker.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(hourlyWorker.getUserId());
            if (userAccountSecurity == null) {
                return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + hourlyWorker.getUserId(), map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null || CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        int sex = 0;
        String name = "";
        Date date = null;
        name = userAccountSecurity.getRealName();//姓名
        sex = CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard());//性别
        Map<String, String> map2 = getBirAgeSex(userAccountSecurity.getIdCard());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(map2.get("birthday"));//生日
        } catch (ParseException e) {
            e.printStackTrace();
        }
        hourlyWorker.setSex(sex);
        hourlyWorker.setName(name);
        hourlyWorker.setBirthday(date);
        hourlyWorkerService.updateBusiness(hourlyWorker);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_HOURLYWORKER + hourlyWorker.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询小时工信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData getHourly(@PathVariable long userId) {
        int collection = 0;//是否收藏过此小时工  0没有  1已收藏
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYWORKER + userId);
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            HourlyWorker kitchen = hourlyWorkerService.findByUserId(userId);
            if (kitchen == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen);
            redisUtils.hmset(Constants.REDIS_KEY_HOURLYWORKER + userId, kitchenMap, Constants.USER_TIME_OUT);
        }
        if (kitchenMap != null && kitchenMap.size() > 0) {
            //验证是否收藏过
            boolean flag = hourlyWorkerService.findWhether(CommonUtils.getMyId(), userId);
            if (flag) {
                collection = 1;//1已收藏
            }
        }
        kitchenMap.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 条件查询小时工
     * @param lat      纬度
     * @param lon      经度
     * @param name     用户名
     * @param page     页码
     * @param count    条数
     * @param watchVideos 筛选视频：0否 1是
     * @param sortType 排序类型：默认【0综合排序】   0综合排序  1距离最近  2服务次数最高  3评分最高
     * @return
     */
    @Override
    public ReturnData findHourlyList(@PathVariable int watchVideos, @PathVariable int sortType, @PathVariable String name, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
//        int raidus = 10000;    //半径/ M
        PageBean<HourlyWorker> pageBean = null;
        pageBean = hourlyWorkerService.findHourlyList(CommonUtils.getMyId(), watchVideos, sortType, lat, lon, name, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HourlyWorker ik = (HourlyWorker) list.get(i);

                double userlon = Double.valueOf(ik.getLon() + "");
                double userlat = Double.valueOf(ik.getLat() + "");

                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

                ik.setDistance(distance);//距离/m

                UserInfo sendInfoCache = null;
                sendInfoCache = userInfoUtils.getUserInfo(ik.getUserId());
                if (sendInfoCache != null) {
                    ik.setProTypeId(sendInfoCache.getProType());
                    ik.setHouseNumber(sendInfoCache.getHouseNumber());
                }
            }
//            if (sortType == 1) {//距离最近
//                Collections.sort(list, new Comparator<HourlyWorker>() {
//                    /*
//                     * int compare(Person o1, Person o2) 返回一个基本类型的整型，
//                     * 返回负数表示：o1 小于o2，
//                     * 返回0 表示：o1和p2相等，
//                     * 返回正数表示：o1大于o2
//                     */
//                    @Override
//                    public int compare(HourlyWorker o1, HourlyWorker o2) {
//                        // 按照距离进行正序排列
//                        if (o1.getDistance() > o2.getDistance()) {
//                            return 1;
//                        }
//                        if (o1.getDistance() == o2.getDistance()) {
//                            return 0;
//                        }
//                        return -1;
//                    }
//                });
//            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 更新实名信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData updateRealName(@PathVariable long userId) {
        int sex = 0;
        String name = "";
        //检测是否实名
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
            if (userAccountSecurity != null) {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
                //放到缓存中
                map = CommonUtils.objectToMap(userAccountSecurity);
                redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
            }
        }
        if (map != null || map.size() > 0) {
            UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
            if (userAccountSecurity != null) {
                if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) && !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                    name = userAccountSecurity.getRealName();//姓名
                    sex = CommonUtils.getSexByIdCard(userAccountSecurity.getIdCard());//性别
                    Map<String, String> map2 = getBirAgeSex(userAccountSecurity.getIdCard());
                    HourlyWorker worker = new HourlyWorker();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = sdf.parse(map2.get("birthday"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    worker.setBirthday(date);
                    worker.setSex(sex);
                    worker.setName(name);
                    worker.setUserId(userId);
                    hourlyWorkerService.updateRealName(worker);

                    //清除缓存
                    redisUtils.expire(Constants.REDIS_KEY_HOURLYWORKER + worker.getUserId(), 0);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增小时工收藏
     * @param collect
     * @return
     */
    @Override
    public ReturnData addHourlyCollect(@Valid @RequestBody HourlyWorkerCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证是否收藏过
        boolean flag = hourlyWorkerService.findWhether(collect.getMyId(), collect.getWorkerId());
        if (flag) {
            return returnData(StatusCode.CODE_COLLECTED_HOURLY_ERROR.CODE_VALUE, "您已收藏过此小时工", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOURLYWORKER + collect.getWorkerId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            HourlyWorker kitchen2 = hourlyWorkerService.findByUserId(collect.getWorkerId());
            if (kitchen2 == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "收藏失败，小时工不存在！", new JSONObject());
            }
            //放入缓存
            kitchenMap = CommonUtils.objectToMap(kitchen2);
            redisUtils.hmset(Constants.REDIS_KEY_HOURLYWORKER + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
        }
        HourlyWorker io = (HourlyWorker) CommonUtils.mapToObject(kitchenMap, HourlyWorker.class);
        if (io != null) {
            //添加收藏记录
            collect.setTime(new Date());
            collect.setWorkerCover(io.getCoverCover());
            collect.setWorkerName(io.getName());

            hourlyWorkerService.addCollect(collect);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询用户收藏列表
     * @param userId   用户ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHourlyCollectList(@PathVariable long userId, @PathVariable double lat, @PathVariable double lon, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HourlyWorkerCollection> pageBean;
        pageBean = hourlyWorkerService.findCollectionList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = null;
        List ktchenList = null;
        list = pageBean.getList();
        String workerIds = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HourlyWorkerCollection kc = (HourlyWorkerCollection) list.get(i);
                if (i == 0) {
                    workerIds = kc.getWorkerId() + "";//小时工ID
                } else {
                    workerIds += "," + kc.getWorkerId();
                }
            }
            //查詢小时工
            ktchenList = hourlyWorkerService.findKitchenList4(workerIds.split(","));
            if (ktchenList != null) {
                for (int i = 0; i < ktchenList.size(); i++) {
                    HourlyWorker ik = (HourlyWorker) ktchenList.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        HourlyWorkerCollection kc = (HourlyWorkerCollection) list.get(j);
                        if (ik != null && kc != null) {
                            if (ik.getUserId() == kc.getWorkerId()) {
                                double userlon = Double.valueOf(ik.getLon() + "");
                                double userlat = Double.valueOf(ik.getLat() + "");
                                //计算距离
                                int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));
                                kc.setDistance(distance);//距离/m
                                //评分
                                kc.setScore(ik.getTotalScore());
                                //到达时间
                                kc.setArriveTime(ik.getArriveTime());
                                //工种
                                kc.setWorkerType(ik.getWorkerType());
                                //年龄
                                kc.setAge(getAge(ik.getBirthday()));
                                //性别
                                kc.setSex(ik.getSex());
                            }
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /**
     * @Description: 删除收藏
     * @return:
     */
    @Override
    public ReturnData delHourlyCollect(@PathVariable String ids) {
        //查询数据库
        hourlyWorkerService.del(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增工作类型
     * @param hourlyWorkerType
     * @return
     */
    @Override
    public ReturnData addHourlyType(@Valid @RequestBody HourlyWorkerType hourlyWorkerType, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户工种数量   最多10条
        int num = hourlyWorkerService.findNum(hourlyWorkerType.getUserId());
        if (num >= 10) {//超过上限
            return returnData(StatusCode.CODE_COLLECTED_HOURLY_TOPLIMIT.CODE_VALUE, "工种类型已达上限", new JSONObject());
        }
        hourlyWorkerType.setAddTime(new Date());
        hourlyWorkerService.addDishes(hourlyWorkerType);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", hourlyWorkerType.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新工作类型
     * @param hourlyWorkerType
     * @return
     */
    @Override
    public ReturnData updateHourlyType(@Valid @RequestBody HourlyWorkerType hourlyWorkerType, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        hourlyWorkerService.updateDishes(hourlyWorkerType);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", hourlyWorkerType.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 删除工作类型
     * @return:
     */
    @Override
    public ReturnData delHourlyType(@PathVariable String ids) {
        //查询数据库
        hourlyWorkerService.delDishes(ids.split(","), CommonUtils.getMyId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询工作类型详情
     * @param id
     * @return
     */
    @Override
    public ReturnData getHourlyType(@PathVariable long id) {
        HourlyWorkerType dishes = hourlyWorkerService.disheSdetails(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", dishes);
    }

    /***
     * 分页查询用户工作类型列表
     * @param workerId  店铺ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHourlyTypeList(@PathVariable long workerId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HourlyWorkerType> pageBean = null;
        pageBean = hourlyWorkerService.findDishesList(workerId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 通过身份证号码获取出生日期、性别、年龄
     * @param certificateNo  身份证号
     * @return 返回的出生日期格式：1990-01-01   性别格式：F-女，M-男
     */
    public static Map<String, String> getBirAgeSex(String certificateNo) {
        String birthday = "";
        String age = "";
        String sexCode = "";

        int year = Calendar.getInstance().get(Calendar.YEAR);
        char[] number = certificateNo.toCharArray();
        boolean flag = true;
        if (number.length == 15) {
            for (int x = 0; x < number.length; x++) {
                if (!flag) return new HashMap<String, String>();
                flag = Character.isDigit(number[x]);
            }
        } else if (number.length == 18) {
            for (int x = 0; x < number.length - 1; x++) {
                if (!flag) return new HashMap<String, String>();
                flag = Character.isDigit(number[x]);
            }
        }
        if (flag && certificateNo.length() == 15) {
            birthday = "19" + certificateNo.substring(6, 8) + "-"
                    + certificateNo.substring(8, 10) + "-"
                    + certificateNo.substring(10, 12);
            sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 3, certificateNo.length())) % 2 == 0 ? "F" : "M";
            age = (year - Integer.parseInt("19" + certificateNo.substring(6, 8))) + "";
        } else if (flag && certificateNo.length() == 18) {
            birthday = certificateNo.substring(6, 10) + "-"
                    + certificateNo.substring(10, 12) + "-"
                    + certificateNo.substring(12, 14);
            sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 4, certificateNo.length() - 1)) % 2 == 0 ? "F" : "M";
            age = (year - Integer.parseInt(certificateNo.substring(6, 10))) + "";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("birthday", birthday);
        map.put("age", age);
        map.put("sexCode", sexCode);
        return map;
    }

    //根据生日计算年龄
    public int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                throw new IllegalArgumentException("年龄不能超过当前日期");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
            int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
            if (nowDayOfYear < bornDayOfYear) {
                age -= 1;
            }
        }
        return age;
    }
}
