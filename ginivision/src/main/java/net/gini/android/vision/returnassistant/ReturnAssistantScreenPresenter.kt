package net.gini.android.vision.returnassistant

import android.app.Activity

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
internal class ReturnAssistantScreenPresenter(activity: Activity, view: ReturnAssistantScreenContract.View) :
        ReturnAssistantScreenContract.Presenter(activity, view) {

    override var listener: ReturnAssistantFragmentListener? = null

    init {
        view.setPresenter(this)
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}