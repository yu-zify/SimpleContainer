package com.simple.container.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.simple.container.NovncActivity;
import com.simple.container.R;
import com.simple.container.databinding.FragmentHomeBinding;
import com.simple.container.services.pulseServer;
import com.simple.container.services.startProot;
import com.simple.container.services.virglServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

// 真正创建容器
        Button createButton=binding.create;
        File container_test = new File(privateDir+"/test");
        if(container_test.exists() && container_test.isDirectory()){
            createBtn=false;
            createButton.setEnabled(createBtn);
        }else {
            createButton.setOnClickListener(view -> {
                String fileUrl = "https://github.com/yu-zify/simple_rootfs/releases/download/rootfs/debian_xfce.tar.gz";
                String savePath = privateDir;

                AlertDialog dialogGet = new AlertDialog.Builder(getActivity())
                        .setTitle("获取rootfs")
                        //.setView(R.layout.progress_dialog))
                        .setMessage("选择获取rootfs方式，如选择本地请把文件命名为debian_xfce.tar.gz放入Download目录，在线下载地址：\n " +
                                "https://github.com/yu-zify/simple_rootfs/releases/download/rootfs/debian_xfce.tar.gz")
                        .setPositiveButton("在线下载", (dialog1, which) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setView(getLayoutInflater().inflate(R.layout.get_rootfs, null));
                            builder.setCancelable(false); //禁用取消功能
                            builder.setMessage("下载中。。。");
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.execute(()->{
                            downloadAndInstall(fileUrl,savePath,dialog);
                            });
                            executor.shutdown();
                        })
                        .setNegativeButton("本地", (dialog12, which) -> {
                            File rootfs_file = new File("/sdcard/Download/debian_xfce.tar.gz");
                            if (rootfs_file.exists() && !rootfs_file.isDirectory()) {
                                System.out.println("发现文件");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setView(getLayoutInflater().inflate(R.layout.get_rootfs, null));
                                builder.setCancelable(false); //禁用取消功能
                                builder.setMessage("解压中。。。");
                                AlertDialog dialog = builder.create();
                                dialog.show();

                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.execute(()->{
                                    installRootfs(dialog);
                                });
                                executor.shutdown();
                            } else {
                                System.out.println("文件不存在");

                            }
                        })
                        .create();
                dialogGet.show();


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
        virglButton.setOnClickListener(view -> {
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

    public void installRootfs(AlertDialog dialog){
        Activity activity = requireActivity();
        String privateDir = activity.getFilesDir().getAbsolutePath();
        String cmd = privateDir + "/install.sh 2";
        Button createButton=binding.create;
        File container_test = new File(privateDir+"/test");
            try {
                System.out.println("解压");
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
    }



    public void downloadAndInstall(String fileUrl, String savePath, AlertDialog dialog){
        //String fileUrl = "https://example.com/file.txt";
        //String savePath = "/sdcard";
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //System.out.println("下载1");
                int fileSize = connection.getContentLength();
                //System.out.println(fileSize);
                //System.out.println("下载2");
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(new File(savePath, "debian_xfce.tar.gz.tmp"));
                File file=new File(savePath+"/debian_xfce.tar.gz.tmp");
                File newFile=new File(savePath+"/debian_xfce.tar.gz");
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    // 计算下载进度
                    int progress = (int) ((file.length() * 100) / fileSize);

                    // 更新UI（可以使用Handler或者runOnUiThread方法更新UI）
                    getActivity().runOnUiThread(() -> {
                        dialog.setMessage("已下载"+ progress +"%");
                    });
                    System.out.println("Download progress: " + progress + "%");
                }

                outputStream.close();
                inputStream.close();
                connection.disconnect();

                newFile.delete();
                Boolean s=file.renameTo(newFile);
                if(s){
                    System.out.println("重命名成功");
                }else {
                    System.out.println("重命名失败");
                }

           //     dialog.dismiss();
                System.out.println("File downloaded successfully.");
                //dialog.setView();
                getActivity().runOnUiThread(() -> {
                    dialog.setMessage("安装中");
                });
                installRootfs(dialog);

            }catch (Exception ignored){}
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