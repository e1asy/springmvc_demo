package com.springmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmvc.pojo.Books;
import com.springmvc.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 解决json返回中文乱码问题
 *  目前可行解：
 *  1. spring-mvc.xml中进行配置
 *  2. 添加produces声明
 *  *. 使用@RestController方式据说可行，但是为啥我这里测试失败了呢？
 */
@RestController // 类中方法只会返回json字符串（直接使用该注解，类方法取消@ResponseBody，中文乱码问题可以解决？？？）
public class JsonController {

    @Autowired
    @Qualifier("BookServiceImpl")
    private BookService bookService;

    //@RequestMapping(value = "/jsonQuery", produces = "application/json;charset=utf-8") // 添加produces也是解决中文乱码的解决方案之一
    @RequestMapping( "/jsonQuery")
    //@ResponseBody
    public String jsonQuery(Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Books> list = bookService.queryAllBook();
        String str = mapper.writeValueAsString(list);
        System.out.println(str);
        return str;
    }
}
