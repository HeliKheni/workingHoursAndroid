package com.hk.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobDetailsAdapter extends RecyclerView.Adapter<JobDetailsAdapter.ViewHolder> {

    private Context context;
    private List<JobDetails> jobDetailsList;

    public JobDetailsAdapter(Context context, List<JobDetails> jobDetailsList) {
        this.context = context;
        this.jobDetailsList = jobDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobDetails jobDetails = jobDetailsList.get(position);
        //holder.tvJob.setText("Job: " + jobDetails.getJob());
        holder.tvJob.setText("Job: " + jobDetails.getJob() + (jobDetails.isDefaultTask() ? " (default)" : ""));

        // Set the text for hourly rate with the dollar sign
        holder.tvHourlyRate.setText("Rate: " + jobDetails.getHourlyRate() + "$");
        // Set item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click here
                Intent intent = new Intent(context, editJob.class);
                intent.putExtra("jobId", jobDetails.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return jobDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJob;
        TextView tvHourlyRate;
        LinearLayout jobTitleRateLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJob = itemView.findViewById(R.id.tvJob);
            tvHourlyRate = itemView.findViewById(R.id.tvHourlyRate);
            jobTitleRateLayout = itemView.findViewById(R.id.jobTitleRateLayout); // Initialize the variable

        }
    }
}
