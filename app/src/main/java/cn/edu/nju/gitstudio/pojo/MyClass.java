package cn.edu.nju.gitstudio.pojo;

import java.io.Serializable;

/**
 * 此类是“班级”的pojo类，不是java中的class
 */

public class MyClass implements Serializable{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
