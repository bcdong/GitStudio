package cn.edu.nju.gitstudio.pojo;

import java.io.Serializable;

/**
 * 练习或考试中每个问题学生的得分情况，包括问题title和4个分数段的人数
 * 4个分数段为0~59, 60~79, 80~89, 90~100
 */

public class QuestionScoreInterval implements Serializable{
    private String title;
    private int[] peopleCount;

    public QuestionScoreInterval() {
    }

    public QuestionScoreInterval(String title, int[] peopleCount) {
        this.title = title;
        this.peopleCount = peopleCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int[] getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int[] peopleCount) {
        this.peopleCount = peopleCount;
    }
}
