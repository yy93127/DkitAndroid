package cn.runhe.dkitandroid.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.runhe.dkitandroid.R;
import cn.runhe.dkitandroid.domain.LoginUserInfo;
import cn.runhe.dkitandroid.domain.UserListInfo;
import cn.runhe.dkitandroid.utils.Constant;
import cn.runhe.dkitandroid.utils.HttpUtil;
import cn.runhe.dkitandroid.utils.LogUtil;
import cn.runhe.dkitandroid.utils.SPUtil;

public class TalkActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private View loading;
    private HttpUtil httpUtil;
    private GetDataTask mTask;
    private MyAdapter mAdapter;
    private Gson gson;
    private List<UserListInfo.RowsEntity> userList;
    private String userInfo;

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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(manager);
        userList = new ArrayList<>();
        userInfo = SPUtil.getString(this, Constant.SP_NAME_LOGIN);
        loadData();
    }

    private void loadData() {
        gson = new Gson();
        httpUtil = new HttpUtil();
        RequestBody body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999").build();
        mTask = new GetDataTask(Constant.QUERY_CLIENT, body);
        mTask.execute((Void) null);
    }

    private void processData(String result) {
        if (!result.equals(HttpUtil.REQUEST_ERROR)) {
            LogUtil.i(TalkActivity.this, "数据列表返回：" + result);
            UserListInfo userListInfo = gson.fromJson(result, UserListInfo.class);
            LoginUserInfo userInfo = gson.fromJson(this.userInfo, LoginUserInfo.class);
            if (mAdapter == null) {
                for(int i= 0;i<userListInfo.rows.size();i++) {
                    if (!(userListInfo.rows.get(i).nickname.equals(userInfo.nickname))) {
                        userList.add(userListInfo.rows.get(i));
                    }
                }
                LogUtil.i(TalkActivity.this, "MyAdapter()");
                mAdapter = new MyAdapter();
                mAdapter.setList(userList);
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = View.inflate(TalkActivity.this, R.layout.item_talk_recyclerview, null);
            LogUtil.i(TalkActivity.this, "onCreateViewHolder()");
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.nickname.setText(list.get(position).nickname);
            String str;
            String sex = list.get(position).sex;
            if ("0".equals(sex)) {
                str = "男";
            } else {
                str = "女";
            }
            holder.sex.setText(str);
            holder.address.setText(list.get(position).address);
            holder.mobile.setText(list.get(position).mobile);
            holder.introduce.setText(list.get(position).introduce);
            LogUtil.i(TalkActivity.this, "onBindViewHolder()");
        }

        @Override
        public int getItemCount() {
            LogUtil.i(TalkActivity.this, "getItemCount()");
            return list.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView nickname;
        TextView sex;
        TextView address;
        TextView mobile;
        TextView introduce;

        public ViewHolder(View itemView) {
            super(itemView);
            LogUtil.i(TalkActivity.this, "ViewHolder()");
            nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            sex = (TextView) itemView.findViewById(R.id.tv_sex);
            address = (TextView) itemView.findViewById(R.id.tv_address);
            mobile = (TextView) itemView.findViewById(R.id.tv_mobile);
            introduce = (TextView) itemView.findViewById(R.id.tv_introduce);
        }
    }

}
