package cn.runhe.dkitandroid.domain;

import java.util.List;

/**
 * Created by runhe on 2015/12/17.
 */
public class ProjectListInfo {

    public int total;
    public int pageSize;
    public int pageNum;

    public List<RowsEntity> rows;

    public static class RowsEntity {
        public String uuid;
        public String create_time;
        public String project_name;
        public String create_user_uuid;
        public String project_context;
        public String project_version;
    }
}
