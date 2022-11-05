package com.example.tuneinn;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapterH  extends RecyclerView.Adapter<RVAdapterH.MyViewHolder> implements MoveItemCallback.ItemTouchHelperContract{

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {

    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    @Override
    public RVAdapterH.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterH.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
