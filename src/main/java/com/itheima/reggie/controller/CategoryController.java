package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }
    
    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<IPage> page(int page, int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }
    
    /**
     * 根据id删除分类
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        //categoryService.removeById(ids);
        
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }
    
    /**
     * 根据id修改分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
    
    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //添加条件
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件 Asc升序  Desc降序
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        
        return R.success(list);
        
    }
    
}
