package net.gini.android.vision.help

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import net.gini.android.vision.GiniVision
import net.gini.android.vision.R
import net.gini.android.vision.internal.util.ActivityHelper

class ReturnAssistantHelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_assistant_help)
        setupHomeButton()
    }

    private fun setupHomeButton() {
        if (GiniVision.hasInstance() && GiniVision.getInstance().areBackButtonsEnabled()) {
            ActivityHelper.enableHomeAsUp(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}