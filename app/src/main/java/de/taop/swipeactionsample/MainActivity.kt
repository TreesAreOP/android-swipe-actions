package de.taop.swipeactionsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import de.taop.swipeaction.AlternatingBackgroundItemDecoration
import de.taop.swipeaction.SwipeActionSetupHelper
import de.taop.swipeaction.actions.DeleteAction
import de.taop.swipeaction.actions.SwipeAction
import de.taop.swipeactionsample.dummyItems.DummyAdapter
import de.taop.swipeactionsample.dummyItems.DummyItem
import de.taop.swipeactionsample.sampleActions.ShareAction

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    //needed for changing actions at runtime
    private var oldItemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // add new item on click
        fab.setOnClickListener {
            (list.adapter as DummyAdapter).addItem(DummyItem())
        }

        //setup recyclerview
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = DummyAdapter(List(6, {
            DummyItem(it)
        }) as ArrayList<DummyItem>)

        // create swipe actions
        val shareAction = ShareAction(list)
        val pendingDeleteAction = DeleteAction(list, R.id.container)
        val instantDeleteAction = DeleteAction(list, R.id.container,
                false,
                iconID = R.drawable.ic_delete_forever_black_24dp,
                colorID = R.color.colorInstantDelete,
                name = "instantDelete")

        // set delete snackbar Text
        pendingDeleteAction.setSnackBarText(R.string.deleteUndo, R.plurals.deletedItems)

        // on delete listener
        instantDeleteAction.itemsDeletedCallback = {
        }

        pendingDeleteAction.itemsDeletedCallback = {
        }

        // set alternating background colors (optional)
        list.addItemDecoration(AlternatingBackgroundItemDecoration(this, R.color.colorWhite, R.color.colorGrey))

         /*initialize the swipe actions
         this is required! it's not used in the example because we use the two spinners to set up the recyclerview*/
        // SwipeActionSetupHelper.setUpRecyclerView(this@MainActivity, list, pendingDeleteAction, pendingDeleteAction)

        // apply different actions
        var swipeLeftAction: SwipeAction = pendingDeleteAction
        var swipeRightAction: SwipeAction = pendingDeleteAction

        // not very clean code but works for this example!
        leftActionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                swipeRightAction = when (p2) {
                    0 -> pendingDeleteAction
                    1 -> instantDeleteAction
                    2 -> shareAction
                    else -> pendingDeleteAction
                }
                updateRecyclerView(swipeLeftAction, swipeRightAction)
            }

        }

        rightActionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                swipeLeftAction = when (p2) {
                    0 -> pendingDeleteAction
                    1 -> instantDeleteAction
                    2 -> shareAction
                    else -> pendingDeleteAction
                }
                updateRecyclerView(swipeLeftAction, swipeRightAction)
            }
        }
    }

    private fun updateRecyclerView(swipeLeftAction: SwipeAction, swipeRightAction: SwipeAction) {
        // if an pending delete is active --> undo it
        (swipeLeftAction as? DeleteAction)?.undoPendingItems(true)
        (swipeRightAction as? DeleteAction)?.undoPendingItems(true)


        // reset all items --> redraws the item!
        list.adapter.notifyItemRangeChanged(0, list.adapter.itemCount - 1)

        // This is absolutely necessary if you want to change the action on the fly!
        // Otherwise the actions won't update!
        oldItemTouchHelper?.attachToRecyclerView(null)
        oldItemTouchHelper = SwipeActionSetupHelper.setUpRecyclerView(this@MainActivity, list, swipeLeftAction, swipeRightAction)

    }
}
