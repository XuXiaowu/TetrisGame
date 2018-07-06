package com.mirahome.tetrisgame.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.mirahome.tetrisgame.constant.BlockType
import fuckermonkey.snackgame.ui.constant.Constants
import fuckermonkey.snackgame.ui.constant.Direction
import java.util.*

/**
 * Created by xuxiaowu on 2017/6/12.
 */
class GameView : View {

    val TAG = "GameView"

    val GRID_LINE_WIDTH = 1
    val DEFAULT_MARGIN = 20

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mGridWidth: Int = 0
    private var mMargin: Int = 0
    private var mDownIndex = 0
    private var mBlockMaxY = 0 //方块最大的Y坐标
    //    private var mGridNum = Constants.getInstance().SIMPLE_GRID_NUM
    private var mGirdXNum = 0
    private var mGirdYNum = 0
    private var mBottomLayerY = 0 //方块的底层Y坐标
    private var mTempBlockHorizontalOffset = 0 //方块水平方向的偏移量
    private var mRemoveLineCount = 0 //消除的行数

    private var mIsAutoAvoid = false
    private var mIsAutoEat = false

    private var mPaint: Paint? = null //画笔
//    private var mFoodPaint: Paint? = null //食物的画笔

    private var mMoveDirection = Direction.RIGHT

    private var mGameStatusListener: GameStatusListener? = null
    private var mTempBlock: LinkedList<Point> = LinkedList()
    private var mPileBlock: LinkedList<Point> = LinkedList() //堆积的方块
    private var mTempBlockArray: Array<Array<Int>> = Array(3) { Array(3, { 0 }) }
    private var mTotalBlockArray: Array<Array<Int>> = Array(mGirdYNum) { Array(mGirdXNum, { 0 }) }
    private val mRandom = Random()
    private var mCurrentBlockType: BlockType? = null //现在的方块类型
    private var mNextBlockType: BlockType? = null //下一个方块类型

    public constructor(context: Context) : super(context) {
        initData()
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initData()
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initData()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(resolveMeasure(widthMeasureSpec, mWidth!!), resolveMeasure(heightMeasureSpec, mHeight!!))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBlock(canvas!!)
    }

    fun setGameStatusListener(listener: GameStatusListener) {
        mGameStatusListener = listener
    }

    fun setLevel(level: Int) {
//        when (level) {
//            0 -> {
//                mGridNum = Constants.getInstance().SIMPLE_GRID_NUM
//            }
//            1 -> {
//                mGridNum = Constants.getInstance().SIMPLE_GRID_NUM
//            }
//            2 -> {
//                mGridNum = Constants.getInstance().SIMPLE_GRID_NUM
//            }
//        }
//        mGridWidth = (mWidth!! - DEFAULT_MARGIN) / mGridNum
        invalidate()
    }

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWidth = wm.defaultDisplay.width
        mHeight = mWidth

//        mGridWidth = (mWidth!! - DEFAULT_MARGIN) / mGridNum
//        mMargin = (mWidth!! - mGridWidth!! * mGridNum) / 2

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.setStrokeWidth(GRID_LINE_WIDTH.toFloat())
        mPaint?.setColor(Color.parseColor("#4CAF50"))

//        mGridNum = Constants.getInstance().SIMPLE_GRID_NUM
        mGirdYNum = Constants.getInstance().SIMPLE_GRID_NUM
        mGirdXNum = (mGirdYNum * 0.7).toInt()
        mBottomLayerY = mGirdYNum - 1
        mTempBlockHorizontalOffset = mGirdXNum / 2

        mGridWidth = (mWidth!! - DEFAULT_MARGIN) / mGirdYNum
        mMargin = (mWidth!! - mGridWidth!! * mGirdYNum) / 2
        mTotalBlockArray = Array(mGirdYNum) { Array(mGirdXNum, { 0 }) }
    }

    private fun initData() {
        initBlockType()
        initTotalBlock()
        initTempBlock()
        updateTempBlock()
        updateBlockMaxY()

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

    /**
     * 绘制方块
     */
    private fun drawBlock(canvas: Canvas) {
        for (it in mTempBlock.iterator()) {
            val block = Rect()
            block.left = mGridWidth * it.x + mMargin + 1
            block.right = block.left + mGridWidth - 1
            block.top = mGridWidth * it.y + mMargin + 1
            block.bottom = block.top + mGridWidth - 1
            canvas.drawRect(block, mPaint)
        }

        for (it in mPileBlock.iterator()) {
            val block = Rect()
            block.left = mGridWidth * it.x + mMargin + 1
            block.right = block.left + mGridWidth - 1
            block.top = mGridWidth * it.y + mMargin + 1
            block.bottom = block.top + mGridWidth - 1
            canvas.drawRect(block, mPaint)
        }
    }

    private fun initBlockType() {
        val currentBlockIndex = mRandom.nextInt(BlockType.values().size)
        val nextBlockIndex = mRandom.nextInt(BlockType.values().size)
        mCurrentBlockType = BlockType.values().get(currentBlockIndex)
        mNextBlockType = BlockType.values().get(nextBlockIndex)
    }

    private fun updateBlockType() {
        mCurrentBlockType = mNextBlockType
        val nextBlockIndex = mRandom.nextInt(BlockType.values().size)
        mNextBlockType = BlockType.values().get(nextBlockIndex)
    }

    private fun initTempBlock() {
        var index = mRandom.nextInt(BlockType.values().size)
        var type = BlockType.values().get(index)
//        type = BlockType.TYPE_I
        when (type) {
            BlockType.TYPE_S_L -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 1
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_S_R -> {
                mTempBlockArray[0][0] = 0
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 1
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 1
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_L_L -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 1
                mTempBlockArray[1][1] = 0
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 1
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_L_R -> {
                mTempBlockArray[0][0] = 0
                mTempBlockArray[0][1] = 0
                mTempBlockArray[0][2] = 1
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 0
                mTempBlockArray[1][2] = 1
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 1
            }
            BlockType.TYPE_T -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 1
                mTempBlockArray[0][2] = 1
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 0
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_O -> {
                mTempBlockArray[0][0] = 1
                mTempBlockArray[0][1] = 1
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 1
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 0
                mTempBlockArray[2][2] = 0
            }
            BlockType.TYPE_I -> {
                mTempBlockArray[0][0] = 0
                mTempBlockArray[0][1] = 1
                mTempBlockArray[0][2] = 0
                mTempBlockArray[1][0] = 0
                mTempBlockArray[1][1] = 1
                mTempBlockArray[1][2] = 0
                mTempBlockArray[2][0] = 0
                mTempBlockArray[2][1] = 1
                mTempBlockArray[2][2] = 0
            }
        }

        mBlockMaxY = 0
        mDownIndex = 0
        mTempBlockHorizontalOffset = mGirdXNum / 2
    }

    /**
     * 初始化全部方块矩阵内容
     */
    private fun initTotalBlock() {
        for (i in mTotalBlockArray.indices) {
            val array = mTotalBlockArray[i]
            for (j in array.indices) {
                mTotalBlockArray[i][j] = 0
            }
        }
    }

    private fun updateTempBlock() {
        mTempBlock.clear()
        for (i in mTempBlockArray.indices) {
            val array = mTempBlockArray[i]
            for (j in array.indices) {
                val value = array[j]
                if (value != 0) {
                    var point = Point(j + mTempBlockHorizontalOffset, i + mDownIndex)
                    mTempBlock.add(point)
                }
            }
        }
    }

    /**
     * 旋转
     */
    private fun turn() {
        val temp: Array<Array<Int>> = Array(3) { Array<Int>(3, { 0 }) }
        val len = temp.size
        for (i in mTempBlockArray.indices) {
            val array = mTempBlockArray[i]
            for (j in array.indices) {
                temp[j][len - 1 - i] = mTempBlockArray[i][j]
            }
        }

        for (i in mTempBlockArray.indices) {
            val array = mTempBlockArray[i]
            for (j in array.indices) {
                mTempBlockArray[i][j] = temp[i][j]
            }
        }
    }

    /**
     * 检查方块是否在边界上
     */
    private fun checkAtBorder(isLeftBorder: Boolean): Boolean {
        val border = if (isLeftBorder) 0 else mGirdXNum - 1
        for (point in mTempBlock) {
            if (point.x == border) {
                return true
            }
        }
        return false
    }

    /**
     * 旋转方块
     */
    fun turnBlock() {
        turn()
        updateTempBlock()
        updateBlockMaxY()
        invalidate()
    }

    /**
     * 左移
     */
    fun moveLeft() {
        if (checkAtBorder(true)) return
        for (point in mTempBlock) {
            point.x--
        }
        mTempBlockHorizontalOffset--
        invalidate()
    }

    /**
     * 左移
     */
    fun moveRight() {
        if (checkAtBorder(false)) return
        for (point in mTempBlock) {
            point.x++
        }
        mTempBlockHorizontalOffset++
        invalidate()
    }

    /**
     * 下降方块
     */
    fun downBlock() {
        mBlockMaxY++
        mDownIndex++


        for (point in mTempBlock) {
            point.y++
        }

//        if (mBlockMaxY == mBottomLayerY) {
        val isToBottom = checkToBottom()
        if (isToBottom) {
            val addBlock = deepCopyTempBlock()
            mPileBlock.addAll(addBlock)

            for (point in addBlock) {
                mTotalBlockArray[point.y][point.x] = 1
            }

            initTempBlock()
            updateTempBlock()
            updateBlockMaxY()
            updateCallback()

            checkRemoveBottomLine()

            if (mGameStatusListener != null) {
                mGameStatusListener!!.updateNextBlock(mTempBlockArray)
            }
        }

        invalidate()
    }

    /**
     * 检查是否充满最底部的一行
     */
    private fun checkFullBottomLine(): Boolean {
        val array = mTotalBlockArray[mGirdYNum - 1]
        for (i in array) {
            if (i == 0) {
                return false
            }
        }
        return true
    }

    /**
     * 检查是否要消除最底部的一行
     */
    private fun checkRemoveBottomLine() {
        if (checkFullBottomLine()) {
            val array = mTotalBlockArray[mGirdYNum - 1]
            for (i in array.indices) {
                array[i] = 0
            }

            val removeBlockPointList: LinkedList<Point> = LinkedList() //要删除的point
            for (point in mPileBlock) {
                if (point.y == mBottomLayerY) {
                    removeBlockPointList.add(point)
                }
            }
            mPileBlock.removeAll(removeBlockPointList) //删除最底部的一行的point

            //剩下的point全部下移一格
            for (point in mPileBlock) {
                point.y++
            }

            //mTotalBlockArray全部下移一行
            val len = mTotalBlockArray.size - 1
            for (i in len downTo 1) {
                mTotalBlockArray[i] = mTotalBlockArray[i - 1]
            }

            mRemoveLineCount++
            mGameStatusListener!!.onGetScore(mRemoveLineCount * mGirdXNum)
            checkRemoveBottomLine() //递归调用
        }
    }

    private fun updateCallback() {
        mGameStatusListener?.onBlockToBottom()
    }

    /**
     * 检查方块是否到达底部
     *
     * @return true到达底部 false未到达底部
     */
    private fun checkToBottom(): Boolean {
        var isThreeLine = false //方块坐标是否填充三行
        for (i in mTempBlockArray[2]) {
            if (i != 0) {
                isThreeLine = true
                break
            }
        }

        /** 检查第一行坐标 **/
        val bottomLineIndex = if (isThreeLine) 2 else 1 //方块的最底层index
        var tempBlockBottomArray = mTempBlockArray[bottomLineIndex] //mTempBlockArray最底下的一行
        var bottomBlockSize = 0 //下行方块数
        //计算下行方块数
        for (item in tempBlockBottomArray) {
            if (item != 0) bottomBlockSize++
        }

        val checkBlockPointList: LinkedList<Point> = LinkedList() //要检查的方块行坐标集合
        var len = mTempBlock.size - 1
        var star = len - bottomBlockSize + 1
        for (index in star..len) {
            checkBlockPointList.add(mTempBlock.get(index)) //填充方块下行的point
        }
        Log.e(TAG, checkBlockPointList.toString())

        var hasEnoughSpace = true
        val index = if (mBlockMaxY + 1 == mTotalBlockArray.size) mTotalBlockArray.size - 1 else mBlockMaxY + 1 //计算方块底行的index
        val firstArray = mTotalBlockArray[index] //第一列要检查的数组
        val secondArray = mTotalBlockArray[index - 1] //第二列要检查的数组
        //扫描方块底行的下一行空间是否被占用
        for (point in checkBlockPointList) {
            if (firstArray[point.x] == 1) {
                hasEnoughSpace = false
                break
            }
        }

        /** 检查第二行坐标 **/
        if (hasEnoughSpace) {
            var middleLayerBlockSize = 0 //block中行的方块数
            checkBlockPointList.clear() //清空数据
            val tempBlockMiddleArray = mTempBlockArray[bottomLineIndex - 1]  //方块最底中行的坐标集合
            //计算下行方块数
            for (item in tempBlockMiddleArray) {
                if (item != 0) middleLayerBlockSize++
            }

            len = mTempBlock.size - bottomBlockSize - 1
            star = len - middleLayerBlockSize + 1
            for (index in star..len) {
                checkBlockPointList.add(mTempBlock.get(index)) //填充方块中行的point
            }

            //扫描方块底行的下一行空间是否被占用
            for (point in checkBlockPointList) {
                if (secondArray[point.x] == 1) {
                    return true
                }
            }
        }

        if (mBlockMaxY == mBottomLayerY && hasEnoughSpace) return true //如果到底部返回true
        return !hasEnoughSpace
    }

    private fun deepCopyTempBlock(): LinkedList<Point> {
        val result: LinkedList<Point> = LinkedList()
        for (point in mTempBlock) {
            val tempPoint = Point(point.x, point.y)
            result.add(tempPoint)
        }
        return result
    }

    private fun updateBlockMaxY() {
        mBlockMaxY = 0
        for (point in mTempBlock) {
            if (point.y > mBlockMaxY) mBlockMaxY = point.y
        }
    }

}
