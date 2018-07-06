package com.mirahome.tetrisgame.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.mirahome.tetrisgame.R
import com.mirahome.tetrisgame.view.GameStatusListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, GameStatusListener {

    val WHAT_UPDATE_UI = 1

    var mRunFlag = true
    var mSleepTime = 500L
    var mSaveSleepTime = 500L
    var mSpeed = 1
    var mScore = 0
    var mSelectItem = 0

    var mLevelDialog: AlertDialog.Builder? = null
    var mSettingDialog: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        game_view.setGameStatusListener(this)
        play_btn.setOnClickListener(this)
//        faster_btn.setOnTouchListener(this)
//        slowly_btn.setOnTouchListener(this)
        stop_btn.setOnClickListener(this)
        level_btn.setOnClickListener(this)
        setting_btn.setOnClickListener(this)
        left_btn.setOnClickListener(this)
        right_btn.setOnClickListener(this)
        top_btn.setOnClickListener(this)
        bottom_btn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.play_btn -> {
                val btn = view as Button
                when (btn.text) {
                    getString(R.string.play) -> {
                        play_btn.setText(R.string.pause)
                        stop_btn.isEnabled = true
//                        handler.removeMessages(WHAT_UPDATE_UI)
                        handler.sendEmptyMessageDelayed(WHAT_UPDATE_UI, mSleepTime)
//                        GameThread().start()
                    }
                    getString(R.string.restart) -> {
//                        mRunFlag = true
//                        mScore = 0
//                        mSpeed = 0
//                        mSaveSleepTime = 500L
//                        mSleepTime = 500L
//                        setGameData()
//                        game_view.setMoveDirection(Direction.RIGHT)
//                        game_view.initData()
//                        game_view.invalidate()
//                        GameThread().start()
//                        play_btn.setText(R.string.pause)
//                        stop_btn.isEnabled = true
//                        setDirectionBtnEnable(true)
                    }
                    getString(R.string.pause) -> {
                        mRunFlag = false
                        handler.removeMessages(WHAT_UPDATE_UI)
                        play_btn.setText(R.string.resume)
                    }
                    getString(R.string.resume) -> {
                        mRunFlag = true
//                        GameThread().start()
                        handler.sendEmptyMessageDelayed(WHAT_UPDATE_UI, mSleepTime)
                        play_btn.setText(R.string.pause)
                    }
                }
            }
            R.id.stop_btn -> {
                mRunFlag = false
                play_btn.setText(R.string.restart)
                stop_btn.isEnabled = false
            }
            R.id.level_btn -> {
//                if (mLevelDialog == null) {
//                    mLevelDialog = AlertDialog.Builder(this)
//                    mLevelDialog?.setIcon(R.mipmap.ic_launcher)
//                    mLevelDialog?.setTitle("Selection difficulty")
//                    mLevelDialog?.setSingleChoiceItems(R.array.difficulty_array, mSelectItem, mItemClickListener)
//                    mLevelDialog?.setPositiveButton(R.string.confirm, mPositiveButtonClickListener)
//                    mLevelDialog?.setNegativeButton(R.string.cancel, null)
//                    mLevelDialog?.show()
//                } else {
//                    mLevelDialog?.show()
//                }
            }
            R.id.setting_btn -> {
//                val view = LayoutInflater.from(this).inflate(R.layout.setting_view, null)
//                val autoAvoidSwitch = view.findViewById(R.id.auto_avoid_switch) as Switch
//                val autoEatSwitch = view.findViewById(R.id.auto_eat_switch) as Switch
//                autoAvoidSwitch.isChecked = mIsAutoAvoid
//                autoEatSwitch.isChecked = mIsAutoEat
//                autoAvoidSwitch.setOnCheckedChangeListener(this)
//                autoEatSwitch.setOnCheckedChangeListener(this)
                mSettingDialog = AlertDialog.Builder(this)
                mSettingDialog?.setIcon(R.mipmap.ic_launcher)
                mSettingDialog?.setTitle("Setting")
                mSettingDialog?.setView(view)
                mSettingDialog?.show()
            }
            R.id.left_btn -> {
                game_view.moveLeft()
            }
            R.id.right_btn -> {
                game_view.moveRight()
            }
            R.id.top_btn -> {
                game_view.turnBlock()
            }
            R.id.bottom_btn -> {
                mSleepTime = 50
                handler.removeMessages(WHAT_UPDATE_UI)
                handler.sendEmptyMessage(WHAT_UPDATE_UI)
            }
        }
    }

    override fun onGameOver() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBlockToBottom() {
        mSleepTime = 500
    }

    override fun onGetScore(score: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateNextBlock(nextBlockArray: Array<Array<Int>>) {
        preview_view.updateBlock(nextBlockArray)
    }

    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg!!.what) {
                WHAT_UPDATE_UI -> {
                    game_view.downBlock()
                    sendEmptyMessageDelayed(WHAT_UPDATE_UI, mSleepTime)
                }
            }
        }
    }

    inner class GameThread : Thread() {
        override fun run() {
            while (mRunFlag) {
                sleep(mSleepTime!!)
                handler.sendEmptyMessage(WHAT_UPDATE_UI)
            }
        }
    }
}
