package de.taop.swipeactionsample.sampleActions

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import de.taop.swipeactionsample.R
import java.util.concurrent.TimeUnit


/**
 * Example Share Action.
 *
 * @author Adrian Bernhart
 */
class ShareAction(recyclerView: RecyclerView) : de.taop.swipeaction.actions.SwipeAction(recyclerView, "share", R.drawable.ic_share_black_24dp, actionBGColorID = R.color.colorShare) {

    companion object {
        val REQUEST_WRITE_STORAGE = 1617
    }

    private var lastSharedPosition: Int = -1

    override fun performAction(swipedHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
        lastSharedPosition = swipedHolder.adapterPosition
        share(swipedHolder.adapterPosition)

        //reset the swiped item after a timeout
        Thread({
            TimeUnit.MILLISECONDS.sleep(1000)

            Handler(Looper.getMainLooper()).post({
                adapter.notifyItemChanged(swipedHolder.adapterPosition)
            })
        }).start()

    }

    private fun share(adapterPosition: Int) {
        startShareIntent("Position $adapterPosition was shared!")
    }

    private fun startShareIntent(urlKey: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text"
        share.putExtra(Intent.EXTRA_TEXT, urlKey)
        // grants user read permission to the uri if needed
        startActivity(context, Intent.createChooser(share, "Share Action"), null)
    }


}