package cn.y.java.minichat.utils;

public class ResourcesUtil {

    public static String getRootPath(){
        return ResourcesUtil.class.getClassLoader().getResource("").getPath();
    }
}
