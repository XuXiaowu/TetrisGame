package com.mirahome.tetrisgame.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import java.util.*

/**
 * Created by xuxiaowu on 2018/7/6.
 */
class GemePreviewView : View {

    val GRID_LINE_WIDTH = 1
    var MARGIN = 10

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mGridWidth: Int = 0 //格子长度

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG) //画笔
    private var mFrameRect: Rect? = null

    private var mTempBlock: LinkedList<Point> = LinkedList()

    public constructor(context: Context) : super(context) {
        initData()
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initData()
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initData()
    }

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWidth = (wm.defaultDisplay.width * 0.3).toInt()
        mHeight = 400
        mGridWidth = (mWidth - MARGIN) / 5

        mPaint.setStrokeWidth(GRID_LINE_WIDTH.toFloat())
        mPaint.setColor(Color.parseColor("#E91E63"))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(resolveMeasure(widthMeasureSpec, mWidth!!), resolveMeasure(heightMeasureSpec, mHeight!!))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawFrame(canvas!!)
        drawBlock(canvas)
    }

    /**
     * 根据传入的值进行测量
     * @param measureSpec
     * @param defaultSize
     */
    private fun resolveMeasure(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
        val specSize = View.MeasureSpec.getSize(measureSpec)
        when (View.MeasureSpec.getMode(measureSpec)) {
            View.MeasureSpec.UNSPECIFIED -> result = defaultSize

            View.MeasureSpec.AT_MOST -> result = Math.min(specSize, defaultSize) //设置warp_content时设置默认值

            View.MeasureSpec.EXACTLY -> {
            }

            else -> result = defaultSize
        }//设置math_parent 和设置了固定宽高值
        return result
    }

    private fun initData() {

    }

    private fun drawFrame(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mFrameRect = Rect(0, MARGIN, mWidth - MARGIN, mHeight)
        canvas.drawRect(mFrameRect, mPaint)
    }

    /**
     * 绘制方块
     */
    private fun drawBlock(canvas: Canvas) {
        for (it in mTempBlock.iterator()) {
            val block = Rect()
            block.left = mGridWidth * it.x + 1
            block.right = block.left + mGridWidth - 1
            block.top = mGridWidth * it.y + 1
            block.bottom = block.top + mGridWidth - 1
            canvas.drawRect(block, mPaint)
        }
    }

    private fun updateTempBlock(tempBlockArray: Array<Array<Int>>) {
        mTempBlock.clear()
        for (i in tempBlockArray.indices) {
            val array = tempBlockArray[i]
            for (j in array.indices) {
                val value = array[j]
                if (value != 0) {
                    var point = Point(j + 1, i + 1)
                    mTempBlock.add(point)
                }
            }
        }
    }

    fun updateBlock(tempBlockArray: Array<Array<Int>>) {
        updateTempBlock(tempBlockArray)
        invalidate()
    }

}