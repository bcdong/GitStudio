package cn.edu.nju.gitstudio.pojo;

import java.io.Serializable;

/**
 * 测试用例信息
 */

public class TestCaseResult implements Serializable{
    private int questionId;
    private String questionTitle;
    private boolean compile_succeeded;
    private boolean tested;
    private int score;
    private TestCase[] mTestCases;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public boolean isCompile_succeeded() {
        return compile_succeeded;
    }

    public void setCompile_succeeded(boolean compile_succeeded) {
        this.compile_succeeded = compile_succeeded;
    }

    public boolean isTested() {
        return tested;
    }

    public void setTested(boolean tested) {
        this.tested = tested;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public TestCase[] getTestCases() {
        return mTestCases;
    }

    public void setTestCases(TestCase[] testCases) {
        mTestCases = testCases;
    }
}
