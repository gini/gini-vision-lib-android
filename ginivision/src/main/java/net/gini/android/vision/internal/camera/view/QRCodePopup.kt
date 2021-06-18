package net.gini.android.vision.internal.camera.view

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import net.gini.android.vision.internal.ui.FragmentImplCallback

/**
 * Internal use only.
 *
 * @suppress
 */
internal class QRCodePopup<T> @JvmOverloads constructor(
        private val fragmentImplCallback: FragmentImplCallback,
        private val popupView: View,
        private val animationDuration: Long,
        private val hideDelayMs: Long,
        private val showAgainDelayMs: Long,
        private val onClicked: (T?) -> Unit = {}) {

    private var animation: ViewPropertyAnimatorCompat? = null
    private val hideRunnable: Runnable = Runnable {
        hide()
    }

    var qrCodeContent: T? = null
        private set

    var isShown = false
        private set

    init {
        popupView.setOnClickListener {
            onClicked(qrCodeContent)
            hide()
        }
    }

    @JvmOverloads
    fun show(qrCodeContent: T, startDelay: Long = 0) {
        if (this.qrCodeContent != null && qrCodeContent != this.qrCodeContent) {
            hide(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View?) {
                    show(showAgainDelayMs)
                }
            })
        } else {
            show(startDelay)
        }

        this.qrCodeContent = qrCodeContent
    }

    private fun show(startDelay: Long = 0) {
        if (popupView.alpha != 0f) {
            fragmentImplCallback.view?.removeCallbacks(hideRunnable)
            fragmentImplCallback.view?.postDelayed(hideRunnable, hideDelayMs)
            return
        }

        clearQRCodeDetectedPopUpAnimation()
        popupView.visibility = View.VISIBLE
        animation = ViewCompat.animate(popupView)
                .alpha(1.0f)
                .setStartDelay(startDelay)
                .setDuration(animationDuration)
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationEnd(view: View?) {
                        isShown = true
                    }
                })
                .apply {
                    start()
                }

        fragmentImplCallback.view?.removeCallbacks(hideRunnable)
        fragmentImplCallback.view?.postDelayed(hideRunnable, hideDelayMs)
    }

    @JvmOverloads
    fun hide(animatorListener: ViewPropertyAnimatorListener? = null) {
        qrCodeContent = null

        if (popupView.alpha != 1f) {
            animatorListener?.onAnimationEnd(popupView)
            return
        }
        clearQRCodeDetectedPopUpAnimation()
        animation = ViewCompat.animate(popupView)
                .alpha(0.0f)
                .setDuration(animationDuration)
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationEnd(view: View) {
                        popupView.visibility = View.GONE
                        isShown = false
                        animatorListener?.onAnimationEnd(view)
                    }
                })
                .apply {
                    start()
                }

        fragmentImplCallback.view?.removeCallbacks(hideRunnable)
    }

    private fun clearQRCodeDetectedPopUpAnimation() {
        animation?.apply {
            cancel()
            popupView.clearAnimation()
            setListener(null)
        }
        fragmentImplCallback.view?.removeCallbacks(hideRunnable)
    }
}