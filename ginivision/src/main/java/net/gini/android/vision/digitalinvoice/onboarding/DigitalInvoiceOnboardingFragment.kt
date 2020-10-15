package net.gini.android.vision.digitalinvoice.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.gv_fragment_digital_invoice_onboarding.*
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 14.10.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
internal class DigitalInvoiceOnboardingFragment : Fragment() {

    companion object {

        @JvmStatic
        fun createInstance() = DigitalInvoiceOnboardingFragment()
    }

    var listener: DigitalInvoiceOnboardingFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.gv_fragment_digital_invoice_onboarding, container, false)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context).inflateTransition(R.transition.fade)
        exitTransition = enterTransition
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInputHandlers()
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onDestroy() {
        listener = null
        super.onDestroy()
    }

    private fun setInputHandlers() {
        gv_done_button.setOnClickListener {
            listener?.onCloseOnboarding()
        }
    }
}