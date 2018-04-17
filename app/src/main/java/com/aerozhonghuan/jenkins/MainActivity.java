package com.aerozhonghuan.jenkins;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aerozhonghuan.demo.Demo1Activity;
import com.aerozhonghuan.java.thread.MyCallable;
import com.aerozhonghuan.java.thread.MyFutureTask;
import com.aerozhonghuan.java.viewutils.ViewInjectUtils;
import com.aerozhonghuan.mytools.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 1、注意：Toast的创建需要依赖Handler，存在handler的话，子线程也可以弹出toast
 * 2、注意：intent传递对象时，是将对象拷贝了一份进行传递
 * 3、注意：quitSafely和quit的区别
 * 4、注意：重写equal()时为什么也得重写hashCode()
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjectUtils.init(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Demo1Activity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + savedInstanceState.getString("data"));
        }
        test1();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.logd(TAG, LogUtils.getThreadName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.logd(TAG, LogUtils.getThreadName());
        outState.putString("data", "test");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.logd(TAG, LogUtils.getThreadName());
    }

    private void test1() {
//        MyThreadPool pool = MyThreadPool.getInstance();
//        MyTask task = new MyTask();
//        // 需考虑资源共享问题
//        for (int i = 1; i < 70; i++) {
//            pool.execute(task);
//        }

        MyCallable callable = new MyCallable();
        MyFutureTask task = new MyFutureTask(callable);
        ExecutorService es = Executors.newFixedThreadPool(3);
        Log.d(TAG, "任务被开始于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        es.execute(task);

        SystemClock.sleep(2000);
        task.cancel(true);

        try {
            String result = task.get();
            Log.d(TAG, "任务结束于：" + Thread.currentThread().getName() + "  result=" + result);
        } catch (CancellationException ex) {
            Log.d(TAG, "任务被取消于：" + Thread.currentThread().getName());
        } catch (InterruptedException ex) {
            Log.d(TAG, "当前线程被中断：" + Thread.currentThread().getName());
        } catch (ExecutionException ex) {
        }

    }
}
