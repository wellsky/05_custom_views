package ru.netology.nmedia.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    private var fullCharge = 0F

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            colors = listOf(
                getColor(
                    R.styleable.StatsView_color1,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color2,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color3,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color4,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color5,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color6,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color7,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color8,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color9,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color10,
                    randomColor()
                )
            )
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            fullCharge = value.sum()
            println(fullCharge)
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var firstColor: Int? = null
        var startFrom = -90F

        for ((index, datum) in data.withIndex()) {
            val angle = 360F * (datum / fullCharge)
            paint.color = colors.getOrNull(index) ?: randomColor()

            firstColor ?: run { firstColor = paint.color }

            canvas.drawArc(oval, startFrom, angle, false, paint)
            startFrom += angle
        }

        firstColor?.let {
            paint.color = it
            // canvas.drawPoint(center.x, center.y - radius, paint)
            // Если нарисовать просто точку, то при 100% заполнении справа от нее виден контур последнего цвета
            // Дуга в 1 градус убирает этот артефакт
            canvas.drawArc(oval, -90F, 1F, false, paint)
        }

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100 / fullCharge),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}