package com.simple.container.ui.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.simple.container.MainActivity;
import com.simple.container.NovncActivity;
import com.simple.container.R;
import com.simple.container.RunCmd;
import com.simple.container.databinding.FragmentHomeBinding;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private boolean testBtn = true;
    private boolean createBtn=true;
    private boolean startBtn=true;
    private boolean soundBtn=true;
    private boolean virglBtn=true;

   // private boolean novncBtn=true;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Activity activity = requireActivity();
        String privateDir = activity.getFilesDir().getAbsolutePath();
        //System.out.println(filesDir+"____fragment");


// 真正创建容器
        Button createButton=binding.create;
        File container_test = new File(privateDir+"/test");
        if(container_test.exists() && container_test.isDirectory()){
            createBtn=false;
            createButton.setEnabled(createBtn);
        }else {
            createButton.setOnClickListener(view -> {
                String cmd = privateDir + "/install.sh 2";
                //RunCmd.runcmd(cmd);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(getLayoutInflater().inflate(R.layout.progress_dialog, null));
                builder.setCancelable(false); //禁用取消功能
                builder.setMessage("解压中。。。");
                AlertDialog dialog = builder.create();
                dialog.show();

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
                        Process process = pb.start();
                        int exitCode = process.waitFor(); // 等待进程执行完毕
                        getActivity().runOnUiThread(() -> {
                            dialog.dismiss(); // 进程执行完毕后关闭对话框
                            if(container_test.exists() && container_test.isDirectory()){
                                createBtn = false;
                                createButton.setEnabled(createBtn);
                            } else {
                                System.out.println("创建失败");
                            }
                        });
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> {
                            dialog.dismiss(); // 出现异常时关闭对话框
                        });
                    }
                });
                executor.shutdown();


//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... voids) {
//                        try {
//                            ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
//                            Process process = pb.start();
//                            int exitCode = process.waitFor(); // 等待进程执行完毕
//                        } catch (IOException | InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        if(container_test.exists() && container_test.isDirectory()){
//                            createBtn = false;
//                            createButton.setEnabled(createBtn);
//                        } else {
//                            System.out.println("创建失败");
//                        }
//                        dialog.dismiss(); // 进程执行完毕后关闭对话框
//                    }
//                }.execute();

//                ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
//
//                Process ps= null;
//                try {
//                    ps = pb.start();
//                    dialog.dismiss();
//                    if(container_test.exists() && container_test.isDirectory()) {
//                        createBtn = false;
//                        createButton.setEnabled(createBtn);
//                    }else {
//                        System.out.println("创建失败");
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }


            });
        }

        Button startButton=binding.start;
        startButton.setEnabled(startBtn);
        startButton.setOnClickListener(view -> {
            startBtn=false;
            startButton.setEnabled(startBtn);
            String cmd = privateDir+"/start.sh";
            Intent intent = new Intent(requireActivity(), startProot.class);
            intent.putExtra("key1", cmd);
            requireActivity().startService(intent);

        });

        Button virglButton=binding.virgl;
        virglButton.setEnabled(virglBtn);
        virglButton.setOnClickListener((View.OnClickListener) view -> {
            virglBtn=false;
            virglButton.setEnabled(virglBtn);
            String cmd = privateDir+"/virgl/start_virgl";
            Intent intent = new Intent(requireActivity(), virglServer.class);
            intent.putExtra("key1", cmd);
            requireActivity().startService(intent);
            virglButton.setEnabled(false);
        });

        Button soundButton=binding.sound;
        soundButton.setEnabled(soundBtn);
        soundButton.setOnClickListener(view -> {
            soundBtn=false;
            soundButton.setEnabled(soundBtn);
            String cmd =privateDir+"/pulse/start.sh";
            Intent intent = new Intent(requireActivity(), pulseServer.class);
            intent.putExtra("key1", cmd);
            requireActivity().startService(intent);
        });

        Button novncButton=binding.web;
        novncButton.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), NovncActivity.class);
            startActivity(intent);
        });

        Button btn=binding.buttonTest;
        btn.setEnabled(testBtn);
        btn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(getLayoutInflater().inflate(R.layout.progress_dialog, null));
            builder.setCancelable(false); //禁用取消功能
            AlertDialog dialog = builder.create();
            dialog.show();
            new Handler().postDelayed(() -> dialog.dismiss(), 3000);
            testBtn =false;
            btn.setEnabled(testBtn);
            Toast.makeText(requireContext(), "Button clicked!", Toast.LENGTH_SHORT).show();
        });

        return root;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("testButton_enabled", testBtn);
        outState.putBoolean("createButton_enabled", createBtn);
        outState.putBoolean("startButton_enabled", startBtn);
        outState.putBoolean("soundButton_enabled", soundBtn);
        outState.putBoolean("virglButton_enabled", virglBtn);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            testBtn = savedInstanceState.getBoolean("testButton_enabled");
            createBtn = savedInstanceState.getBoolean("createButton_enabled");
            startBtn = savedInstanceState.getBoolean("startButton_enabled");
            soundBtn = savedInstanceState.getBoolean("soundButton_enabled");
            virglBtn = savedInstanceState.getBoolean("virglButton_enabled");

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}