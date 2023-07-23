package com.hk.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

    private List<WorkItem> workList;

    public WorkAdapter(List<WorkItem> workList) {
        this.workList = workList;
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_work, parent, false);
        return new WorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        WorkItem workItem = workList.get(position);
        holder.tvDescription.setText("Work : " + workItem.getDescription());
        holder.tvJob.setText("Job: " + workItem.getJob());
        holder.tvDay.setText("Day: " + workItem.getDay());
        holder.tvStartTime.setText("Start Time: " + workItem.getStartTime());
        holder.tvEndTime.setText("End Time: " + workItem.getEndTime());
        holder.tvWorkingTime.setText("Working Time: " + workItem.getWorkingTime() + "\n---------------------------------------------\n");
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }

    public class WorkViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvJob, tvDay, tvStartTime, tvEndTime, tvWorkingTime;

        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvJob = itemView.findViewById(R.id.tvJob);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvWorkingTime = itemView.findViewById(R.id.tvWorkingTime);
        }
    }
}

