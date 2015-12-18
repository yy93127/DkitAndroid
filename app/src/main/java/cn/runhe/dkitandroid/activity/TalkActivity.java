package cn.runhe.dkitandroid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import cn.runhe.dkitandroid.domain.UserListInfo;
import cn.runhe.dkitandroid.utils.Constant;
import cn.runhe.dkitandroid.utils.HttpUtil;
import cn.runhe.dkitandroid.utils.LogUtil;

public class TalkActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private View loading;
    private HttpUtil httpUtil;
    private GetDataTask mTask;
    private MyAdapter mAdapter;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loading = findViewById(R.id.loading_dialog);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview_talk);

        loadData();
    }

    private void loadData() {
        gson = new Gson();
        httpUtil = new HttpUtil();
        RequestBody body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999").build();
        mTask = new GetDataTask(Constant.QUERY_CLIENT,body);
        mTask.execute((Void)null);
    }

    private void processData(String result) {
        if (!result.equals(HttpUtil.REQUEST_ERROR)) {
            LogUtil.i(TalkActivity.this, "数据列表返回：" + result);
            UserListInfo userListInfo = gson.fromJson(result, UserListInfo.class);
            if (mAdapter == null) {
                mAdapter = new MyAdapter();
                mAdapter.setList(userListInfo.rows);
                recyclerview.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        private String url;
        private RequestBody body;

        public GetDataTask(String url, RequestBody body) {
            this.url = url;
            this.body = body;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = httpUtil.requestServer(url, body);
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
            processData(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loading.setVisibility(View.GONE);
            mTask = null;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<UserListInfo.RowsEntity> list;

        public void setList(List<UserListInfo.RowsEntity> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View itemView = View.inflate(TalkActivity.this, R.layout.item_talk_recyclerview, null);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.nickname.setText(list.get(position).nickname);
            String str = "";
            String sex = list.get(position).sex;
            if ("0".equals(sex)) {
                str = "男";
            } else {
                str = "女";
            }
            holder.sex.setText(str);
            holder.mobile.setText(list.get(position).mobile);
            holder.tips.setText(list.get(position).tip);
        }

        @Override
        public int getItemCount() {
            LogUtil.i(TalkActivity.this,list.size()+"");
            return list.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView nickname;
        TextView sex;
        TextView mobile;
        TextView tips;

        public ViewHolder(View itemView) {
            super(itemView);
            nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            sex = (TextView) itemView.findViewById(R.id.tv_sex);
            mobile = (TextView) itemView.findViewById(R.id.tv_mobile);
            tips = (TextView) itemView.findViewById(R.id.tv_tips);
        }
    }

}
