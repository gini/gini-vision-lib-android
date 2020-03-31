package net.gini.android.vision.digitalinvoice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.gini.android.vision.R
import net.gini.android.vision.analysis.AnalysisActivity
import net.gini.android.vision.camera.CameraActivity
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsActivity
import net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

private const val RETURN_ASSISTANT_FRAGMENT = "RETURN_ASSISTANT_FRAGMENT"

private const val EDIT_LINE_ITEM_REQUEST = 1

private const val EXTRA_IN_EXTRACTIONS = "EXTRA_IN_EXTRACTIONS"

private const val EXTRA_IN_COMPOUND_EXTRACTIONS = "EXTRA_IN_COMPOUND_EXTRACTIONS"

/**
 * When you use the Screen API, the `DigitalInvoiceActivity` displays the line items extracted from an invoice document and their total
 * price. The user can deselect line items which should not be paid for and also edit the quantity, price or description of each line item.
 * The total price is always updated to include only the selected line items.
 *
 * The returned extractions in the [CameraActivity.EXTRA_OUT_EXTRACTIONS] and [CameraActivity.EXTRA_OUT_COMPOUND_EXTRACTIONS] are updated to
 * include the user's modifications:
 * - "amountToPay" is updated to contain the sum of the selected line items' prices,
 * - the line items are updated according to the user's modifications.
 *
 * The `DigitalInvoiceActivity` is started by the [AnalysisActivity] if the following are true:
 * - analysis completed successfully
 * - line item extractions have been enabled for your client id
 * - the analysis result contains line item extractions
 *
 * ### Customizing the Digital Invoice Screen
 *
 * Customizing the look of the Digital Invoice Screen is done via overriding of app resources.
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
class DigitalInvoiceActivity : AppCompatActivity(), DigitalInvoiceFragmentListener {

    private var fragment: DigitalInvoiceFragment? = null
    private lateinit var extractions: Map<String, GiniVisionSpecificExtraction>
    private lateinit var compoundExtractions: Map<String, GiniVisionCompoundExtraction>

    companion object {

        /**
         * Internal use only.
         *
         * @suppress
         */
        @JvmStatic
        fun createIntent(activity: Activity, extractions: Map<String, GiniVisionSpecificExtraction>,
                         compoundExtractions: Map<String, GiniVisionCompoundExtraction>) =
                Intent(activity, DigitalInvoiceActivity::class.java).apply {
                    putExtra(EXTRA_IN_EXTRACTIONS, Bundle().apply {
                        extractions.forEach { putParcelable(it.key, it.value) }
                    })
                    putExtra(EXTRA_IN_COMPOUND_EXTRACTIONS, Bundle().apply {
                        compoundExtractions.forEach { putParcelable(it.key, it.value) }
                    })
                }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gv_activity_digital_invoice)
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
        extractions = intent.extras?.getBundle(EXTRA_IN_EXTRACTIONS)?.run {
            keySet().map { it to getParcelable<GiniVisionSpecificExtraction>(it)!! }.toMap()
        } ?: emptyMap()
        compoundExtractions = intent.extras?.getBundle(EXTRA_IN_COMPOUND_EXTRACTIONS)?.run {
            keySet().map { it to getParcelable<GiniVisionCompoundExtraction>(it)!! }.toMap()
        } ?: emptyMap()
    }

    private fun initFragment() {
        if (!isFragmentShown()) {
            createFragment()
            showFragment()
        }
    }

    private fun isFragmentShown() = supportFragmentManager.findFragmentByTag(
            RETURN_ASSISTANT_FRAGMENT) != null

    private fun createFragment() {
        fragment = DigitalInvoiceFragment.createInstance(extractions, compoundExtractions)
    }

    private fun showFragment() = fragment?.let {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.gv_fragment_digital_invoice, it, RETURN_ASSISTANT_FRAGMENT)
                .commit()
    }

    private fun retainFragment() {
        fragment = supportFragmentManager.findFragmentByTag(
                RETURN_ASSISTANT_FRAGMENT) as DigitalInvoiceFragment?
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onEditLineItem(selectableLineItem: SelectableLineItem) {
        startActivityForResult(LineItemDetailsActivity.createIntent(this, selectableLineItem),
                EDIT_LINE_ITEM_REQUEST)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onPayInvoice(specificExtractions: Map<String, GiniVisionSpecificExtraction>,
                              compoundExtractions: Map<String, GiniVisionCompoundExtraction>) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(CameraActivity.EXTRA_OUT_EXTRACTIONS, Bundle().apply {
                specificExtractions.forEach { putParcelable(it.key, it.value) }
            })
            putExtra(CameraActivity.EXTRA_OUT_COMPOUND_EXTRACTIONS, Bundle().apply {
                compoundExtractions.forEach { putParcelable(it.key, it.value) }
            })
        })
        finish()
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_LINE_ITEM_REQUEST -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.getParcelableExtra<SelectableLineItem>(
                                LineItemDetailsActivity.EXTRA_OUT_SELECTABLE_LINE_ITEM)?.let {
                            fragment?.updateLineItem(it)
                        }
                    }
                }
            }
        }
    }
}