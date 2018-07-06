package fuckermonkey.snackgame.ui.constant

/**
 * Created by xuxiaowu on 2017/6/16.
 */
class Constants {

    val SIMPLE_GRID_NUM = 20 //15 30
    val NORMAL_GRID_NUM = 50 //网格数
    val HARD_GRID_NUM = 70 //网格数
    val BLOCK_TYPE_SIZE = 5 //方块类型个数

    companion object {
        private val constants = Constants()

        @JvmStatic
        fun getInstance() = constants
    }
}