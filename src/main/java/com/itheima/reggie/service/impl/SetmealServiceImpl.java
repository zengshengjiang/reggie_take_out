package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl
        extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    
    @Autowired
    private SetmealDishService setmealDishService;
    
    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal
        this.save(setmealDto);
        
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        
        //    保存套餐和菜品的关联信息，操作setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
        
    }
    
    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确认可以删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);
        
        int count = this.count(wrapper);
        if (count > 0) {
            //如果不能删除，抛出异常
            throw new CustomException("有套餐正在售卖，无法删除");
        }
        //    可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);
        
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        //    删除关系表中的数据--setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
        
        
    }
}
