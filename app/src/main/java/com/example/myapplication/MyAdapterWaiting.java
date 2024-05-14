package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyAdapterWaiting extends RecyclerView.Adapter<MyViewHolderWaiting>{

    private Context context;
    private List<waitingCardAp> dataList;

    public MyAdapterWaiting(Context context, List<waitingCardAp> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolderWaiting onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_card, parent, false);
        return new MyViewHolderWaiting(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolderWaiting holder, int position) {
        waitingCardAp waitingCard = dataList.get(position);
        holder.recRole.setText(waitingCard.getRole());
        holder.recId.setText(waitingCard.getIdUser());
        holder.recTime.setText(waitingCard.getTime());
        holder.recName.setText(waitingCard.getUserName());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    waitingCardAp currentCard = dataList.get(currentPosition);
                    String key = currentCard.getKey();
                    // Remove the item from the database
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Waiting-list").child("640037");
                    databaseReference.child(key).removeValue();
                    // Remove the item from the list and notify the adapter
                    dataList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, dataList.size());
                }
            }
        });
        holder.approvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    waitingCardAp currentCard = dataList.get(currentPosition);
                    String key = currentCard.getKey();
                    String[] keyParts = key.split("/");
                    String role = keyParts[0];
                    String id = keyParts[1];
                    // Reference to the waiting list item
                    DatabaseReference waitingListRef = FirebaseDatabase.getInstance().getReference("Waiting-list").child("640037").child(key);
                    // Reference to the organization list
                    DatabaseReference organizationRef = FirebaseDatabase.getInstance().getReference("organization").child("640037").child(role).child(id);

                    // Read the data from the waiting list and write it to the organization list
                    waitingListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                organizationRef.setValue(dataSnapshot.getValue()).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Remove the item from the waiting list database
                                        waitingListRef.removeValue();
                                        // Remove the item from the list and notify the adapter
                                        dataList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, dataList.size());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors.
                        }
                    });
                }
            }
        });
    }



    public int getItemCount() {
        return dataList.size();
    }

}
class MyViewHolderWaiting extends RecyclerView.ViewHolder
{
    TextView recTime, recRole, recId,recName;
    CardView recCard;
    ImageView deleteButton, approvalButton;

    public MyViewHolderWaiting(@NonNull View itemView) {
        super(itemView);
        recCard = itemView.findViewById(R.id.UserCard);
        recRole = itemView.findViewById(R.id.role);
        recId = itemView.findViewById(R.id.idUser);
        recTime = itemView.findViewById(R.id.recTime);
        recName = itemView.findViewById(R.id.name);
        deleteButton = itemView.findViewById(R.id.delete);
        approvalButton = itemView.findViewById(R.id.approval);

    }


}