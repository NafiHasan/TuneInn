package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MoveItemCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract Adapter;

    public MoveItemCallback(ItemTouchHelperContract adapter)
    {
        Adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled()
    {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return false;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        Adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)
        {
            if(viewHolder instanceof RVAdapterH.MyViewHolder)
            {
                RVAdapterH.MyViewHolder myViewHolder = (RVAdapterH.MyViewHolder) viewHolder;
                Adapter.onRowSelected(myViewHolder);
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if(viewHolder instanceof RVAdapterH.MyViewHolder)
        {
            RVAdapterH.MyViewHolder myViewHolder = (RVAdapterH.MyViewHolder) viewHolder;
            Adapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract
    {
        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(RVAdapterH.MyViewHolder myViewHolder);
        void onRowClear(RVAdapterH.MyViewHolder myViewHolder);
    }
}
