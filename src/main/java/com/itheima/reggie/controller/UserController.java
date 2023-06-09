package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.SendMailUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private SendMailUtils sendMailUtils;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 发送手机验证码--手机验证码要钱，用邮箱代替
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            
             //重新设置验证码
            //String  code = "1234";
             
            //发送短信服务 -- 用邮箱代替
            SMSUtils.sendMessage("瑞吉外卖", "", phone, code);
            log.info("验证码:{}", code);
            //发送邮件
            //sendMailUtils.sendMail(phone, code);
            log.info("验证码:邮件已发送");
            
            //需要将生成的验证码保存
            session.setAttribute(phone, code);
            
            //将生成的验证码缓存到redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            
            
            return R.success("手机短信验证码发送成功");
        }
        return R.error("手机短信验证码发送失败");
    }
    
    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        System.out.println("map:" + map);
        //获取手机
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        
        //Object codeInSession = session.getAttribute(phone);
        //从redis获取缓存验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            
            User user = userService.getOne(wrapper);
            
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            
            //用户登录成功，删除缓存验证码
            redisTemplate.delete(phone);
            
            return R.success(user);
        }
        
        return R.error("登录失败");
    }
    
    
}
