package com.example.myapplication.adapters;



import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entity.Msg;

import java.util.List;


public  class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg>mMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(View view)
        {
            super(view);
            leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout=(LinearLayout)view.findViewById(R.id.right_layout);
            leftMsg=(TextView)view.findViewById(R.id.left_msg);
            rightMsg=(TextView)view.findViewById(R.id.right_msg);
        }
    }
    public  MsgAdapter(List<Msg>msgList)
    {
        mMsgList=msgList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position)
    {
        Msg msg=mMsgList.get(position);
        if(msg.getType()==Msg.TYPE_RECEIVED)
        {
            //如果是收到的消息则显示左边的布局,将右边的布局隐藏掉
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }
        else if(msg.getType()==Msg.TYPE_SENT)
        {
            //如果是收到的消息则显示右边的布局，将左边的布局隐藏掉
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }
    @Override
    public int getItemCount()
    {
        return mMsgList.size();
    }
}