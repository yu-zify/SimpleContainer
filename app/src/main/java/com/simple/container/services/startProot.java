package com.simple.container.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class startProot extends Service {
    private static final String CHANNEL_ID = "proot";
    private static final int NOTIFICATION_ID = 1;
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        // 创建 Notification 对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("前台服务")
                .setContentText("正在运行");

        // 启动前台服务
        startForeground(NOTIFICATION_ID, builder.build());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在这里编写服务启动时的主要逻辑
        // 比如开启线程执行长时间的任务
        String cmd;
        if (intent != null) {
             cmd = intent.getStringExtra("key1");
            //int value2 = intent.getIntExtra("key2", 0);
            // 使用传递的参数执行服务操作
        } else {
            cmd = "";
        }

        new Thread(() -> {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
            // ProcessBuilder pb2 = new ProcessBuilder("/bin/sh","-c","ls /sdcard > /data/user/0/com.simple.container/files/out");

            Process ps= null;
            try {
                ps = pb.start();
                System.out.println("proot start");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //String outputFile=getFilesDir().getAbsolutePath()+"/out";

            try (InputStream inputStream = ps.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 InputStream errStream = ps.getErrorStream();
                 BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream))
                 //FileWriter writer = new FileWriter(outputFile)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    // writer.write(line);
                    //writer.write("\n");
                }
                String line2;
                while ((line2 = errReader.readLine()) != null) {
                    System.out.println(line2);
                    // writer.write(line);
                    //writer.write("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 停止前台服务
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}