package ba.rubicon.widget.battery

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.graphics.drawable.VectorDrawableCompat
import android.util.AttributeSet
import android.view.View


class BatteryMeterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var batteryHeadWidth = 0
    private var contentHeight: Int = 0
    private var contentWidth: Int = 0
    private var mainContentOffset: Int = 20

    // Shapes
    private val backgroundRect: Rect = Rect()

    private val batteryLevelRect: Rect = Rect()

    private val batteryHeadRect: Rect = Rect()

    // Paints
    private val backgroundPaint: Paint = Paint(ANTI_ALIAS_FLAG)

    private val backgroundPaintStroke: Paint = Paint(ANTI_ALIAS_FLAG)

    private val textValuePaint: Paint = Paint(ANTI_ALIAS_FLAG)

    private val batteryHeadPaint: Paint = Paint(ANTI_ALIAS_FLAG)

    private val batteryLevelPaint: Paint = Paint(ANTI_ALIAS_FLAG)

    private val chargingLogoPaint: Paint = Paint(ANTI_ALIAS_FLAG)

    // Colors

    var batteryLevelColor: Int = DEFAULT_BATTERY_LEVEL_COLOR
        set(@ColorInt color) {
            field = color
            batteryLevelPaint.color = color
            invalidate()
        }

    var warningColor: Int = DEFAULT_WARNING_COLOR
        set(@ColorInt color) {
            field = color
            batteryLevelPaint.color = color
            invalidate()
        }

    var backgroundRectColor: Int = DEFAULT_BACKGROUND_COLOR
        set(@ColorInt color) {
            field = color
            backgroundPaint.color = color
            invalidate()
        }

    var batteryHeadColor: Int = DEFAULT_BATTERY_HEAD_COLOR
        set(@ColorInt color) {
            field = color
            batteryHeadPaint.color = color
            invalidate()
        }

    var chargingColor: Int = DEFAULT_CHARGING_COLOR
        set(@ColorInt color) {
            field = color
            chargingLogoPaint.color = color
            invalidate()
        }

    var textColor: Int = DEFAULT_TEXT_COLOR
        set(@ColorInt color) {
            field = color
            textValuePaint.color = color
            invalidate()
        }

    // Battery Status

    var isCharging: Boolean = DEFAULT_CHARGING_STATE
        @CheckResult
        get() = field
        set(value) {
            field = value
            invalidate()
        }

    var batteryLevel: Int = DEFAULT_BATTERY_LEVEL
        @CheckResult
        get() = field
        set(level) {
            field = when {
                level > 100 -> 100
                level < 0 -> 0
                else -> level
            }
            if (field <= warningLevel) {
                batteryLevelPaint.color = warningColor
            } else {
                batteryLevelPaint.color = batteryLevelColor
            }
            invalidate()
        }

    var warningLevel: Int = DEFAULT_WARNING_LEVEL

    init {
        parseAttr(attrs)
        /*
        * Initialize all properties
         */
        batteryLevelPaint.apply {
            style = Paint.Style.FILL
            color = batteryLevelColor
        }

        backgroundPaint.apply {
            style = Paint.Style.FILL
            color = backgroundRectColor
        }
        backgroundPaintStroke.apply {
            style = Paint.Style.STROKE
            strokeWidth = 20f
            color = Color.BLACK
        }

        batteryHeadPaint.apply {
            style = Paint.Style.FILL
            color = batteryHeadColor
        }

        chargingLogoPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = chargingColor
            strokeWidth = 5f
        }

        textValuePaint.apply {
            textAlign = Paint.Align.CENTER
            color = textColor
        }
    }

    private fun parseAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.BatteryMeterView, 0, 0
        )
        isCharging = typedArray.getBoolean(
            R.styleable.BatteryMeterView_charging,
            DEFAULT_CHARGING_STATE
        )
        batteryLevel = typedArray.getInteger(
            R.styleable.BatteryMeterView_battery_level,
            DEFAULT_BATTERY_LEVEL
        )
        warningLevel = typedArray.getInteger(
            R.styleable.BatteryMeterView_warning_level,
            DEFAULT_WARNING_LEVEL
        )
        batteryLevelColor = typedArray.getColor(
            R.styleable.BatteryMeterView_normal_fill_color,
            DEFAULT_BATTERY_LEVEL_COLOR
        )
        backgroundRectColor = typedArray.getColor(
            R.styleable.BatteryMeterView_background_fill_color,
            DEFAULT_BACKGROUND_COLOR
        )
        warningColor = typedArray.getColor(
            R.styleable.BatteryMeterView_warning_fill_color,
            DEFAULT_WARNING_COLOR
        )
        batteryHeadColor = typedArray.getColor(
            R.styleable.BatteryMeterView_battery_head_color,
            DEFAULT_BATTERY_HEAD_COLOR
        )
        chargingColor = typedArray.getColor(
            R.styleable.BatteryMeterView_charging_color,
            DEFAULT_CHARGING_COLOR
        )
        textColor = typedArray.getColor(
            R.styleable.BatteryMeterView_text_color,
            DEFAULT_TEXT_COLOR
        )
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        contentWidth = width - paddingLeft - paddingRight
        contentHeight = height - paddingTop - paddingBottom
        textValuePaint.textSize = contentHeight * TEXT_SIZE_RATIO
        batteryHeadWidth = (1f / 12f * contentWidth).toInt()


        // Draw the background body of battery view
        drawBackground(canvas)

        // Draw the head of battery
        drawBatteryHead(canvas)

        // Draw the current battery level
        drawBatteryLevel(canvas)

        if (isCharging) {
            drawChargingLogo(canvas)
        } else {
            drawCurrentBatteryValueText(canvas)
        }
    }

    private fun drawCurrentBatteryValueText(canvas: Canvas) {
        val text = if (batteryLevel == 0) "Empty" else batteryLevel.toString()
        canvas.drawText(
            text,
            (contentWidth * 0.5).toFloat(),
            (contentHeight * 0.7).toFloat(),
            textValuePaint
        )
    }

    private fun drawBatteryEmptyStatus(canvas: Canvas) {
        canvas.drawText(
            "Empty",
            (contentWidth * 0.5).toFloat(),
            (contentHeight * 0.7).toFloat(),
            textValuePaint
        )
    }

    private fun drawChargingLogo(canvas: Canvas) {
        val chargingLogoDrawable = VectorDrawableCompat.create(context.resources,
            R.drawable.ic_charging_bolt, null)?.apply {
            setBounds(backgroundRect.left + contentWidth/4, backgroundRect.top + contentHeight/4, backgroundRect.right - contentWidth/4, backgroundRect.bottom - contentHeight/4)
            setColorFilter(chargingColor,PorterDuff.Mode.SRC_IN   )

        }
        chargingLogoDrawable?.draw(canvas)
    }

    private fun drawBatteryLevel(canvas: Canvas) {
        batteryLevelRect.set(
            backgroundRect.left + mainContentOffset,
            backgroundRect.top + mainContentOffset,
            ((backgroundRect.right - mainContentOffset) * (this.batteryLevel.toDouble() / 100.toDouble())).toInt(),
            backgroundRect.bottom - mainContentOffset
        )

        if (batteryLevel <= warningLevel)
            batteryLevelPaint.color = warningColor
        else
            batteryLevelPaint.color = batteryLevelColor

        if (batteryLevel == 0) {
            drawBatteryEmptyStatus(canvas)
        } else {
            canvas.drawRoundRect(RectF(batteryLevelRect),25f,25f, batteryLevelPaint)
        }

    }

    private fun drawBatteryHead(canvas: Canvas) {
        batteryHeadRect.set(
            paddingLeft + contentWidth - batteryHeadWidth + mainContentOffset,
            paddingTop + contentHeight/4,
            paddingLeft + contentWidth,
            paddingTop + contentHeight*3/4
        )
        // Draw the head of battery view
        canvas.drawRoundRect(RectF(batteryHeadRect),10f,10f, batteryHeadPaint)
    }

    private fun drawBackground(canvas: Canvas) {
        backgroundRect.set(
            paddingLeft + 10,
            paddingTop + 10,
            paddingLeft + contentWidth - batteryHeadWidth + 10,
            paddingTop + contentHeight - 10
        )
        //backgroundRect corners made rounded to avoid coming out of backgroundPaintStroke
        canvas.drawRoundRect(RectF(backgroundRect),50f,50f, backgroundPaint)
        canvas.drawRoundRect(RectF(backgroundRect),50f,50f, backgroundPaintStroke)
    }

    companion object {
        private const val DEFAULT_CHARGING_STATE = false
        private const val DEFAULT_BATTERY_LEVEL = 70
        private const val DEFAULT_WARNING_LEVEL = 30
        private const val DEFAULT_BATTERY_LEVEL_COLOR = Color.GREEN
        private const val DEFAULT_WARNING_COLOR = Color.RED
        private const val DEFAULT_BACKGROUND_COLOR = Color.LTGRAY
        private const val DEFAULT_BATTERY_HEAD_COLOR = Color.DKGRAY
        private const val DEFAULT_TEXT_COLOR = Color.DKGRAY
        private const val DEFAULT_CHARGING_COLOR = Color.DKGRAY
        private const val TEXT_SIZE_RATIO = 0.5f
    }
}
