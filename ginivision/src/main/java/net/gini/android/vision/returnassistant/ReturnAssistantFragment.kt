package net.gini.android.vision.returnassistant

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class ReturnAssistantFragment : Fragment(), ReturnAssistantScreenContract.View,
        ReturnAssistantFragmentInterface {

    override var listener: ReturnAssistantFragmentListener?
        get() = this.presenter?.listener
        set(value) {
            this.presenter?.listener = value
        }

    private var presenter: ReturnAssistantScreenContract.Presenter? = null

    companion object {

        @JvmStatic
        fun createInstance() = ReturnAssistantFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this.activity
        checkNotNull(activity) {
            "Missing activity for fragment."
        }
        createPresenter(activity)
        initListener()
    }

    private fun createPresenter(activity: Activity) = ReturnAssistantScreenPresenter(activity, this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.gv_fragment_return_assistant, container, false)
    }

    private fun initListener() {
        if (activity is ReturnAssistantFragmentListener) {
            listener = activity as ReturnAssistantFragmentListener?
        } else checkNotNull(listener) {
            ("MultiPageReviewFragmentListener not set. "
                    + "You can set it with MultiPageReviewFragment#setListener() or "
                    + "by making the host activity implement the MultiPageReviewFragmentListener.")
        }
    }

    override fun setPresenter(presenter: ReturnAssistantScreenContract.Presenter) {
        this.presenter = presenter
    }

}
