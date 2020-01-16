package net.gini.android.vision.returnassistant

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

private const val RETURN_ASSISTANT_FRAGMENT = "RETURN_ASSISTANT_FRAGMENT"

class ReturnAssistantActivity : AppCompatActivity(), ReturnAssistantFragmentListener {

    var fragment: ReturnAssistantFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gv_activity_return_assistant)

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
        fragment = ReturnAssistantFragment.createInstance()
    }

    private fun showFragment() = fragment?.let {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.gv_fragment_return_assistant, it, RETURN_ASSISTANT_FRAGMENT)
                .commit()
    }

    private fun retainFragment() {
        fragment = supportFragmentManager.findFragmentByTag(
                RETURN_ASSISTANT_FRAGMENT) as ReturnAssistantFragment?
    }
}
