package com.occs.ldsoft.occs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.ref.SoftReference;

/**
 * Created by yeliu on 15/7/28.
 */
public class Person {
    private String avatar = "";
    private String realname = "";
    private String nickname = "";
    private String sex = "";
    private String idno = "";
    private String birthday = "";
    private String address = "";
    private String educollege = "";
    private String degree = "";
    private String mobile = "";
    private String email = "";
    private String profession = "";
    private String skill = "";
    private String qq = "";
    private String weixin = "";
    private String tjid = "";
    private String amount = "0";
    private String ocoin = "0";
    private String ocoinCash = "0";
    private String ocoinFree = "0";
    private int typeNumber = 0;
    private String key = "";
    private String password = "";
    private String name = "";
    /// this part is needed by 软企和企业用户
    private String industry = "";
    private String url = "";
    private String orgcode = "";
    private String summary = "";

    private static Person person = null;

    private Person(String avatar, String name, String realname, String nickname, String sex, String idno,
                  String birthday, String address, String educollege, String degree, String mobile,
                  String email, String profession, String skill, String qq, String weixin,
                  String tjid, String amount, String ocoin, String ocoinCash, String ocoinFree,
                  int typeNumber, String key, String password, String industry, String url,
                   String orgcode, String summary) {
        this.avatar = avatar;
        this.name = name;
        this.realname = realname;
        this.nickname = nickname;
        this.sex = sex;
        this.idno = idno;
        this.birthday = birthday;
        this.address = address;
        this.educollege = educollege;
        this.degree = degree;
        this.mobile = mobile;
        this.email = email;
        this.profession = profession;
        this.skill = skill;
        this.qq = qq;
        this.weixin = weixin;
        this.tjid = tjid;
        this.amount = amount;
        this.ocoin = ocoin;
        this.ocoinCash = ocoinCash;
        this.ocoinFree = ocoinFree;
        this.typeNumber = typeNumber;
        this.key = key;
        this.password = password;
        this.industry = industry;
        this.url = url;
        this.orgcode = orgcode;
        this.summary = summary;
    }

    public static Person getInstance(String avatar, String name, String realname, String nickname, String sex, String idno,
                                     String birthday, String address, String educollege, String degree, String mobile,
                                     String email, String profession, String skill, String qq, String weixin,
                                     String tjid, String amount, String ocoin, String ocoinCash, String ocoinFree,
                                     int typeNumber, String key, String password, String industry, String url,
                                     String orgcode, String summary){
        if (person == null) {
           person = new Person(avatar, name, realname, nickname, sex, idno,birthday, address,
                   educollege, degree, mobile, email, profession, skill, qq, weixin, tjid,
                   amount, ocoin, ocoinCash, ocoinFree, typeNumber, key, password, industry,
                   url, orgcode, summary);
        }else{
            person.setAvatar(avatar);
            person.setName(name);
            person.setRealname(realname);
            person.setNickname(nickname);
            person.setSex(sex);
            person.setIdno(idno);
            person.setBirthday(birthday);
            person.setAddress(address);
            person.setEducollege(educollege);
            person.setDegree(degree);
            person.setMobile(mobile);
            person.setEmail(email);
            person.setProfession(profession);
            person.setSkill(skill);
            person.setQq(qq);
            person.setWeixin(weixin);
            person.setTjid(tjid);
            person.setAmount(amount);
            person.setOcoin(ocoin);
            person.setOcoinCash(ocoinCash);
            person.setOcoinFree(ocoinFree);
            person.setTypeNumber(typeNumber);
            person.setKey(key);
            person.setPassword(password);
            person.setIndustry(industry);
            person.setUrl(url);
            person.setOrgcode(orgcode);
            person.setSummary(summary);
        }
        return person;
    }

    public static Person getPerson() {
        if (person == null){
            return Person.getInstance();
        }else{
            return person;
        }
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRealname() {
        return realname;
    }
    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdno() {
        return idno;
    }
    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducollege() {
        return educollege;
    }
    public void setEducollege(String educollege) {
        this.educollege = educollege;
    }

    public String getDegree() {
        return degree;
    }
    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSkill() {
        return skill;
    }
    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getQq() {
        return qq;
    }
    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }
    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getTjid() {
        return tjid;
    }
    public void setTjid(String tjid) {
        this.tjid = tjid;
    }

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOcoin() {
        return ocoin;
    }
    public void setOcoin(String ocoin) {
        this.ocoin = ocoin;
    }

    public String getOcoinCash() {
        return ocoinCash;
    }
    public void setOcoinCash(String ocoinCash) {
        this.ocoinCash = ocoinCash;
    }

    public String getOcoinFree() {
        return ocoinFree;
    }
    public void setOcoinFree(String ocoinFree) {
        this.ocoinFree = ocoinFree;
    }

    public int getTypeNumber() {
        return typeNumber;
    }
    public void setTypeNumber(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIndustry() {
        return industry;
    }
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrgcode() {
        return orgcode;
    }
    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getTypeNameFromInt(int typenum){
        switch (typenum){
            case 1:
                return "个人";
            case 9:
                return "企业";
            case 91:
                return "软企";
            default:
                return null;
        }
    }

    public void setPersonPreference(Context c){
        SharedPreferences sharedPref = c.getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("key", key);
        editor.putInt("typeNumber", typeNumber);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("realname",realname);
        editor.putString("phoneNumber", mobile);
        editor.putString("password", password);
        if (typeNumber != 0){
            editor.putInt("color", getColorFromType(getTypeNameFromInt(typeNumber)));
        } else {
            editor.putInt("color", R.color.btn_orange_normal);
        }
        editor.putString("avatar", avatar);
        editor.putString("nickname", nickname);
        editor.putString("sex", sex);
        editor.putString("idno", idno);
        editor.putString("birthday", birthday);
        editor.putString("address", address);
        editor.putString("educollege", educollege);
        editor.putString("degree", degree);
        editor.putString("profession", profession);
        editor.putString("skill", skill);
        editor.putString("qq",qq);
        editor.putString("weixin",weixin);
        editor.putString("tjid",tjid);
        editor.putString("amount",amount);
        editor.putString("ocoin",ocoin);
        editor.putString("ocoinCash",ocoinCash);
        editor.putString("ocoinFree",ocoinFree);

        editor.commit();
    }

    public static Person getInstance(){
        if (person == null){
            person = new Person("","","","","","","","","","","","","","","","","","","","","",0,"","","","","","");
        }else{
            person.setAvatar("");
//            person.setName("");
            person.setRealname("");
            person.setNickname("");
            person.setSex("");
            person.setIdno("");
            person.setBirthday("");
            person.setAddress("");
            person.setEducollege("");
            person.setDegree("");
            person.setMobile("");
            person.setEmail("");
            person.setProfession("");
            person.setSkill("");
            person.setQq("");
            person.setWeixin("");
            person.setTjid("");
            person.setAmount("");
            person.setOcoin("");
            person.setOcoinCash("");
            person.setOcoinFree("");
            person.setTypeNumber(0);
            person.setKey("");
            person.setPassword("");
            person.setIndustry("");
            person.setUrl("");
            person.setOrgcode("");
            person.setSummary("");
        }
        return person;
    }

    public static Person getPersonLogin(Context c){
        SharedPreferences sharedPref = c.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String nameTxt = sharedPref.getString("name","");
        String passTxt = sharedPref.getString("password","");

        Person p = Person.getInstance();
        p.setName(nameTxt);
        p.setPassword(passTxt);
        return p;
    }

    public int getColorFromType(String memberType) {
        switch (memberType){
            case "个人":
                return R.color.btn_blue_normal;
            case "软企":
                return R.color.btn_cyan_normal;
            case "企业":
                return R.color.btn_orange_normal;
        }
        return 0;
    }

    public static void changeProp(String prop, String value, Context c) {
        if (person != null){
            switch (prop){
                case"昵称":
                    person.setNickname(value);
                    break;
                case"性别":
                    person.setSex(value);
                    break;
                case"生日":
                    person.setBirthday(value);
                    break;
                case"地址":
                    person.setAddress(value);
                    break;
                case"毕业院校":
                    person.setEducollege(value);
                    break;
                case"QQ":
                    person.setQq(value);
                    break;
                case"微信":
                    person.setWeixin(value);
                    break;
            }
        }
        person.setPersonPreference(c);
    }

    public static String personToJsonString(){
        Person p = Person.getPerson();
        String jsonStr = "{";

        jsonStr += "nick_name:\"" + Person.getPerson().getNickname() + "\",";
        jsonStr += "Sex:\"" + Person.getPerson().getSex() + "\",";
        jsonStr += "birthday:\"" + Person.getPerson().getBirthday() + "\",";
        jsonStr += "address:\"" + Person.getPerson().getAddress() + "\",";
        jsonStr += "educollege:\"" + Person.getPerson().getEducollege() + "\",";
        jsonStr += "qq:\"" + Person.getPerson().getQq() + "\",";
        jsonStr += "weixin:\"" + Person.getPerson().getWeixin() + "\"";

        jsonStr += "}";
        return jsonStr;
    }
}