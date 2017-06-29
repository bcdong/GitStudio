package cn.edu.nju.gitstudio.pojo;

import java.io.Serializable;

/**
 * 测试用例名称 ===> 是否通过
 */

public class TestCase implements Serializable{
    private String name;
    private boolean passed;

    public TestCase(String name, boolean passed) {
        this.name = name;
        this.passed = passed;
    }

    public String getName() {
        return name;
    }

    public boolean isPassed() {
        return passed;
    }
}
