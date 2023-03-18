package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * error：在下单时，必须要有一个默认地址，不然一直跳转到增加地址页面
 * 地址管理：
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    
    
    
    /**
     * 新增
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        
        
        addressBookService.save(addressBook);
        return R.success(addressBook);
        
    }
    
    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        
        addressBookService.update(wrapper);
        
        addressBook.setIsDefault(1);
        
        addressBookService.updateById(addressBook);
        
        return R.success(addressBook);
        
    }
    
    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        
        if (addressBook != null) {
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }
    
    /**
     * 查询默认地址
     *
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault, 1);
        
        AddressBook addressBook = addressBookService.getOne(wrapper);
        
        if (null != addressBook) {
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }
    
    /**
     * 查询指定用户的全部地址
     *
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        
        List<AddressBook> list = addressBookService.list(queryWrapper);
        
        return R.success(list);
        
    }
    
    
}
