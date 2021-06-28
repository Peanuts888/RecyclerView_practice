package com.example.recyclerview_practice;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Columns> dataArray;

    RecyclerViewAdapter(List<Columns> myDataset) {
        dataArray = myDataset;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CreateMemoActivity.class);
                final int position = holder.getAdapterPosition();
                String isStr = String.valueOf(dataArray.get(position).getId());
                intent.putExtra("id", isStr);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mBody.setText(dataArray.get(position).getBody());
        holder.mDate.setText(dataArray.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }

    @Override
    public long getItemId(int position) {
        return dataArray.get(position).getId();
    }
}