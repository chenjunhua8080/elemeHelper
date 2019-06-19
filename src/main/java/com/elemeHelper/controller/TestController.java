package com.elemeHelper.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test ok!";
    }

    @GetMapping("/getDate")
    public Map<String,Object> getDate() {
        Map<String,Object> map=new HashMap<>();
        map.put("received",System.currentTimeMillis());
        map.put("msg","成功");
        Map<String,Object> map2=new HashMap<>();
        map2.put("t",System.currentTimeMillis());
        map.put("data",map2);
        return map;

    }

}
