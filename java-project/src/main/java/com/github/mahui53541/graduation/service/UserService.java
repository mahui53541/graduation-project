package com.github.mahui53541.graduation.service;

import com.github.pagehelper.PageHelper;
import com.github.mahui53541.graduation.mapper.UserMapper;
import com.github.mahui53541.graduation.model.User;
import com.github.pagehelper.PageRowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * java类简单作用描述
 *
 * @Description: java类作用描述
 * @Author: MaHui
 * @CreateDate: 2018/4/29 20:03
 * @Version: 1.0
 */
@Service("userService")
public class UserService extends BaseService<UserMapper,User>{

    public List<User> findAllUser(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        //PageHelper.startPage(pageNum, pageSize);
        PageRowBounds rowBounds=new PageRowBounds((pageNum-1)*pageSize,pageSize);
        List<User> users=userMapper.selectByRowBounds(null,rowBounds);
        return users;
    }
}
