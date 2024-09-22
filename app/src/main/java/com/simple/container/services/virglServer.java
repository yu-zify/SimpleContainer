package com.simple.container.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class virglServer extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cmd;
        if (intent != null) {
            cmd = intent.getStringExtra("key1");
            System.out.println(cmd);
        } else {
            cmd = "";
            System.out.println("234567");
        }

        new Thread(() -> {
            System.out.println(cmd);
            System.out.println("09876543456789");
            ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
            // ProcessBuilder pb2 = new ProcessBuilder("/bin/sh","-c","ls /sdcard > /data/user/0/com.simple.container/files/out");
            Process ps;
            try {
                ps = pb.start();
                System.out.println("virgl start");
                String privateDir = getFilesDir().getAbsolutePath();
                System.out.println(privateDir);
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

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}