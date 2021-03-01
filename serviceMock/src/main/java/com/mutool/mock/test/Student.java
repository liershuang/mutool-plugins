package com.mutool.mock.test;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/2 16:58<br>
 */
@Data
public class Student implements Serializable {
    private static final long serialVersionUID = 7624053384197581252L;

    private String name;

    private int age;
}
