package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("提交订单成功");
    }
    
    /**
     * 前台获取订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<IPage> userPage(int page, int pageSize) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return R.error("请先登录");
        }
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        //根据用户id查询订单
        wrapper.eq(Orders::getUserId, userId);
        //根据订单创建时间倒序排列
        wrapper.orderByDesc(Orders::getOrderTime);
        
        orderService.page(pageInfo, wrapper);
        
        return R.success(pageInfo);
    }
    
    /**
     * 后台获取订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<IPage> page(int page, int pageSize) {
        
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        //根据订单创建时间倒序排列
        wrapper.orderByDesc(Orders::getOrderTime);
        
        orderService.page(pageInfo, wrapper);
        
        return R.success(pageInfo);
    }
    
    /**
     * 更新订单状态
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders) {
        //System.out.println("id:" + orders.getId());
        //System.out.println("status:" + orders.getStatus());
        //LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        //wrapper.eq(Orders::getId, orders.getId());
        //orderService.update(orders, wrapper);
        orderService.updateById(orders);
        
        return R.success("更新成功");
    }
    
}
