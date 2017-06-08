package cn.edu.nju.gitstudio.pojo;

/**
 * Created by mrzero on 17-6-8.
 * 示例：
 {
 "id": 64,
 "username": "nanguangtailang",
 "name": "南光太郎",
 "type": "student",
 "avatar": null,
 "gender": "male",
 "email": "ngtl@126.com",
 "schoolId": 1,
 "gitId": 1,
 "number": null,
 "groupId": 0,
 "gitUsername": null
 }
 */

public class User {
    private int id;
    private String username;
    private String name;
    private String type;
    private String avatar;
    private String gender;
    private String email;
    private int schoolId;

    //下面四个字段只有学生有，教师的没有意义
    private int gitId;
    private String number;
    private int groupId;
    private String gitUsername;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getGitId() {
        return gitId;
    }

    public void setGitId(int gitId) {
        this.gitId = gitId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }
}
