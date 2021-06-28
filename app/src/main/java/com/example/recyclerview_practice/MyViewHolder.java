package com.example.recyclerview_practice;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mBody;
        public TextView mDate;

        public MyViewHolder(View v) {
            super(v);
            mBody = v.findViewById(R.id.title);
            mDate = v.findViewById(R.id.date);
        }
    }
