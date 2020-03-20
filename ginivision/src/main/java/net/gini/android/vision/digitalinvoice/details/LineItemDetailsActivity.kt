package net.gini.android.vision.digitalinvoice.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.gini.android.vision.R
import net.gini.android.vision.digitalinvoice.SelectableLineItem
import net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp

private const val LINE_ITEM_DETAILS_FRAGMENT = "LINE_ITEM_DETAILS_FRAGMENT"
private const val EXTRA_IN_SELECTABLE_LINE_ITEM = "EXTRA_IN_SELECTABLE_LINE_ITEM"

class LineItemDetailsActivity : AppCompatActivity(), LineItemDetailsFragmentListener {

    companion object {
        const val EXTRA_OUT_SELECTABLE_LINE_ITEM = "EXTRA_OUT_SELECTABLE_LINE_ITEM"

        fun createIntent(
                activity: Activity, selectableLineItem: SelectableLineItem) = Intent(activity,
                LineItemDetailsActivity::class.java).apply {
            putExtra(EXTRA_IN_SELECTABLE_LINE_ITEM, selectableLineItem)
        }
    }

    var fragment: LineItemDetailsFragment? = null

    lateinit var lineItem: SelectableLineItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gv_activity_line_item_details)
        readExtras()
        if (savedInstanceState == null) {
            initFragment()
        } else {
            retainFragment()
        }
        enableHomeAsUp(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun readExtras() {
        intent.extras?.let {
            lineItem = checkNotNull(it.getParcelable(EXTRA_IN_SELECTABLE_LINE_ITEM)) {
                ("LineItemDetailsActivity requires a SelectableLineItem. " +
                        "Set it as an extra using the EXTRA_IN_SELECTABLE_LINE_ITEM key.")
            }
        }
    }

    private fun initFragment() {
        if (!isFragmentShown()) {
            createFragment()
            showFragment()
        }
    }

    private fun isFragmentShown() = supportFragmentManager.findFragmentByTag(
            LINE_ITEM_DETAILS_FRAGMENT) != null

    private fun createFragment() {
        fragment = LineItemDetailsFragment.createInstance(lineItem)
    }

    private fun showFragment() = fragment?.let {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.gv_fragment_line_item_details, it, LINE_ITEM_DETAILS_FRAGMENT)
                .commit()
    }

    private fun retainFragment() {
        fragment = supportFragmentManager.findFragmentByTag(
                LINE_ITEM_DETAILS_FRAGMENT) as LineItemDetailsFragment?
    }

    override fun onSave(selectableLineItem: SelectableLineItem) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_OUT_SELECTABLE_LINE_ITEM, selectableLineItem)
        })
        finish()
    }
}