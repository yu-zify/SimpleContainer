package com.simple.container;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.simple.container.databinding.ActivityMainBinding;
import com.simple.container.services.pulseServer;
import com.simple.container.services.startProot;
import com.simple.container.services.virglServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    final private int REQUEST_CODE_STORAGE_PERMISSION=1;

    @SuppressLint({"SetWorldWritable", "SetWorldReadable"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        String privateDir = getFilesDir().getAbsolutePath();
        System.out.println(privateDir);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isNotificationPermissionGranted()) {
            requestNotificationPermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("sqcc");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }
        //第一次启动初始化

        File okfile = new File(privateDir+"/firstOk");
        if (okfile.exists() && !okfile.isDirectory()) {
            // 文件存在
            System.out.println("已初始化");
            //createButton.setEnabled(false);
        } else {
            // 文件不存在
            firstSet(privateDir+"/firstOk");
        }


        //clean tmp
        String cleantmp="rm -rf "+privateDir+"/tmp/* "+privateDir+"/test/tmp/.* "+privateDir+"/test/tmp/*";
        System.out.println(cleantmp);
        try {
            RunCmd.runcmd(cleantmp);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("****************************");

        System.out.println("&&&&");
    }

    public void firstSet(String okFilePath){
        try {
            String[] files = {"core.tar","start.sh","busybox","libbusybox.so.1.36.1","install.sh"};
            AssetManager assetManager = getAssets();
            String[] fil = assetManager.list("");
            //System.out.println(fil);
            List<String> fileNames = new ArrayList<>();
            for (String file : fil) {
                fileNames.add(file);
                System.out.println(file);
            }
            // System.out.println(files+"&&&&&&&&&&&&&&&&&&&&&");
            for(String f :files) {
                String path = getFilesDir().getAbsolutePath() + "/" + f;
                File file = new File(path);

                InputStream in = assetManager.open(f);

                FileOutputStream out = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                file.setReadable(true, false);
                file.setWritable(true, false);
                file.setExecutable(true, false);

                in.close();
                out.flush();
                out.close();

                String privateDir = getFilesDir().getAbsolutePath();
                RunCmd.runcmd(privateDir+"/install.sh 1");

                File okfile=new File(okFilePath);
                okfile.createNewFile();
            }
        } catch (IOException e) {
            //Log.e("AssetCopy", "Error copying asset files: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isNotificationPermissionGranted() {
        NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
        return manager.areNotificationsEnabled();
    }

    private void requestNotificationPermission() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE,getPackageName());
        startActivity(intent);
    }

}