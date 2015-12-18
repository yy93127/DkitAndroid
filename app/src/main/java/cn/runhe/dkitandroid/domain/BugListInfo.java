package cn.runhe.dkitandroid.domain;

import java.util.List;

/**
 * Created by runhe on 2015/12/18.
 */
public class BugListInfo {

    public int total;
    public int pageSize;
    public int pageNum;

    public List<RowsEntity> rows;

    public static class RowsEntity {
        public String uuid;
        public String project_uuid;
        public String bug_title;
        public String create_time;
        public String bug_status;
        public String bug_level;
        public String bug_create_user_uuid;
        public String bug_user_uuid;
        public String bug_context;
    }
}
