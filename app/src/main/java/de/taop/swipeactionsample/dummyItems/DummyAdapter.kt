package de.taop.swipeactionsample.dummyItems

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.taop.swipeaction.actions.SwipeActionAdapter
import de.taop.swipeactionsample.R
import kotlinx.android.synthetic.main.dummy_layout.view.*


class DummyAdapter(var items: ArrayList<DummyItem>, val itemClick: (DummyItem) -> Unit = {})
    : RecyclerView.Adapter<DummyAdapter.DummyViewHolder>(),
        SwipeActionAdapter {

    fun addItem(item: Any) {
        items.add(item as DummyItem)
        notifyItemInserted(items.size - 1)
    }

    override fun addItemAt(position: Int, item: Any) {
        items.add(position, item as DummyItem)
        notifyItemInserted(position)
    }

    override fun removeItemAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemAt(position: Int): DummyItem = items[position]

    override fun containsItem(item: Any): Boolean = items.contains(item)

    override fun getIndexOfItem(itemAt: Any): Int = items.indexOf(itemAt)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dummy_layout, parent, false)
        return DummyViewHolder(view, itemClick)
    }

    fun setData(newDataSet: MutableList<DummyItem>) {
        items = newDataSet.toList() as ArrayList<DummyItem>
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DummyViewHolder, position: Int) {
        holder.bindView(items[position], position)
    }

    override fun getItemCount(): Int = items.size ?: 0

    class DummyViewHolder(val view: View, val itemClick: (DummyItem) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindView(item: DummyItem, position: Int) {
            with(item) {
                item.position = position
                itemView.name.text = "original position: ${item.position}"
                itemView.setOnClickListener { itemClick(this) }
            }

        }
    }

}