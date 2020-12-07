package fr.enssat.babelblock.jourdren_duchene.services.pipeline

import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


interface ItemMoveAdapter {
    var itemsChain: ToolChain

    fun onRowMoved(from: Int, to: Int)
    fun onRowDeleted(target: Int)
    fun onRowSelected(viewHolder: RecyclerView.ViewHolder)
    fun onRowReleased(viewHolder: RecyclerView.ViewHolder)
    fun onRowRestore(position: Int, item: Tool)
}

object ToolChainMoveSwipeHelper {
    fun create(adapter: ItemMoveAdapter) = ItemTouchHelper(ItemMoveCallback(adapter))
}


private class ItemMoveCallback(private val adapter: ItemMoveAdapter): ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true
    override fun isItemViewSwipeEnabled() = true // enable swipe


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int = makeMovementFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position: Int = viewHolder.adapterPosition
        val item: Tool = adapter.itemsChain[position]

        // delete viewHolder action call
        adapter.onRowDeleted(viewHolder.adapterPosition)

        // undo deletion action
        val snackbar: Snackbar = Snackbar.make(viewHolder.itemView, item.title + " was removed from the pipeline.", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            adapter.onRowRestore(position, item)
        }

        // set 'undo' button in red
        snackbar.setActionTextColor(Color.RED)
        snackbar.show()
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