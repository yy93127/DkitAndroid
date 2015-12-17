package cn.runhe.dkitandroid.utils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by runhe on 2015/12/17.
 */
public class HttpUtil {

    public static final String REQUEST_ERROR = "ERROR";

    private OkHttpClient client;

    public HttpUtil() {
        if (client == null) {
            client = new OkHttpClient();
        }
    }

    public String requestServer(String url,RequestBody body) throws IOException {
        String result = "";
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        } else {
            result = REQUEST_ERROR;
        }
        return result;
    }
}
