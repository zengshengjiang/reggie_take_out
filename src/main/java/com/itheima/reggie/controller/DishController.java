package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    
    @Autowired
    private DishFlavorService dishFlavorService;
    
    @Autowired
    private CategoryService categoryService;
    
    
    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        
        return R.success("新增菜品成功");
    }
    
    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        
        //条件构造器对象
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        wrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, wrapper);
        
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            
            BeanUtils.copyProperties(item, dishDto);
            
            Long categoryId = item.getCategoryId();//分类id
            //    根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            
            return dishDto;
        }).collect(Collectors.toList());
        
        dishDtoPage.setRecords(list);
        
        return R.success(dishDtoPage);
    }
    
    /**
     * 根据id查询菜品信息和 对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWitFlavor(id);
        
        return R.success(dishDto);
    }
    
    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        
        return R.success("修改菜品成功");
    }
    
    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //查询条件
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //查询状态为1的菜品，1为起售
        wrapper.eq(Dish::getStatus,1);
        //排序条件
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //查询条件
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //查询状态为1的菜品，1为起售
        wrapper.eq(Dish::getStatus, 1);
        //排序条件
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        
        List<Dish> list = dishService.list(wrapper);
        
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            
            Long categoryId = item.getCategoryId();
            
            Category category = categoryService.getById(categoryId);
            
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<DishFlavor>();
            queryWrapper.eq(DishFlavor::getDishId, dishId);
            
            
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
            
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
    
    
}
