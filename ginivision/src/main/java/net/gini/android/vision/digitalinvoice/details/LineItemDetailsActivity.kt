package net.gini.android.vision.digitalinvoice.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.gini.android.vision.R
import net.gini.android.vision.camera.CameraActivity
import net.gini.android.vision.digitalinvoice.DigitalInvoiceActivity
import net.gini.android.vision.digitalinvoice.SelectableLineItem
import net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp

private const val LINE_ITEM_DETAILS_FRAGMENT = "LINE_ITEM_DETAILS_FRAGMENT"
private const val EXTRA_IN_SELECTABLE_LINE_ITEM = "EXTRA_IN_SELECTABLE_LINE_ITEM"

/**
 * When you use the Screen API, the `LineItemDetailsActivity` displays a line item to be edited by the user. The user can modify the
 * following:
 * - deselect the line item,
 * - edit the line item description,
 * - edit the quantity,
 * - edit the price.
 *
 * The extractions returned in the [CameraActivity.EXTRA_OUT_COMPOUND_EXTRACTIONS] are updated to include the user's modifications.
 *
 * The `LineItemDetailsActivity` is started by the [DigitalInvoiceActivity] when the user taps on a line item to edit it.
 *
 * ### Customizing the Line Item Details Screen
 *
 * Customizing the look of the Line Item Details Screen is done via overriding of app resources.
 *
 * The following items are customizable:
 * - TODO
 * - TODO
 *
 * **Important:** All overriden styles must have their respective `Root.` prefixed style as their parent. Ex.: the parent of
 * `GiniVisionTheme.Snackbar.Error.TextStyle` must be `Root.GiniVisionTheme.Snackbar.Error.TextStyle`.
 *
 * ### Customizing the Action Bar
 *
 * Customizing the Action Bar is also done via overriding of app resources and each one - except the title string resource - is global to
 * all Activities.
 *
 * The following items are customizable:
 * - **Background color:** via the color resource named `gv_action_bar` (highly recommended for Android 5+: customize the status bar color
 * via `gv_status_bar`)
 * - **Back button:** via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_action_bar_back}
 */
class LineItemDetailsActivity : AppCompatActivity(), LineItemDetailsFragmentListener {

    companion object {
        internal const val EXTRA_OUT_SELECTABLE_LINE_ITEM = "EXTRA_OUT_SELECTABLE_LINE_ITEM"

        @JvmSynthetic
        internal fun createIntent(
                activity: Activity, selectableLineItem: SelectableLineItem) = Intent(activity,
                LineItemDetailsActivity::class.java).apply {
            putExtra(EXTRA_IN_SELECTABLE_LINE_ITEM, selectableLineItem)
        }
    }

    private var fragment: LineItemDetailsFragment? = null

    private lateinit var lineItem: SelectableLineItem

    /**
     * Internal use only.
     *
     * @suppress
     */
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

    /**
     * Internal use only.
     *
     * @suppress
     */
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

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onSave(selectableLineItem: SelectableLineItem) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_OUT_SELECTABLE_LINE_ITEM, selectableLineItem)
        })
        finish()
    }
}