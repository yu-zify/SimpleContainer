package com.simple.container.ui.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simple.container.R;
import com.simple.container.databinding.FragmentNotificationsBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private ExtraAdapter extraAdapter;
    private List<ExtraItem> extraItemList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView=binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        extraItemList = new ArrayList<ExtraItem>();
        extraItemList.add(new ExtraItem("turnip and freedreno 驱动程序","安装","移除",true,true));
        extraItemList.add(new ExtraItem("box64 and wine wow64","安装","移除",false,false));
        extraAdapter=new ExtraAdapter(getContext(),extraItemList,new ExtraAdapter.OnItemButtonClickListener(){
            @Override
            public void onButtonClick(int num,int witch){
                String msg="null";
                if(witch==0){
                    msg="安装中。。。";
                }else if(witch==1){
                    msg="移除中。。。";
                }
               //System.out.println("发现文件");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(getLayoutInflater().inflate(R.layout.get_rootfs, null));
                builder.setCancelable(false); //禁用取消功能
                builder.setMessage(msg);
                AlertDialog dialog = builder.create();
                dialog.show();

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(()->{

                    if(num==0&&witch==0){
                        System.out.println("xxxxx00");
                        String fileUrl = "https://gitee.com/yuzify/simple_rootfs/releases/download/rootfs/extra.tar.gz";
                        String savePath = getActivity().getFilesDir().getAbsolutePath()+"/extra";
                        String fileName="extra.tar.gz";
                        downloadAndInstall(fileUrl,savePath,fileName,dialog);
                    }else if(num==0&&witch==1){
                        System.out.println("xxxxx01");

                    }else if(num==1&&witch==0){
                        System.out.println("xxxxx10");
                    }else if(num==1&&witch==1){
                        System.out.println("xxxxx11");
                    }
                });
                executor.shutdown();


            }
        });
        recyclerView.setAdapter(extraAdapter);



        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void installExtra(AlertDialog dialog){
        Activity activity = requireActivity();
        String privateDir = activity.getFilesDir().getAbsolutePath();
        String cmd = privateDir + "/install.sh 3";
        //Button createButton=binding.create;
        File container_test = new File(privateDir+"/test");

        try {
            System.out.println("解压");
            ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
            Process process = pb.start();
            int exitCode = process.waitFor(); // 等待进程执行完毕
            getActivity().runOnUiThread(() -> {
                dialog.dismiss(); // 进程执行完毕后关闭对话框
                if(container_test.exists() && container_test.isDirectory()){
                   // createBtn = false;
                   // createButton.setEnabled(createBtn);
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



    public void downloadAndInstall(String fileUrl, String savePath,String fileName, AlertDialog dialog){
        //String fileUrl = "https://example.com/file.txt";
        //String savePath = "/sdcard";
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //System.out.println("下载1");
            int fileSize = connection.getContentLength();
            //System.out.println(fileSize);
            System.out.println("下载2");
            System.out.println(fileName);
            System.out.println(fileUrl);
            System.out.println(savePath);
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(new File(savePath, fileName+".tmp"));
            File file=new File(savePath+"/"+fileName+".tmp");
            File newFile=new File(savePath+"/"+fileName);
            byte[] buffer = new byte[4096];
            int bytesRead;
           // int totalBytesRead = 0;
            System.out.println("下载3");

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
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
            installExtra(dialog);

        }catch (Exception ignored){}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}