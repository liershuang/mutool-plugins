package com.mutool.mock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/25 12:20<br>
 */
@Controller
@RequestMapping("common")
public class CommonController {

    /**
     * 退出系统
     */
    @ResponseBody
    @RequestMapping("exitSystem")
    public void exitSystem(){
        System.exit(0);
    }


}
