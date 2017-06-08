package cn.edu.nju.gitstudio.pojo;

/**
 * Created by mrzero on 17-6-8.
 * 示例：
 {
 "id": 1,
 "username": "liuqin",
 "name": "刘钦",
 "type": "teacher",
 "avatar": null,
 "gender": "male",
 "email": "lq@nju.edu.cn",
 "schoolId": 1
 }
 */
public class Teacher {
    private int id;
    private String username;
    private String name;
    private String type;
    private String avatar;
    private String gender;
    private String email;
    private int schoolId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
