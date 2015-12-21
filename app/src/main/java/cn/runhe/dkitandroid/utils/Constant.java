package cn.runhe.dkitandroid.utils;

/**
 * 服务器接口
 */
public class Constant {

    /**
     * 主机地址
     */
    public static final String LOCAL_HOST = "http://192.168.1.99:7002/service/";
    /**
     * 登陆
     */
    public static final String LOGIN = LOCAL_HOST + "Login";
    /**
     * 查询所有用户信息
     */
    public static final String QUERY_CLIENT = LOCAL_HOST + "QueryClient";
    /**
     * 查询所有项目
     */
    public static final String QUERY_BUG_PROJECT = LOCAL_HOST + "QueryBugProject";
    /**
     * 查询所有bug
     */
    public static final String QUERY_BUG = LOCAL_HOST + "QueryBug";
    /**
     * 更新bug状态
     */
    public static final String UPDATE_BUG = LOCAL_HOST + "UpdateBug";
    /**
     * 根据项目查询人员信息
     */
    public static final String QUERY_USERS_BY_PROJECT = LOCAL_HOST + "QueryUsersByProject";
    /**
     * 上传图片IP地址
     */
    public static final String UPLOAD_SERVER_IP = "192.168.1.99";
    /**
     * 上传图片IP地址
     */
    public static final int UPLOAD_SERVER_PORT = 7002;
    /**
     * 首选项文件名
     */
    public static final String SP_NAME_LOGIN = "userlogin.txt";
    public static final String SP_USER_CLIENT = "userclient.txt";
}
