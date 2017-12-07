package de.taop.swipeactionsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import de.taop.swipeaction.AlternatingBackgroundItemDecoration
import de.taop.swipeaction.SwipeActionSetupHelper
import de.taop.swipeaction.actions.DeleteAction
import de.taop.swipeactionsample.dummyItems.DummyAdapter
import de.taop.swipeactionsample.dummyItems.DummyItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            (list.adapter as DummyAdapter).addItem(DummyItem())
        }

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = DummyAdapter(List(6, {
            DummyItem(it)
        }) as ArrayList<DummyItem>)

        // set Background colors of list
        list.addItemDecoration(AlternatingBackgroundItemDecoration(this, R.color.colorWhite, R.color.colorGrey))

        // create swipe actions
        val pendingDeleteAction = DeleteAction(list, R.id.container)
        val instantDeleteAction = DeleteAction(list, R.id.container,
                false,
                iconID = R.drawable.ic_delete_forever_black_24dp,
                colorID = R.color.colorInstantDelete)

        instantDeleteAction.setSnackBarText(R.string.deleteUndo, R.plurals.deletedItems)

        SwipeActionSetupHelper.setUpRecyclerView(this, list, pendingDeleteAction, instantDeleteAction)

        instantDeleteAction.itemsDeletedCallback = {
        }

        pendingDeleteAction.itemsDeletedCallback = {
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
