package cn.runhe.dkitandroid.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.runhe.dkitandroid.R;
import cn.runhe.dkitandroid.domain.BugListInfo;
import cn.runhe.dkitandroid.domain.LoginUserInfo;
import cn.runhe.dkitandroid.domain.UserListInfo;
import cn.runhe.dkitandroid.utils.Constant;
import cn.runhe.dkitandroid.utils.HttpUtil;
import cn.runhe.dkitandroid.utils.LogUtil;
import cn.runhe.dkitandroid.utils.SPUtil;

public class BugListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TextView tv_status;
    private TextView tv_deal;
    private TextView tv_post;
    private HttpUtil httpUtil;
    private Gson gson;
    private GetDataTask mTask;
    private MyAdapter mAdapter;
    private View loading;
    private Map<String, String> userMap;
    private List<String> nickNameList;
    private PopupWindow window;
    private String project_uuid;
    private List<String> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "暂时没啥用", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        init();

        loadData();
    }

    private void init() {
        loading = findViewById(R.id.loading_dialog);

        View post = findViewById(R.id.rl_bug_post);
        tv_post = (TextView) findViewById(R.id.tv_post);
        post.setOnClickListener(this);
        View deal = findViewById(R.id.rl_bug_deal);
        tv_deal = (TextView) findViewById(R.id.tv_deal);
        deal.setOnClickListener(this);
        View status = findViewById(R.id.rl_bug_status);
        tv_status = (TextView) findViewById(R.id.tv_status);
        status.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_bug);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        project_uuid = getIntent().getStringExtra("project_uuid");

        httpUtil = new HttpUtil();
        gson = new Gson();

    }

    private void loadData() {
        String userlistStr = SPUtil.getString(BugListActivity.this, Constant.SP_USER_CLIENT);
        String userloginStr = SPUtil.getString(BugListActivity.this, Constant.SP_NAME_LOGIN);
        UserListInfo userListInfo = gson.fromJson(userlistStr, UserListInfo.class);
        LoginUserInfo loginUserInfo = gson.fromJson(userloginStr, LoginUserInfo.class);
        List<UserListInfo.RowsEntity> userList = userListInfo.rows;
        statusList = new ArrayList<>();
        statusList.add("全部");
        statusList.add("已解决");
        statusList.add("未解决");
        statusList.add("已关闭");
        nickNameList = new ArrayList<>();
        nickNameList.add(0,"全部");
        userMap = new HashMap<>();
        //        userMap.put("全部","");
        for (int i = 0; i < userList.size(); i++) {
            userMap.put(userList.get(i).nickname, userList.get(i).uuid);
            nickNameList.add(userList.get(i).nickname);
        }

        RequestBody body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999")
                .add("project_uuid", project_uuid).add("bug_user_uuid", loginUserInfo.uuid).build();
        mTask = new GetDataTask(Constant.QUERY_BUG, body);
        mTask.execute((Void[]) null);
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
            LogUtil.i(BugListActivity.this, "数据列表返回：" + result);
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
        if (!result.equals(HttpUtil.REQUEST_ERROR)) {
            BugListInfo bugListInfo = gson.fromJson(result, BugListInfo.class);
            List<BugListInfo.RowsEntity> rows = bugListInfo.rows;
            if (rows.size() > 0) {
                if (mAdapter == null) {
                    mAdapter = new MyAdapter();
                    mAdapter.setList(rows);
                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void setOnItemClick(View view, int position) {

                        }
                    });
                    recyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(BugListActivity.this, "当前条件下没有记录", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<BugListInfo.RowsEntity> list;
        private OnItemClickListener mOnitemClicklister;

        public void setList(List<BugListInfo.RowsEntity> list) {
            this.list = list;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnitemClicklister = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View itemView = View.inflate(BugListActivity.this, R.layout.item_bug_recyclerview, null);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.title.setText(list.get(position).bug_title);
            String str = list.get(position).bug_status;
            if ("1".equals(str)) {
                holder.status.setText("未解决");
            } else if ("2".equals(str)) {
                holder.status.setText("已解决");
            } else {
                holder.status.setText("已关闭");
            }
            holder.createTime.setText(list.get(position).create_time);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnitemClicklister.setOnItemClick(v, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public interface OnItemClickListener {
        void setOnItemClick(View view, int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView status;
        TextView createTime;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_bug_title);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            createTime = (TextView) itemView.findViewById(R.id.tv_createtime);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bug_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_bug_post:
                showPopupWindow(BugListActivity.this, findViewById(v.getId()), nickNameList);
                break;
            case R.id.rl_bug_deal:
                showPopupWindow(BugListActivity.this, findViewById(v.getId()), nickNameList);
                break;
            case R.id.rl_bug_status:
                showPopupWindow(BugListActivity.this, findViewById(v.getId()), statusList);
                break;
            default:
                break;
        }
    }

    private void showPopupWindow(Context cx, View view, List<String> list) {
        window = new PopupWindow(cx);
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list_view, null);
        ListView pop_listview = (ListView) contentView.findViewById(R.id.pop_listview);
        pop_listview.setAdapter(new MyPopAdapter(list,view));
        window.setContentView(contentView);
        window.setWidth(view.getWidth());
        if (list.size()>4) {
            window.setHeight(580);
        } else {
            window.setHeight(AbsListView.LayoutParams.WRAP_CONTENT);
        }
        window.setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
        // 设置PopupWindow外部区域是否可触摸
        window.setFocusable(true); //设置PopupWindow可获得焦点
        window.setTouchable(true); //设置PopupWindow可触摸
        window.setOutsideTouchable(true); //设置非PopupWindow区域可触摸
        window.update();
        window.showAsDropDown(view, 0, 0);
    }

    private class MyPopAdapter extends BaseAdapter {

        private List<String> list;
        private View mView;
        private RequestBody body;

        public MyPopAdapter(List<String> list,View view) {
            this.list = list;
            this.mView = view;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            PopViewHolder holder;
            if (convertView != null) {
                view = convertView;
                holder = (PopViewHolder) view.getTag();
            } else {
                view = View.inflate(BugListActivity.this, R.layout.item_pop_listview, null);
                holder = new PopViewHolder();
                holder.pop = (TextView) view.findViewById(R.id.tv_pop);
                view.setTag(holder);
            }
            holder.pop.setText(list.get(position));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (mView.getId()) {
                        case R.id.rl_bug_post:
                            tv_post.setText(list.get(position));
                            String create_user_uuid = userMap.get(list.get(position));
                            body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999")
                                    .add("project_uuid", project_uuid).add("bug_create_user_uuid",create_user_uuid).build();

                            break;
                        case R.id.rl_bug_deal:
                            tv_deal.setText(list.get(position));
                            String user_uuid = userMap.get(list.get(position));
                            body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999")
                                    .add("project_uuid", project_uuid).add("bug_create_user_uuid",user_uuid).build();
                            break;
                        case R.id.rl_bug_status:
                            String status_id;
                            tv_status.setText(list.get(position));
                            String status = (String) tv_status.getText();
                            if ("全部".equals(status)) {
                                status_id = "";
                            } else if ("已解决".equals(status)) {
                                status_id = "2";
                            } else if ("未解决".equals(status)) {
                                status_id = "1";
                            } else if ("已关闭".equals(status)){
                                status_id = "3";
                            }
                            body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999")
                                    .add("project_uuid", project_uuid).add("bug_status",status).build();
                            break;
                        default:
                            break;
                    }
                    mTask = new GetDataTask(Constant.QUERY_BUG, body);
                    mTask.execute((Void[]) null);
                    window.dismiss();
                }
            });
            return view;
        }
    }

    private class PopViewHolder {
        TextView pop;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (window!=null) {
            window.dismiss();
            window = null;
        }
    }
}
