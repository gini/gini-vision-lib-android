package net.gini.android.vision.help

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.gv_activity_return_assistant_help.*
import net.gini.android.vision.GiniVision
import net.gini.android.vision.R
import net.gini.android.vision.internal.util.ActivityHelper

class ReturnAssistantHelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gv_activity_return_assistant_help)
        setupHomeButton()
        setInputHandlers()
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

    private fun setInputHandlers() {
        gv_back_button.setOnClickListener {
            finish()
        }
    }

}