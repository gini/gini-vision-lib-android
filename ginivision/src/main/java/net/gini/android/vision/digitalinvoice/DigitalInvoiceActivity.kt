package net.gini.android.vision.digitalinvoice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.gini.android.vision.R
import net.gini.android.vision.camera.CameraActivity
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsActivity
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

class DigitalInvoiceActivity : AppCompatActivity(), DigitalInvoiceFragmentListener {

    var fragment: DigitalInvoiceFragment? = null
    var extractions: Map<String, GiniVisionSpecificExtraction> = emptyMap()
    var compoundExtractions: Map<String, GiniVisionCompoundExtraction> = emptyMap()

    companion object {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gv_activity_digital_invoice)
        readExtras()
        if (savedInstanceState == null) {
            initFragment()
        } else {
            retainFragment()
        }
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

    override fun onEditLineItem(selectableLineItem: SelectableLineItem) {
        startActivityForResult(LineItemDetailsActivity.createIntent(this, selectableLineItem),
                EDIT_LINE_ITEM_REQUEST)
    }

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