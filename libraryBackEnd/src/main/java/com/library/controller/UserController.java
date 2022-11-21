package com.library.controller;

import com.library.entity.User;
import com.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    public UserMapper userMapper;

    @RequestMapping("getAllUsers")
    @ResponseBody
    public List<User> getAllUsers(){
        return this.userMapper.selectAll();
    }
}
