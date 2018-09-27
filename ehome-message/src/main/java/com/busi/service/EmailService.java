package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.UserAccountSecurity;
import com.busi.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送邮件具体业务
 * author suntj
 * Create time 2018/6/3 16:43
 */
@Component
@Slf4j
public class EmailService implements MessageAdapter {

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;
    /**
     * 发送邮件
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        long userId = Long.parseLong(body.getString("userId"));
        String email = body.getString("email");
        String userName = body.getString("userName");
        int emailType = Integer.parseInt(body.getString("emailType"));
        String code = body.getString("code");
        //根据邮件类型 开始发送邮件
        String subject="";//邮件主题
        String mailMessge="";//邮件内容
        UserAccountSecurity userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(userId);
        if(emailType==1){//1解绑密保邮箱的验证邮件
            if(userAccountSecurity!=null&&!CommonUtils.checkFull(userAccountSecurity.getEmail())
                    &&email.equals(userAccountSecurity.getEmail())){
                subject = "您正在进行邮箱解绑验证操作";
                mailMessge ="<table width='615' cellpadding='0' cellspacing='0'>"+
                        "<tr>"+
                        "  <th style='height:49px; background:#0b5ca6;font-size:24px; color:#FFF; font-weight:normal;'>历程网络·云家园</th>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font style='display:block;font-size:22px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:45px 25px 0 25px; '>尊敬的用户["+userName+"]您好: </font></td>"+
                        "</tr>"+
                        "<tr>"+
                        "</tr>"+
                        "<tr>"+
                        "<td><p style=' display:block;color:#828282; height: 26px; font-size:20px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 100px; margin:0;'><em style='float:left;font-style:normal;'></em><font style='clear:both;'></font></p></td>"+
                        "</tr>"+
                        "<tr>"+
                        "<td><p style=' display:block;color:#828282; height: 26px; font-size:22px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 150px; margin:0;'><em style='float:left;font-style:normal;'>您的验证码是: "+code+"</em><font style='clear:both;'></font></p></td>"+
                        "</tr>"+
                        "<tr>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font style='font-size:14px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:50px 60px 0 60px; display:block;'>此验证码在10分钟内有效，请及时进行验证，感谢您对云家园的关注与支持!</font></td>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font  style='border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6;border-bottom:6px solid #0b5ca6; background:#FFF;padding:5px 60px 40px 60px; display:block;'></font></td>"+
                        "</tr>"+
                        "</table>";
            }else{
                log.info("邮箱解绑操作失败,邮箱[\"+email+\"]与被绑定邮箱不一致!");
                return;
            }
        }else if(emailType==2){//2修改密码的验证邮件
            if(userAccountSecurity!=null&&CommonUtils.checkFull(userAccountSecurity.getEmail())
                    &&email.equals(userAccountSecurity.getEmail())){
                subject = "您正在进行邮箱修改密码验证操作";
                mailMessge ="<table width='615' cellpadding='0' cellspacing='0'>"+
                        "<tr>"+
                        "  <th style='height:49px; background:#0b5ca6;font-size:24px; color:#FFF; font-weight:normal;'>历程网络·云家园</th>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font style='display:block;font-size:22px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:45px 25px 0 25px; '>尊敬的用户["+userName+"]您好: </font></td>"+
                        "</tr>"+
                        "<tr>"+
                        "</tr>"+
                        "<tr>"+
                        "<td><p style=' display:block;color:#828282; height: 26px; font-size:20px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 100px; margin:0;'><em style='float:left;font-style:normal;'></em><font style='clear:both;'></font></p></td>"+
                        "</tr>"+
                        "<tr>"+
                        "<td><p style=' display:block;color:#828282; height: 26px; font-size:22px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 150px; margin:0;'><em style='float:left;font-style:normal;'>您的验证码是:  "+code+"</em><font style='clear:both;'></font></p></td>"+
                        "</tr>"+
                        "<tr>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font style='font-size:14px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:50px 60px 0 60px; display:block;'>此验证码在10分钟内有效，请及时进行验证，感谢您对云家园的关注与支持!</font></td>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font  style='border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6;border-bottom:6px solid #0b5ca6; background:#FFF;padding:5px 60px 40px 60px; display:block;'></font></td>"+
                        "</tr>"+
                        "</table>";
            }else{
                log.info("修改密码操作失败,邮箱[\"+email+\"]与被绑定邮箱不一致!");
                return;
            }
        }else if(emailType==3){//3找回密码的验证邮件
            if(userAccountSecurity!=null&&CommonUtils.checkFull(userAccountSecurity.getEmail())
                    &&email.equals(userAccountSecurity.getEmail())){
                subject = "您正在进行邮箱找回密码验证操作";
                mailMessge ="<table width='615' cellpadding='0' cellspacing='0'>"+
                        "<tr>"+
                        "  <th style='height:49px; background:#0b5ca6;font-size:24px; color:#FFF; font-weight:normal;'>历程网络·云家园</th>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font style='display:block;font-size:22px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:45px 25px 0 25px; '>尊敬的用户["+userName+"]您好: </font></td>"+
                        "</tr>"+
                        "<tr>"+
                        "</tr>"+
                        "<tr>"+
                        "<td><p style=' display:block;color:#828282; height: 26px; font-size:20px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 100px; margin:0;'><em style='float:left;font-style:normal;'></em><font style='clear:both;'></font></p></td>"+
                        "</tr>"+
                        "<tr>"+
                        "<td><p style=' display:block;color:#828282; height: 26px; font-size:22px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 150px; margin:0;'><em style='float:left;font-style:normal;'>您的验证码是:  "+code+"</em><font style='clear:both;'></font></p></td>"+
                        "</tr>"+
                        "<tr>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font style='font-size:14px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:50px 60px 0 60px; display:block;'>此验证码在10分钟内有效，请及时进行验证，感谢您对云家园的关注与支持!</font></td>"+
                        "</tr>"+
                        "<tr>"+
                        "  <td><font  style='border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6;border-bottom:6px solid #0b5ca6; background:#FFF;padding:5px 60px 40px 60px; display:block;'></font></td>"+
                        "</tr>"+
                        "</table>";
            }else{
                log.info("邮箱找回密码验证操作失败,邮箱[\"+email+\"]与被绑定邮箱不一致!");
                return;
            }
        }else{//0绑定密保邮箱的验证邮件
            subject = "您正在进行邮箱绑定操作";
            mailMessge ="<table width='615' cellpadding='0' cellspacing='0'>"+
                    "<tr>"+
                    "  <th style='height:49px; background:#0b5ca6;font-size:24px; color:#FFF; font-weight:normal;'>历程网络·云家园</th>"+
                    "</tr>"+
                    "<tr>"+
                    "  <td><font style='display:block;font-size:22px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:45px 25px 0 25px; '>尊敬的用户["+userName+"]您好: </font></td>"+
                    "</tr>"+
                    "<tr>"+
                    "</tr>"+
                    "<tr>"+
                    "<td><p style=' display:block;color:#828282; height: 26px; font-size:20px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 100px; margin:0;'><em style='float:left;font-style:normal;'></em><font style='clear:both;'></font></p></td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td><p style=' display:block;color:#828282; height: 26px; font-size:22px;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:2px 25px 10px 150px; margin:0;'><em style='float:left;font-style:normal;'>您的绑定验证码是: "+code+"</em><font style='clear:both;'></font></p></td>"+
                    "</tr>"+
                    "<tr>"+
                    "</tr>"+
                    "<tr>"+
                    "  <td><font style='font-size:14px; color:#828282;border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6; background:#FFF;padding:50px 60px 0 60px; display:block;'>此验证码在10分钟内有效，请及时进行验证，感谢您对云家园的关注与支持!</font></td>"+
                    "</tr>"+
                    "<tr>"+
                    "  <td><font  style='border-left:6px solid #0b5ca6;border-right:6px solid #0b5ca6;border-bottom:6px solid #0b5ca6; background:#FFF;padding:5px 60px 40px 60px; display:block;'></font></td>"+
                    "</tr>"+
                    "</table>";
        }
        //发送邮件
        SendEmail sendMailUtil = new SendEmail();
        sendMailUtil.send(email, subject, mailMessge);
    }
}
