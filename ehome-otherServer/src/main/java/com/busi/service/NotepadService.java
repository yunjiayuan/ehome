package com.busi.service;

import com.busi.dao.NotepadDao;
import com.busi.entity.Notepad;
import com.busi.entity.NotepadFestival;
import com.busi.entity.NotepadLunar;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 记事本
 * @author: ZHaoJiaJie
 * @create: 2018-10-11 15:10
 */
@Service
public class NotepadService {

    @Autowired
    private NotepadDao notepadDao;

    /***
     * 新增
     * @param notepad
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(Notepad notepad) {
        return notepadDao.add(notepad);
    }

    /***
     * 更新
     * @param notepad
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(Notepad notepad) {
        return notepadDao.update(notepad);
    }

    /***
     * 查询用户记事
     * @return
     */
    public Notepad findDayInfo(long userId, long thisDateId) {
        return notepadDao.findDayInfo(userId, thisDateId);
    }

    /***
     * 统计该用户当天日程数量
     * @param userId
     * @param type 0日程 1记事
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId, int type) {
        return notepadDao.findNum(userId, type);
    }

    /***
     * 删除
     * @param id 将要删除的ID
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id) {
        return notepadDao.del(id);
    }

    /***
     * 根据ID查询用户记事
     * @return
     */
    public Notepad findById(long id) {
        return notepadDao.findById(id);
    }

    /***
     * 按月查询带标记的日期
     * @param userId
     * @return
     */
    public List<Notepad> findIdentify(long userId, long startTime, long endTime) {
        List<Notepad> list;
        list = notepadDao.findIdentify(userId, startTime, endTime);
        return list;
    }

    /***
     * 获取我某天的记事
     * @param userId
     * @return
     */
    public List<Notepad> findThisDateRecord(long userId, long thisDateId, int options) {
        List<Notepad> list;
        list = notepadDao.findThisDateRecord(userId, thisDateId, options);
        return list;
    }

    /***
     * 分页查询我的记事
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<Notepad> findList(long userId, int options, int page, int count) {

        List<Notepad> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = notepadDao.findList(userId, options);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 获取指定年份黄历记载
     * @param thisYearId 年份 格式 2016
     * @return
     */
    public List<NotepadLunar> findAlmanac(long thisYearId) {
        String num = thisYearId + "0101";
        String num2 = thisYearId + "1231";
        List<NotepadLunar> list;
        list = notepadDao.findAlmanac(num, num2);
        return list;
    }

    /**
     * 查询黄历详情
     *
     * @param calendar; //查询黄历详情：格式20180101
     * @return
     */
    public NotepadLunar findDetails(int calendar) {
        return notepadDao.findDetails(calendar);
    }

    /***
     * 更新黄历
     * @param notepad
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNotepadLunar(NotepadLunar notepad) {
        return notepadDao.updateNotepadLunar(notepad);
    }

    /**
     * 获取指定年份法定节假日加班日安排时间
     *
     * @param thisYearId
     * @return
     */
    public NotepadFestival findCalendarsHoliday(long thisYearId) {
        return notepadDao.findCalendarsHoliday(thisYearId);
    }

    /***
     * 获取我某天的记事
     * @param userId
     * @return
     */
    public List<Notepad> findThisDateId(long userId, long thisDateId, int options) {
        List<Notepad> list;
        list = notepadDao.findThisDateId(userId, thisDateId, options);
        return list;
    }

}
