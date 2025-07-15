package com.ikhut.messengerapp.presentation.components

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.config.Constants
import kotlin.math.cos
import kotlin.math.sin

class LoadingDotsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val dots = mutableListOf<Dot>()

    private val animatorSet = AnimatorSet()

    data class Dot(
        var x: Float, var y: Float, var alpha: Float = 1f, var scale: Float = 1f
    )

    init {
        paint.color = ContextCompat.getColor(context, R.color.loading_dots_color)
        paint.style = Paint.Style.FILL

        setupDots()
    }

    private fun setupDots() {
        dots.clear()

        if (width > 0 && height > 0) {
            val centerX = width / 2f
            val centerY = height / 2f

            for (i in 0 until Constants.DOT_COUNT) {
                val angle = (2 * Math.PI * i / Constants.DOT_COUNT).toFloat()
                val x = centerX + Constants.CIRCLE_RADIUS * cos(angle)
                val y = centerY + Constants.CIRCLE_RADIUS * sin(angle)
                dots.add(Dot(x, y))
            }

            startAnimation()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupDots()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (dot in dots) {
            paint.alpha = (255 * dot.alpha).toInt()

            canvas.drawCircle(
                dot.x, dot.y, Constants.DOT_RADIUS * dot.scale, paint
            )
        }
    }

    private fun startAnimation() {
        stopAnimation()

        val animators = mutableListOf<ObjectAnimator>()

        for (i in dots.indices) {
            val dot = dots[i]

            val scaleAnimator =
                ObjectAnimator.ofFloat(dot, Constants.PARAM_SCALE, 1f, 1.4f, 1f).apply {
                    duration = Constants.ANIM_DURATION
                    repeatCount = ObjectAnimator.INFINITE
                    startDelay = (i * Constants.ANIM_DELAY).toLong()
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener { invalidate() }
                }

            val alphaAnimator =
                ObjectAnimator.ofFloat(dot, Constants.PARAM_ALPHA, 1f, 0.4f, 1f).apply {
                    duration = Constants.ANIM_DURATION
                    repeatCount = ObjectAnimator.INFINITE
                    startDelay = (i * Constants.ANIM_DELAY).toLong()
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener { invalidate() }
                }

            animators.add(scaleAnimator)
            animators.add(alphaAnimator)
        }

        animatorSet.playTogether(animators as Collection<Animator>?)
        animatorSet.start()
    }

    private fun stopAnimation() {
        animatorSet.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}