package net.gini.android.vision.digitalinvoice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.gini.android.vision.R
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsActivity

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

private const val RETURN_ASSISTANT_FRAGMENT = "RETURN_ASSISTANT_FRAGMENT"

private const val EDIT_LINE_ITEM_REQUEST = 1

class DigitalInvoiceActivity : AppCompatActivity(), DigitalInvoiceFragmentListener {

    var fragment: DigitalInvoiceFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gv_activity_digital_invoice)

        if (savedInstanceState == null) {
            initFragment()
        } else {
            retainFragment()
        }
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
        fragment = DigitalInvoiceFragment.createInstance()
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
