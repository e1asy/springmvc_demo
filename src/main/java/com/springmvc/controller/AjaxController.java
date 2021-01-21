package com.springmvc.controller;


import com.springmvc.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class AjaxController {

    @RequestMapping("/ajax")
    public List<User> ajax(){
        List<User> list = new ArrayList<User>();
        list.add(new User("鲁班",3,"男"));
        list.add(new User("亚瑟",4,"男"));
        list.add(new User("孙尚香",5,"女"));
        return list; //由于@RestController注解，将list转成json格式返回
    }

}