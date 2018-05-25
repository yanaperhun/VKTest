package com.vktest.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vktest.app.R;

import java.util.List;

/**
 * Created by seishu on 25.05.18.
 */

public class SmsListAdapter extends RecyclerView.Adapter<SmsListAdapter.VH> {
    private List<String> messages;

    public SmsListAdapter(List<String> messages) {
        this.messages = messages;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms,
                parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ((TextView) holder.itemView).setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);

        }
    }
}
