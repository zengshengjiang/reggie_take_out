package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    
    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        System.out.println(shoppingCart);
        log.info(shoppingCart.toString());
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        
        
        Long dishId = shoppingCart.getDishId();
        
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        
        
        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        
        return R.success(cartServiceOne);
    }
    
    /**
     * 减少数量
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        System.out.println(shoppingCart);
        log.info(shoppingCart.toString());
        //用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        
        
        Long dishId = shoppingCart.getDishId();
        
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        
        //判断传入的是菜品还是套餐
        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        
        //在购物车中
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            
            if (number > 0) {
                int num = number - 1;
                cartServiceOne.setNumber(num);
                //如果数量为0，则删除，否则就更新
                if (num == 0){
                    shoppingCartService.removeById(cartServiceOne);
                }else{
                    shoppingCartService.updateById(cartServiceOne);
                }
                
              
            }
        } else {
            return R.error("该商品不在购物车内");
        }
        
        return R.success(cartServiceOne);
    }
    
    
    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentId = BaseContext.getCurrentId();
        
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        
        return R.success(list);
    }
    
    /**
     * 清空购物车
     *
     * @return
     */
    //@DeleteMapping("/delete")
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        
        shoppingCartService.remove(queryWrapper);
        
        return R.success("清空购物车成功");
    }
    
    
}
