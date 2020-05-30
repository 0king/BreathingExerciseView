package com.example.breathingexerciseview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Color
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class BreathingCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //main inner big circle
    lateinit var innerCircle : Circle
    lateinit var outerCircle : Circle
    lateinit var point1 : Circle
    lateinit var point2 : Circle
    lateinit var point3 : Circle
    lateinit var point4 : Circle

    //state
    enum class STATE{
        Inhaling, InhalePause, Exhaling, ExhalePause
    }
    var isPlaying = false
    var isStarted = false

    var centerY:Float = 0f
    var centerX:Float = 0f
    var innerCircleRadius:Float = 0f
    var innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL_AND_STROKE
    }

    //outer cirle
    var outerCircleRadius : Float = 0f
    var outerCircleStrokeWidth : Float = 7.toPx()
    val outerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = outerCircleStrokeWidth
    }
    lateinit var outerCirclePath:Path

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
    var inhalePauseSecs = 4
    var exhaleSsecs = 5
    var exhalePauseSecs = 4

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

    lateinit var enclosingRect : RectF

    val arcPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = outerCircleStrokeWidth
    }
    val arcPaint2 = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = outerCircleStrokeWidth
    }

    lateinit var valueAnimator : ValueAnimator

    //scaling
    var arc1Max = 0F
    var arc2Max = 0F
    var arc3Max = 0F
    val MAX_SCALE = 1.4F
    var xScale = 0F
    var yScale = 0F
    var maxScale = 0F
    val initialScale = 1 / MAX_SCALE
    val availableScale = MAX_SCALE - 1.0F

    //sounds
    var isSound1Playing = false
    var isSound2Playing = false

    var rotationListener : RotationListener? = null

    init {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        valueAnimator = ValueAnimator.ofFloat(0f, 1F).apply {
            addUpdateListener {
                val fraction = animatedValue as Float
                swipeAngle = (2 * PI * fraction).toFloat() - (PI/2).toFloat()
                when(swipeAngle){
                    in 0.0..theta1 -> {
                        centerText = "breathe in"
                        updateScaleUp(fraction)
                    }
                    in -PI/2..0.0 -> {
                        centerText = "breathe in"
                        updateScaleUp(fraction)
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
                        updateScaleDown(fraction)
                    }
                    else /* inside fourth quad */ -> centerText = "hold"
                }
                invalidate()
                //postInvalidateOnAnimation()
            }
            duration = (inhaleSecs + inhalePauseSecs + exhaleSsecs + exhalePauseSecs) * 1000L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun updateScaleUp(fraction:Float){
        val scale = initialScale + (availableScale / arc1Max) * fraction
        xScale = scale
        yScale = scale
        maxScale = scale
    }

    private fun updateScaleDown(fraction: Float){
        val f_diffMax = arc3Max - arc2Max
        val scale = maxScale - (availableScale / f_diffMax) * (fraction - arc2Max)
        xScale = scale
        yScale = scale
    }

    private fun findFractions(){
        val total = inhaleSecs.toFloat() + inhalePauseSecs + exhaleSsecs + exhalePauseSecs
        arc1Max = inhaleSecs / total
        arc2Max = (inhaleSecs + inhalePauseSecs) / total
        arc3Max = (inhaleSecs+inhalePauseSecs+exhaleSsecs) / total
    }

    fun start(){
        isPlaying = true
        isStarted = true
        findFractions()
        valueAnimator.start()
    }

    fun resume(){
        if (isStarted) {
            findFractions()
            valueAnimator.resume()
        }else{
            isPlaying = true
            isStarted = true
            start()
        }
    }

    fun stop(){
        isPlaying = false
        isStarted = false
        valueAnimator.cancel()
    }

    fun pause(){
        isPlaying = false
        isStarted = true
        valueAnimator.pause()
    }

    fun restart(){
        valueAnimator.cancel()
        start()
    }

    override fun onDetachedFromWindow() {
        valueAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w / 2f
        centerY = h / 2f
        val side = Math.min(w, h)
        val availableRadius = side/2F
        outerCircleRadius = availableRadius / MAX_SCALE
        val gap = outerCircleRadius / 10
        innerCircleRadius = outerCircleRadius - gap

        xScale = 1 / MAX_SCALE
        yScale = xScale

        //smaller circle
        orbitingCircleCenterX = centerX
        orbitingCircleCenterY = h/2 - outerCircleRadius

        firstPoint = PointF(w/2F, h/2 - outerCircleRadius)

        orbitingCircleRadius = innerCircleRadius / 12

        outerCirclePath = Path().apply {
            addCircle(centerX, centerY, outerCircleRadius, Path.Direction.CW)
        }

        val startX = w/2F - outerCircleRadius //left
        val topY = h/2F - outerCircleRadius //top
        val endX = centerX + outerCircleRadius //right
        val bottomY =  centerY + outerCircleRadius //bottom
        enclosingRect = RectF(startX, topY, endX, bottomY)
        val shader = LinearGradient(startX,topY,endX,bottomY,Color.GRAY, Color.BLUE, Shader.TileMode.CLAMP)
        innerCirclePaint.shader = shader
    }

    private fun calculatePoints(){

        rotatingPoint = PointF(orbitingCircleCenterX, orbitingCircleCenterY)

        val totalSecs = inhaleSecs + exhaleSsecs + inhalePauseSecs + exhalePauseSecs
        val perimeter = 2 * PI * outerCircleRadius
        val arc1 = inhaleSecs / totalSecs * perimeter
        theta1 = (inhaleSecs / totalSecs.toFloat()) * 2 * PI - PI/2

        val p2X = (centerX + outerCircleRadius * cos(theta1)).toFloat()
        val p2Y = (centerY + outerCircleRadius * sin(theta1)).toFloat()

        secondPoint = PointF(p2X, p2Y)

        theta2 = (inhaleSecs + inhalePauseSecs) / totalSecs.toDouble() * 2 * PI - PI/2

        val p3X = (centerX + outerCircleRadius * cos(theta2)).toFloat()
        val p3Y = (centerY + outerCircleRadius * sin(theta2)).toFloat()
        thirdPoint = PointF(p3X, p3Y)

        theta3 = ((inhaleSecs+inhalePauseSecs+exhaleSsecs) / totalSecs.toDouble())*2*PI - PI/2

        val p4X = (centerX + outerCircleRadius * cos(theta3)).toFloat()
        val p4Y = (centerY + outerCircleRadius * sin(theta3)).toFloat()
        fourthPoint = PointF(p4X, p4Y)
    }

    private fun findNewCenter(){
        val radians = swipeAngle
        orbitingCircleCenterX = (centerX + outerCircleRadius * cos(radians))
        orbitingCircleCenterY = (centerY + outerCircleRadius * sin(radians))
        rotatingPoint = PointF(orbitingCircleCenterX, orbitingCircleCenterY)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.scale(xScale, yScale, centerX, centerY)
        canvas?.clipPath(outerCirclePath)
        canvas?.drawPath(outerCirclePath, outerCirclePaint)
        canvas?.drawCircle(centerX, centerY, innerCircleRadius, innerCirclePaint)
        findNewCenter()
        calculatePoints()
        canvas?.drawArc(enclosingRect, -90f, 90+Math.toDegrees(theta1).toFloat(), false, arcPaint)
        val stAng = Math.toDegrees(theta2).toFloat()
        val swAng = Math.toDegrees(theta3-theta2).toFloat()
        canvas?.drawArc(enclosingRect, stAng, swAng, false, arcPaint)
        //todo - draw four arcs

        if (inhaleSecs>0)
            canvas?.drawCircle(firstPoint.x, firstPoint.y, orbitingCircleRadius, paint1)
        if (inhalePauseSecs>0)
            canvas?.drawCircle(secondPoint.x, secondPoint.y, orbitingCircleRadius, paint2)
        if (exhaleSsecs>0)
            canvas?.drawCircle(thirdPoint.x, thirdPoint.y, orbitingCircleRadius, paint3)
        if (exhalePauseSecs>0)
            canvas?.drawCircle(fourthPoint.x, fourthPoint.y, orbitingCircleRadius, paint4)

        canvas?.drawCircle(rotatingPoint.x, rotatingPoint.y, orbitingCircleRadius, smallCirclePaint)

        //todo - outside of onDraw
        //use staticLayout
        textPaint.getTextBounds(centerText, 0, centerText.length, textRect)
        val x1 = centerX
        val y1 = centerY + textRect.height()/2
        canvas?.drawText(centerText, x1, y1, textPaint)
    }

    interface RotationListener{
        fun onInhaleStart()
        fun onHold1Start()
        fun onExhaleStart()
        fun onHold2Start()
    }

    fun log(msg : String) = Log.d("durga", "BCV- $msg")

}


