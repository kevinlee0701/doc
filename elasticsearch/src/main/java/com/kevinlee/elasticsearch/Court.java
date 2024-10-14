package com.kevinlee.elasticsearch;

public enum Court {


    YUN_QU_YUAN("云趣园", "云趣园一区",90,110,"南 北"),
    LONG_HUA_YUAN("龙华园", "龙华园",80,83,"南 北"),
    XIN_LONG_CHENG("新龙城", "新龙城（80-120）",80,120,"南 北"),
    LONG_TENG_YUAN("龙腾苑二区", "龙腾苑（80-120）",80,120,"南 北"),
    LONG_HUA_YUAN70("龙华园", "龙华园（70-80）",70,80,"南 北"),
    JIN_KE_CHENG("金科城", "金科城（130-200）",130,130,"南 北"),
    JIN_KE_CHENG80("金科城", "金科城（80-90）",80,90,"南 北"),
    YU_TAI_JIU_LONG_YUAN("鈺泰九龍苑", "鈺泰九龍苑(100-103)",100,103,"南 北");

    private final String court;

    private final String address;

    Court(String court, String address, int gteArea, int lteArea, String hourseInfo) {
        this.court = court;
        this.address = address;
        this.gteArea = gteArea;
        this.lteArea = lteArea;
        this.hourseInfo = hourseInfo;
    }

    // 持续时间，以秒为单位
    private final int gteArea;
    private final int lteArea;
    private final String hourseInfo;

    public String getCourt() {
        return court;
    }

    public String getAddress() {
        return address;
    }

    public int getGteArea() {
        return gteArea;
    }

    public int getLteArea() {
        return lteArea;
    }

    public String getHourseInfo() {
        return hourseInfo;
    }

    @Override
    public String toString() {
        return "Court{" +
                "court='" + court + '\'' +
                ", address='" + address + '\'' +
                ", gteArea=" + gteArea +
                ", lteArea=" + lteArea +
                ", hourseInfo='" + hourseInfo + '\'' +
                '}';
    }
}
