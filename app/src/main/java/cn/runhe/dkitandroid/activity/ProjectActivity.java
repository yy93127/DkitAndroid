package cn.runhe.dkitandroid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.List;

import cn.runhe.dkitandroid.R;
import cn.runhe.dkitandroid.domain.ProjectListInfo;
import cn.runhe.dkitandroid.utils.Constant;
import cn.runhe.dkitandroid.utils.HttpUtil;
import cn.runhe.dkitandroid.utils.LogUtil;

public class ProjectActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private HttpUtil httpUtil;
    private Gson gson;
    private GetProjectListTask mTask;
    private MyAdapter mAdapter;
    private View loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        loading = findViewById(R.id.loading_dialog);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);

        init();

        loadData();
    }

    private void loadData() {
        mTask.execute((Void) null);
    }

    private class GetProjectListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                RequestBody body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999").build();
                result = httpUtil.requestServer(Constant.QUERY_BUG_PROJECT, body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mTask = null;
            loading.setVisibility(View.GONE);
            LogUtil.i(ProjectActivity.this, "项目列表返回：" + result);
            processData(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loading.setVisibility(View.GONE);
            mTask = null;
        }
    }

    private void processData(String result) {
        if (HttpUtil.REQUEST_ERROR != result) {
            ProjectListInfo projectListInfo = gson.fromJson(result, ProjectListInfo.class);
            List<ProjectListInfo.RowsEntity> rows = projectListInfo.rows;
            if (mAdapter == null) {
                mAdapter = new MyAdapter();
                mAdapter.setList(rows);
                recyclerview.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<ProjectListInfo.RowsEntity> list;

        public void setList(List<ProjectListInfo.RowsEntity> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = View.inflate(ProjectActivity.this, R.layout.item_projects_recyclerview, null);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.projectName.setText(list.get(position).project_name);
            holder.version.setText(list.get(position).project_version);
            holder.createTime.setText(list.get(position).create_time);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView projectName;
        TextView version;
        TextView createTime;

        public ViewHolder(View itemView) {
            super(itemView);
            projectName = (TextView) itemView.findViewById(R.id.tv_projectname);
            version = (TextView) itemView.findViewById(R.id.tv_version);
            createTime = (TextView) itemView.findViewById(R.id.tv_createtime);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
//                    startActivity(new Intent(ProjectActivity.this,BugListActivity.class));
                }
            });
        }
    }

    private void init() {
        httpUtil = new HttpUtil();
        gson = new Gson();
        mTask = new GetProjectListTask();
    }

}
