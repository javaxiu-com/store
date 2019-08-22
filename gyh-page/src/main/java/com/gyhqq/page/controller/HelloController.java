package com.gyhqq.page.controller;

import com.gyhqq.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller //不能写@RestController因为它复合注解,包含@ResponseBody,只能返回json,这里我们要返回页面!
public class HelloController {

    @Autowired
    private PageService pageService;

    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name","jack");
        model.addAttribute("age",null);
        return "hello";
    }

    @RequestMapping("/item/{id}.html")
    public String hello(Model model, @PathVariable(name="id") Long spuId){
        Map<String, Object> map = pageService.loadData(spuId);
        model.addAllAttributes(map);
        return "item";
    }
}
