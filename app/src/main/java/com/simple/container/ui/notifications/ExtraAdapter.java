package com.simple.container.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.simple.container.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ExtraAdapter extends RecyclerView.Adapter<ExtraAdapter.ViewHolder> {
    private List<ExtraItem> extraItemList;
    private Context context;
    private OnItemButtonClickListener listener;

    public interface OnItemButtonClickListener{
        void onButtonClick(int num,int witch);
    }

    public ExtraAdapter(Context context, List<ExtraItem> extraItemList, OnItemButtonClickListener listener){
        this.context=context;
        this.extraItemList=extraItemList;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.extra_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExtraItem extraItem=extraItemList.get(position);
        holder.textView.setText(extraItem.getText());
        holder.button1.setText(extraItem.getInsBtn());
        holder.button2.setText(extraItem.getRmBtn());
        holder.button1.setEnabled(extraItem.isBtn1());
        holder.button2.setEnabled(extraItem.isBtn2());

        holder.button1.setOnClickListener(view -> {
            listener.onButtonClick(position,0);
        });

        holder.button2.setOnClickListener(view -> {
            listener.onButtonClick(position,1);
        });
    }


    @Override
    public int getItemCount() {
        return extraItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        Button button1;
        Button button2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.extraText);
            button1=itemView.findViewById(R.id.insBtn);
            button2=itemView.findViewById(R.id.rmBtn);
        }
    }
}
