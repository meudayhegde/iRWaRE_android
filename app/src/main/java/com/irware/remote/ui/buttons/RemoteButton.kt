package com.irware.remote.ui.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.irware.remote.MainActivity
import com.irware.remote.R
import com.irware.remote.holders.ButtonProperties
import com.irware.remote.holders.OnModificationListener

@SuppressLint("ViewConstructor")
class RemoteButton(context: Context?, properties:ButtonProperties) : Button(context) {
    private var properties:ButtonProperties?=null
    init {
        this.properties=properties
        setTextColor(Color.WHITE)
        setButtonProperties()
        gravity= Gravity.CENTER
        setTextSize(Button.AUTO_SIZE_TEXT_TYPE_UNIFORM, 12F)
        properties.setOnModificationListener(object:OnModificationListener{
            override fun onTypeModified() {
                setType(type = properties.getAlignType())
            }

            override fun onIconModified() {

            }

            override fun onTextModified() {
                text = properties.getText()
            }

            override fun onColorModified() {
                setColorMode(bg_type = properties.getColorMode())
            }

            override fun onIrModified() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPositionModified() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    fun setButtonProperties(){
        setType(type = properties?.getAlignType()!!)
        text = properties?.getText()!!
        setColorMode(bg_type = properties?.getColorMode()!!)
    }

    fun getProperties():ButtonProperties{
        return properties!!
    }

    fun setColorMode(bg_type:Int){
        var bg_res=0
        when(bg_type){
            COLOR_GREY->bg_res = R.drawable.round_btn_bg_grey
            COLOR_RED->bg_res=R.drawable.round_btn_bg_red
            COLOR_GREEN->bg_res=R.drawable.round_btn_bg_green
            COLOR_BLUE->bg_res=R.drawable.round_btn_bg_blue
            COLOR_YELLOW->bg_res=R.drawable.round_btn_bg_yellow
        }
        setBackgroundResource(bg_res)
    }

    fun setIcon(drawable_resid:Int){
        setCompoundDrawablesWithIntrinsicBounds(drawable_resid,0,0,0)
    }

    fun setType(type:Int){
        when(type){
            TYPE_RECT_HOR -> {
                    layoutParams= LinearLayout.LayoutParams(BTN_WIDTH-(2* BTN_PADDING), MIN_HIGHT-(2* BTN_PADDING))
            }
            TYPE_ROUND_MINI -> {
                layoutParams= LinearLayout.LayoutParams(MIN_HIGHT-(2* BTN_PADDING),MIN_HIGHT-(2* BTN_PADDING))
            }
            TYPE_ROUND_MEDIUM -> {
                layoutParams= LinearLayout.LayoutParams(BTN_WIDTH-(2* BTN_PADDING),BTN_WIDTH-(2* BTN_PADDING))
            }
            TYPE_RECT_VER -> {
                layoutParams= ViewGroup.LayoutParams(MIN_HIGHT-(2* BTN_PADDING),BTN_WIDTH-(2* BTN_PADDING))
            }
        }
    }

    companion object{
        const val COLOR_GREY=0
        const val COLOR_RED=1
        const val COLOR_GREEN=2
        const val COLOR_BLUE=3
        const val COLOR_YELLOW=4

        const val TYPE_ROUND_MINI=0
        const val TYPE_RECT_HOR=1
        const val TYPE_RECT_VER=2
        const val TYPE_ROUND_MEDIUM=3

        const val BTN_WIDTH=160
        const val MIN_HIGHT=80
        const val BTN_PADDING=10
    }
}