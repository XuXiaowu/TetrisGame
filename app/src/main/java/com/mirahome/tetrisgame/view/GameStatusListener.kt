package com.mirahome.tetrisgame.view

/**
 * Created by xuxiaowu on 2017/6/13.
 */
interface GameStatusListener {

    fun onGameOver()
    fun onBlockToBottom()
    fun onGetScore(score: Int)
    fun updateNextBlock(nextBlockArray: Array<Array<Int>>)
}