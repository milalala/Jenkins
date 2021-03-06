package com.aerozhonghuan.demo.net.http.task;

import android.os.AsyncTask;
import android.util.Log;

import com.aerozhonghuan.demo.net.callback.NetCallback;
import com.aerozhonghuan.demo.net.callback.NetData;
import com.aerozhonghuan.demo.net.http.engine.HttpGetRequest;
import com.aerozhonghuan.mytools.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;


/**
 * Get 异步任务
 */
public class MyAsyncGetTask extends AsyncTask<Void, Void, String> {

    private String TAG = "MyAsyncGetTask";
    private HttpGetRequest httpRequest;
    private String url;
    private Map<String, Object> hashMap;
    private NetCallback callback;

    public MyAsyncGetTask(String url, Map<String, Object> hashMap, NetCallback callback) {
        httpRequest = HttpGetRequest.getInstance();
        this.url = url;
        this.hashMap = hashMap;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (!isCancelled()) {
            return httpRequest.doGet(url, hashMap);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.d(TAG, LogUtils.getThreadName() + "response:" + response);
        if (callback != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<NetData>() {
            }.getType();
            NetData data = gson.fromJson(response, type);
            if (data.getCode() == 200) {
                callback.netSuccess(data.getMsg());
            } else {
                callback.netFail(data.getCode(), data.getMsg());
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, LogUtils.getThreadName());
    }
}
