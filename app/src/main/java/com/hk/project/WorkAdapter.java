package com.hk.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {

    private List<WorkItem> workList;
    private boolean[] expandedStates;

    public WorkAdapter(List<WorkItem> workList) {
        this.workList = workList;
        this.expandedStates = new boolean[workList.size()];
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

        // Set job date and total working hours
        holder.tvJobDate.setText( workItem.getDay());
        holder.tvTotalWorkingHours.setText("Hours :" + workItem.getWorkingTime());

        // Show/hide the expandableLayout based on the expanded state
        if (expandedStates[position]) {
            holder.expandableLayout.setVisibility(View.VISIBLE);
            holder.ivArrow.setImageResource(R.drawable.uparrow_icon);
        } else {
            holder.expandableLayout.setVisibility(View.GONE);
            holder.ivArrow.setImageResource(R.drawable.arrow_icon);
        }

        // Set click listener for the arrow icon
        holder.ivArrow.setOnClickListener(v -> {
            expandedStates[position] = !expandedStates[position];
            notifyDataSetChanged();
        });

        // Set other details for the expanded view
        if (expandedStates[position]) {
            holder.tvJob.setText("Job: " + workItem.getJob());
            holder.tvDay.setText("Day: " + workItem.getDay());
            holder.tvStartTime.setText("Start Time: " + workItem.getStartTime());
            holder.tvEndTime.setText("End Time: " + workItem.getEndTime());
            holder.tvWorkingTime.setText("Working Time: " + workItem.getWorkingTime());
        } else {
            // Reset the text to empty when not expanded
            holder.tvJob.setText("");
            holder.tvDay.setText("");
            holder.tvStartTime.setText("");
            holder.tvEndTime.setText("");
            holder.tvWorkingTime.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }

    public class WorkViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobDate, tvJob, tvDay, tvStartTime, tvEndTime, tvWorkingTime, tvTotalWorkingHours;
        ImageView ivArrow;
        LinearLayout expandableLayout;


        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobDate = itemView.findViewById(R.id.tvJobDate);
            tvJob = itemView.findViewById(R.id.tvJob);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvWorkingTime = itemView.findViewById(R.id.tvWorkingTime);
            tvTotalWorkingHours = itemView.findViewById(R.id.tvTotalWorkingHours);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            // Add this line
        }
    }


}
