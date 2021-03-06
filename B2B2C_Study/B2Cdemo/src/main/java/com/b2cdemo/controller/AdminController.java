package com.b2cdemo.controller;

import com.b2cdemo.mapper.AdminMapper;
import com.b2cdemo.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    //查询所有用户
    @GetMapping ("/queryAdminList")
    public List<Admin> queryAdminList(){
        List<Admin> adminList = adminMapper.queryAdminList();
        for (Admin admin:adminList){
            System.out.println(admin);
        }
        return adminList;
    }

    //查询单个用户
    @GetMapping("/seletone/{id}")
    public Admin queryAdmin(@PathVariable("id")Integer id){
        Admin admin = adminMapper.queryAdminById(id);
        System.out.println("ok");
        return admin;
    }

    //添加用户
    @GetMapping("/addAdmin")
    public String addAdmin(){
        System.out.println("ok");
        return "ok";
    }

    //修改用户
    @GetMapping("/updateAdmin/{id}")
    public String updateAdmin(@PathVariable("id")Integer id){
        System.out.println("ok");
        return "ok";
    }

    //删除用户
    @GetMapping("/deleteAdmin/{id}")
    public String deleteAdmin(@PathVariable("id")Integer id){
        System.out.println("ok");
        return "ok";
    }
}

