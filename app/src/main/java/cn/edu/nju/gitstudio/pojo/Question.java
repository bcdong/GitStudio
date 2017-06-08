package cn.edu.nju.gitstudio.pojo;

/**
 * Created by mrzero on 17-6-8.
 * 示例：
 {
 "id": 1,
 "title": "题目1",
 "description": "题目1",
 "difficulty": "3",
 "gitUrl": "http://115.29.184.56:10080/Mooc-CSE-I-2017/Exercise013-IsPalindrome.git",
 "type": "exam",
 "creator": {
 "id": 1,
 "username": "liuqin",
 "name": "刘钦",
 "type": "teacher",
 "avatar": null,
 "gender": "male",
 "email": "lq@nju.edu.cn",
 "schoolId": 1
 },
 "duration": 0,
 "link": -1,
 "knowledgeVos": null
 }
 */
public class Question {
    private int id;
    private String title;
    private String description;
    private String difficulty;
    private String gitUrl;
    private String type;
    private User creator;
    private int duration;
    private int link;
    private String knowledgeVos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public String getKnowledgeVos() {
        return knowledgeVos;
    }

    public void setKnowledgeVos(String knowledgeVos) {
        this.knowledgeVos = knowledgeVos;
    }
}
