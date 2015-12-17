package cn.runhe.dkitandroid.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.List;

import cn.runhe.dkitandroid.R;
import cn.runhe.dkitandroid.domain.LoginUserInfo;
import cn.runhe.dkitandroid.domain.ProjectListInfo;
import cn.runhe.dkitandroid.utils.Constant;
import cn.runhe.dkitandroid.utils.HttpUtil;
import cn.runhe.dkitandroid.utils.LogUtil;
import cn.runhe.dkitandroid.utils.SPUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerview;
    public HttpUtil httpUtil;
    public Gson gson;
    private GetDataTask mTask;
    private MyAdapter mAdapter;
    private View loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViewData();
    }

    private void initViewData() {
        loading = findViewById(R.id.loading_dialog);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview_main);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
//        TextView nickname = (TextView) findViewById(R.id.tv_nickname);
//        TextView tip = (TextView) findViewById(R.id.tv_tip);
        httpUtil = new HttpUtil();
        gson = new Gson();
        RequestBody body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999").build();
        mTask = new GetDataTask(Constant.QUERY_BUG_PROJECT,body);
        mTask.execute((Void) null);

        String string = SPUtil.getString(MainActivity.this, Constant.SP_NAME_LOGIN);
        LoginUserInfo loginUserInfo = gson.fromJson(string, LoginUserInfo.class);
//        nickname.setText(loginUserInfo.nickname);
//        tip.setText(loginUserInfo.tip);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        private String url;
        private RequestBody body;

        public GetDataTask(String url,RequestBody body) {
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
            LogUtil.i(MainActivity.this, "数据列表返回：" + result);
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
        if (! result.equals(HttpUtil.REQUEST_ERROR)) {
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
            View itemView = View.inflate(MainActivity.this, R.layout.item_projects_recyclerview, null);
            return new ViewHolder(itemView);
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
//                    int position = getAdapterPosition();
//                    startActivity(new Intent(ProjectActivity.this,BugListActivity.class));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Toast.makeText(MainActivity.this, "设置", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            Toast.makeText(MainActivity.this, "所有项目", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_tome) {
            Toast.makeText(MainActivity.this, "提给我的", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_resolved) {
            Toast.makeText(MainActivity.this, "已解决", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_unresolved) {
            Toast.makeText(MainActivity.this, "未解决", Toast.LENGTH_SHORT).show();    
        } else if (id == R.id.nav_close) {
            Toast.makeText(MainActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
