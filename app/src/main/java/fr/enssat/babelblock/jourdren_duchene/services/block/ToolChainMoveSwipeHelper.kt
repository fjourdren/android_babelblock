package fr.enssat.babelblock.jourdren_duchene.services.block

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


interface ItemMoveAdapter {
    fun onRowMoved(from: Int, to: Int)
    fun onRowDeleted(target: Int)
    fun onRowSelected(viewHolder: RecyclerView.ViewHolder)
    fun onRowReleased(viewHolder: RecyclerView.ViewHolder)
}

object ToolChainMoveSwipeHelper {
    fun create(adapter: ItemMoveAdapter) = ItemTouchHelper(ItemMoveCallback(adapter))
}


private class ItemMoveCallback(private val adapter: ItemMoveAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true
    override fun isItemViewSwipeEnabled() = true


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int = makeMovementFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onRowDeleted(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.run { adapter.onRowSelected(this) }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.onRowReleased(viewHolder)
    }
}