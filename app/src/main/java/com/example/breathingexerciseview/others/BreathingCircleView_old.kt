package com.example.breathingexerciseview.others

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Color
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


class BreathingCircleView : View {

    var mWidth = 0
    var mHeight = 0
    var mSide = 0

    //main inner big circle
    var centerY:Float = 0f
    var centerX:Float = 0f
    var innerCircleRadius:Float = 0f
    var innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    //outer cirle
    var outerCircleRadius : Float = 0f
    var outerCircleStrokeWidth : Float = 7.toPx()
    val outerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = outerCircleStrokeWidth
    }
    lateinit var outerCirclePath:Path;
    //lateinit var angleAnimator : ValueAnimator

    //orbiting circle
    var orbitingCircleCenterX:Float = 0f
    var orbitingCircleCenterY:Float = 0f
    lateinit var rotatingPoint : PointF
    var orbitingCircleRadius:Float = 0f
    var swipeAngle = -90f
    var smallCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }
    val paint1 = Paint().apply { color = Color.DKGRAY }
    val paint2 = Paint().apply { color = Color.GRAY }
    val paint3 = Paint().apply { color = Color.GREEN }
    val paint4 = Paint().apply { color = Color.BLUE }

    var inhaleSecs = 5
    var exhaleSsecs = 5
    var hold1Secs = 3
    var hold2Secs = 3

    //static points/circles
    lateinit var firstPoint : PointF
    lateinit var secondPoint : PointF
    lateinit var thirdPoint : PointF
    lateinit var fourthPoint : PointF
    var theta1:Double = 0.0
    var theta2:Double = 0.0
    var theta3:Double = 0.0

    var centerText = "breathe in"
    val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        textAlign = Paint.Align.CENTER
        textSize = 16.toPx()
    }
    val textRect = Rect()

    lateinit var rect1: RectF
    lateinit var rect2: RectF

    val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = outerCircleStrokeWidth
    }
    val arcPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = outerCircleStrokeWidth
    }

    //for scale in/out anim
    var scaleFactor: Float = 0.3f
    var reductionScaleFactor = -scaleFactor
    var duration = 1000
    var isScaling = false

    lateinit var valueAnimator : ValueAnimator

    //scaling
    var f1Max = 0F
    var f2Max = 0F
    var f3Max = 0F
    val MAX_SCALE = 1.4F
    var xScale = 0F
    var yScale = 0F
    var maxScale = 0F
    val initialScale = 1 / MAX_SCALE
    val availableScale = MAX_SCALE - 1.0F
    //var thirdArcMinValue = 0F

    //sounds
    var isSound1Playing = false
    var isSound2Playing = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //-90
        //value 0-1 -> -90 to 0 to 270
        //4 quad -
        valueAnimator = ValueAnimator.ofFloat(0f, 1F).apply {
            addUpdateListener {
                //rotating point -> change center
                val fraction = animatedValue as Float
                swipeAngle = (2 * PI * fraction).toFloat() - (PI/2).toFloat()
                when(swipeAngle){
                    in 0.0..theta1 -> {
                        centerText = "breathe in"
                        //val scale = (animatedValue as Float)/5 + 1F
                        val scale = initialScale + (availableScale / f1Max) * fraction
                        xScale = scale
                        yScale = scale
                        maxScale = scale
                    }
                    in -PI/2..0.0 -> {
                        centerText = "breathe in"
                        //val scale = (animatedValue as Float)/2 + 1F
                        val scale = initialScale + (availableScale / f1Max) * fraction
                        xScale = scale
                        yScale = scale
                        maxScale = scale
                        if (!isSound1Playing){
                            isSound1Playing = true
                            //othersPlaying = false
                            //play sound
                            rotationListener?.onInhaleStart()
                        }
                    }
                    in theta1..theta2 -> centerText = "hold"
                    in theta2..theta3 -> {
                        centerText = "breathe out"
                        val f_diffMax = f3Max - f2Max
                        val scale = maxScale - (availableScale / f_diffMax) * (fraction - f2Max)
                        xScale = scale
                        yScale = scale
                    }
                    else /* inside fourth quad */ -> centerText = "hold"
                }
                invalidate()
                //postInvalidateOnAnimation()
            }
            duration = (inhaleSecs + hold1Secs + exhaleSsecs + hold2Secs) * 1000L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            //start()
        }
        //toggle()
    }

    private fun findFractions(){
        val total = inhaleSecs.toFloat() + hold1Secs + exhaleSsecs + hold2Secs
        f1Max = inhaleSecs / total
        f2Max = (inhaleSecs + hold1Secs) / total
        f3Max = (inhaleSecs+hold1Secs+exhaleSsecs) / total
    }

    fun start(){
        findFractions()
        valueAnimator.start()
        //valueAnimator.resume()
    }

    fun resume(){
        valueAnimator.resume()
    }

    fun stop(){
        valueAnimator.cancel()
    }

    fun pause(){
        valueAnimator.pause()
    }

    override fun onDetachedFromWindow() {
        //angleAnimator?.cancel()
        valueAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    var rotationListener : RotationListener? = null

    fun setListener(listener: RotationListener){
        rotationListener = listener
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        innerCirclePaint.color = Color.BLUE
        innerCirclePaint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // getHeight() is not reliable, use getMeasuredHeight() on first run:
        //centerX = width / 2f
        //centerY = height / 2f
        mWidth = w
        mHeight = h
        centerX = w / 2f
        centerY = h / 2f
        mSide = Math.min(mWidth, mHeight)

        val availableRadius = mSide/2F
        //outerCircleRadius = mSide/2 - 10.toPx() //(mSide/2 * 0.9).toFloat()
        outerCircleRadius = availableRadius / MAX_SCALE //(mSide/2 * 0.9).toFloat()
        val gap = outerCircleRadius / 10
        innerCircleRadius = outerCircleRadius - gap

        xScale = 1 / MAX_SCALE
        yScale = xScale
        //initialScale = xScale

        //smaller circle
        orbitingCircleCenterX = centerX
        //val offsetY = outerCircleStrokeWidth / 4
        orbitingCircleCenterY = h/2 - outerCircleRadius //+ outerCircleStrokeWidth/2 //- offsetY
        //firstPoint = PointF(orbitingCircleCenterX, orbitingCircleCenterY)

        //firstPoint = PointF(w/2F, mSide/2 - outerCircleRadius + outerCircleStrokeWidth/2)
        firstPoint = PointF(w/2F, h/2 - outerCircleRadius)

        //log("height = ${height.toDp()} h = ${h.toDp()} y = ${y.toDp()}" )
        //height = 525 h = 525 y = 529.0
        //height = 200 h = 200 y = 201
        orbitingCircleRadius = innerCircleRadius / 12

        outerCirclePath = Path().apply {
            addCircle(centerX, centerY, outerCircleRadius, Path.Direction.CW)
        }

        //val startX = 0F + 10.toPx() //left
        val startX = w/2F - outerCircleRadius //left
        //val topY = startX //top
        val topY = h/2F - outerCircleRadius //top
        //val endX = mSide.toFloat() - 10.toPx() //right
        val endX = centerX + outerCircleRadius //right
        //val bottomY = endX //bottom
        val bottomY =  centerY + outerCircleRadius //bottom
        rect1 = RectF(startX, topY, endX, bottomY)
    }

    private fun calculatePoints(){
        //firstPoint = PointF(orbitingCircleCenterX, orbitingCircleCenterY)
        //firstPoint = PointF(centerX + 0F, -1F)

        rotatingPoint = PointF(orbitingCircleCenterX, orbitingCircleCenterY)

        val totalSecs = inhaleSecs + exhaleSsecs + hold1Secs + hold2Secs
        val perimeter = 2 * PI * outerCircleRadius
        val arc1 = inhaleSecs / totalSecs * perimeter
        theta1 = (inhaleSecs / totalSecs.toFloat()) * 2 * PI - PI/2
        //log("th1 = " + Math.toDegrees(theta1.toDouble()))

        val p2X = (centerX + outerCircleRadius * cos(theta1)).toFloat()
        val p2Y = (centerY + outerCircleRadius * sin(theta1)).toFloat()
        //subtract - float startY = (float) (cy - radius * Math.cos(angle));

        secondPoint = PointF(p2X, p2Y)

        theta2 = (inhaleSecs + hold1Secs) / totalSecs.toDouble() * 2 * PI - PI/2
        //log("th2 = " + Math.toDegrees(theta2.toDouble()))

        val p3X = (centerX + outerCircleRadius * cos(theta2)).toFloat()
        val p3Y = (centerY + outerCircleRadius * sin(theta2)).toFloat()
        thirdPoint = PointF(p3X, p3Y)

        theta3 = ((inhaleSecs+hold1Secs+exhaleSsecs) / totalSecs.toDouble())*2*PI - PI/2
        //log("th3 = " + Math.toDegrees(theta3.toDouble()))

        val p4X = (centerX + outerCircleRadius * cos(theta3)).toFloat()
        val p4Y = (centerY + outerCircleRadius * sin(theta3)).toFloat()
        fourthPoint = PointF(p4X, p4Y)
    }

    private fun findNewCenter(){
        val radians = swipeAngle// * Math.PI / 180f
        orbitingCircleCenterX = (centerX + outerCircleRadius * cos(radians)).toFloat()
        orbitingCircleCenterY = (centerY + outerCircleRadius * sin(radians)).toFloat()
        rotatingPoint = PointF(orbitingCircleCenterX, orbitingCircleCenterY)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //canvas?.save()
        //canvas?.translate(0, yValue)
        canvas?.scale(xScale, yScale, centerX, centerY)
        /* draw whatever you want scaled at 0,0*/
        //canvas?.restore()

        //clip
        canvas?.clipPath(outerCirclePath)
        //outer circle
        canvas?.drawPath(outerCirclePath, outerCirclePaint)

        //inner filled circle
        canvas?.drawCircle(centerX, centerY, innerCircleRadius, innerCirclePaint)

        //orbiting smaller circle
        findNewCenter()
        calculatePoints()
        canvas?.drawArc(rect1, -90f, 90+Math.toDegrees(theta1).toFloat(), false, arcPaint)
        val stAng = Math.toDegrees(theta2).toFloat()
        val swAng = Math.toDegrees(theta3-theta2).toFloat()
        canvas?.drawArc(rect1, stAng, swAng, false, arcPaint)

        canvas?.drawCircle(firstPoint.x,
            firstPoint.y,
            orbitingCircleRadius,
            paint1)

        canvas?.drawCircle(secondPoint.x,
            secondPoint.y,
            orbitingCircleRadius,
            paint2)

        canvas?.drawCircle(thirdPoint.x,
            thirdPoint.y,
            orbitingCircleRadius,
            paint3)

        canvas?.drawCircle(fourthPoint.x,
            fourthPoint.y,
            orbitingCircleRadius,
            paint4)

        canvas?.drawCircle(rotatingPoint.x,
            rotatingPoint.y,
            orbitingCircleRadius,
            smallCirclePaint)

        //todo - outside of onDraw
        //use staticLayout
        textPaint.getTextBounds(centerText, 0, centerText.length, textRect)
        //val textWidth = textPaint.measureText(centerText)
        //val textHeight = textPaint.descent() - textPaint.ascent()
        val x1 = centerX
        val y1 = centerY + textRect.height()/2

        canvas?.drawText(centerText, x1, y1, textPaint)
    }

    fun toggle() {
        if (isScaling) {
            stop2()
        } else {
            start1()
        }
    }

    /**
     * Starts the scale up animation
     */
    fun start1() {
        isScaling = true
        animate().scaleXBy(scaleFactor)
            .scaleYBy(scaleFactor)
            .setDuration(duration.toLong())
            .setListener(scaleUpListener)
    }

    fun stop2() {
        isScaling = false
        clearAnimation()
    }

    private val scaleUpListener: AnimatorListener = object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            //we ignore heartBeating as we want to ensure the heart is reduced back to original size
            animate()
                .scaleXBy(reductionScaleFactor)
                .scaleYBy(reductionScaleFactor)
                .setDuration(duration.toLong())
                .setListener(scaleDownListener)
        }
        override fun onAnimationCancel(animation: Animator) {}
    }

    private val scaleDownListener: AnimatorListener = object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            if (isScaling) {
                //duration twice as long for the upscale
                animate().scaleXBy(scaleFactor)
                    .scaleYBy(scaleFactor)
                    .setDuration(duration * 2.toLong())
                    .setListener(scaleUpListener)
            }
        }
        override fun onAnimationCancel(animation: Animator) {}
    }

    fun log(msg : String) = Log.d("durga", "BCV- $msg")

    fun pxToDp(px:Int)  = (px / Resources.getSystem().displayMetrics.density).toInt()

    fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

    interface RotationListener{
        fun onInhaleStart()
        fun onHold1Start()
        fun onExhaleStart()
        fun onHold2Start()
    }

}

fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Float.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Float.toPx() = this * Resources.getSystem().displayMetrics.density
fun Int.toPx() = this * Resources.getSystem().displayMetrics.density


private class RepeatRunnable(val view: View) : Runnable {
    private val millis = 10L
    override fun run() {
        view.invalidate();
        view.postDelayed(this, millis);
    }
}
