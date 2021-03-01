package com.mutool.mock.test;

import javax.annotation.PostConstruct;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/30 14:41<br>
 */
//@HSFProvider(serviceInterface = HelloService.class)
public class HelloServiceImpl implements HelloService {

    @PostConstruct
    public void loadImpl() {
        System.out.println("load HelloServiceImpl");
    }


    @Override
    public String getName(String name) {
        System.out.println("get name");
        return "this is name";
    }

    @Override
    public Student getStudentByName(String name) {
        return null;
    }


}
