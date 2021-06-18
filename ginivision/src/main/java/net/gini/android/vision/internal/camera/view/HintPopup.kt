package net.gini.android.vision.internal.camera.view

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter

/**
 * Internal use only.
 *
 * @suppress
 */
internal class HintPopup(
        private val popupView: View,
        private val popupArrow: View,
        closeButton: View,
        private val animationDuration: Long,
        private val onCloseClicked: () -> Unit) {

    private var popupAnimation: ViewPropertyAnimatorCompat? = null
    private var popupArrowAnimation: ViewPropertyAnimatorCompat? = null

    var isShown = false
        private set

    init {
        closeButton.setOnClickListener {
            onCloseClicked()
        }
    }

    fun show() {
        popupView.visibility = View.VISIBLE
        popupArrow.visibility = View.VISIBLE
        clearUploadHintPopUpAnimations()
        popupArrowAnimation = ViewCompat.animate(
                popupView)
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(object: ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationEnd(view: View?) {
                        isShown = true
                    }
                })
                .apply {
                    start()
                }
        popupAnimation = ViewCompat.animate(
                popupArrow)
                .alpha(1f)
                .setDuration(animationDuration)
                .apply {
                    start()
                }
    }

    fun hide(animatorListener: ViewPropertyAnimatorListenerAdapter?) {
        clearUploadHintPopUpAnimations()
        popupArrowAnimation = ViewCompat.animate(popupView)
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationEnd(view: View) {
                        isShown = false
                        popupArrow.visibility = View.GONE
                        popupView.visibility = View.GONE
                        animatorListener?.onAnimationEnd(view)
                    }
                })
                .apply {
                    start()
                }
        popupAnimation = ViewCompat.animate(popupArrow)
                .alpha(0f)
                .setDuration(animationDuration)
                .apply {
                    start()
                }
    }

    private fun clearUploadHintPopUpAnimations() {
        popupArrowAnimation?.apply {
            cancel()
            popupView.clearAnimation()
            setListener(null)
        }
        popupAnimation?.apply {
            cancel()
            popupView.clearAnimation()
            setListener(null)
        }
    }
}