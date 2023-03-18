package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl
        extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
    
    @Autowired
    private DishService dishService;
    
    @Autowired
    private SetmealService setmealService;
    
    /**
     * 根据id删除分类，删除之前需要判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(wrapper);
        //查询当前分类是否关联了菜品，如已经关联，抛出异常
        if (count > 0) {
            //关联了菜品,抛出一个业务异常
            throw new CustomException("当前分类关联了菜品,不能删除");
        }
        //查询当前分类是否关联了套餐，如已经关联，抛出异常
        LambdaQueryWrapper<Setmeal> wrapper2 = new LambdaQueryWrapper();
        wrapper2.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(wrapper2);
        if (count2 > 0) {
            //关联了套餐,抛出一个业务异常
            throw new CustomException("当前分类关联了套餐,不能删除");
        }
        
        super.removeById(id);
    }
}
