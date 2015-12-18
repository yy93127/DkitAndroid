package cn.runhe.dkitandroid.domain;

import java.util.List;

/**
 * Created by runhe on 2015/12/18.
 */
public class UserListInfo {

    public int total;
    public int pageSize;
    public int pageNum;

    public List<RowsEntity> rows;

    public static class RowsEntity {
        public String tip;
        public String introduce;
        public String address;
        public String sex;
        public String nickname;
        public String mobile;
        public String uuid;
    }
}
