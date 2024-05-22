package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(!dataList.get(position).getImageUrl().equals("dont use image"))
        {
            Glide.with(context).load(dataList.get(position).getImageUrl()).into(holder.recImage);
        }
        holder.recTitle.setText(dataList.get(position).getNumClass());
        holder.recRole.setText(dataList.get(position).getRole());
        holder.recLang.setText(dataList.get(position).getTime());
        holder.recName.setText(dataList.get(position).getUserName());
        // Setting the color of the stripe based on urgency
        if (dataList.get(position).getUrgency().equals("High")) {
            holder.colorStripe.setBackgroundColor(context.getResources().getColor(R.color.red)); // Assuming high urgency should be red
        } else if (dataList.get(position).getUrgency().equals("Medium")) {
            holder.colorStripe.setBackgroundColor(Color.parseColor("#FFA500")); // Assuming medium urgency should be orange
        } else if (dataList.get(position).getUrgency().equals("Low")) {
            holder.colorStripe.setBackgroundColor(context.getResources().getColor(R.color.green)); // Assuming low urgency should be green
        }
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("detail", dataList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder
{
    ImageView recImage;
    TextView recTitle, recRole, recLang,recName;
    View colorStripe;
    CardView recCard;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recRole = itemView.findViewById(R.id.recDesc);
        recLang = itemView.findViewById(R.id.recTime);
        recTitle = itemView.findViewById(R.id.recTitle);
        recName = itemView.findViewById(R.id.recPriority);
        colorStripe = itemView.findViewById(R.id.colorStripe);
    }


}

