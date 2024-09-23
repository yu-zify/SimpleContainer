package com.simple.container.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.simple.container.databinding.FragmentDashboardBinding;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        TextView show=binding.shellShow;
        EditText input=binding.shellEdit;
        Button enter=binding.enter;
        ScrollView scrollView=binding.scroll;

        getActivity().runOnUiThread(()->{
            show.setText("");
        });

        enter.setOnClickListener(view -> {
            new Thread(() -> {
                String inputText=input.getText().toString();
                ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", inputText);
                System.out.println(inputText);
                pb.redirectErrorStream(true);
                try {
                    // 执行命令
                    Process process = pb.start();
                    // 获取合并后的输出流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        String finalLine = line;
                        getActivity().runOnUiThread(()->{
                            show.setText(show.getText()+"\n"+ finalLine);
                            scrollView.smoothScrollTo(0, show.getBottom());
                        });

                    }

                    // 等待命令执行完成
                    int exitCode = process.waitFor();
                    System.out.println("Command exit code: " + exitCode);
                    getActivity().runOnUiThread(() -> show.setText(show.getText()+"\n"+"Command exit code: " + exitCode ));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}