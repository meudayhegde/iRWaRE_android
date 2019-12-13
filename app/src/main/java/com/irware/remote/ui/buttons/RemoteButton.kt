package com.irware.remote.ui.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import com.irware.remote.MainActivity
import com.irware.remote.holders.ButtonProperties
import com.irware.remote.holders.OnModificationListener


class RemoteButton : Button {
    private var properties:ButtonProperties?=null
    private val dr = GradientDrawable()

    constructor(context:Context):super(context){
        visibility = View.GONE
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, int:Int) : super(context, attrs,int)

    fun initialize(properties:ButtonProperties?){
        this.properties=properties
        if(properties == null) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE
        setTextColor(Color.WHITE)
        setButtonProperties(properties)
        properties.setOnModificationListener(object:OnModificationListener{
            override fun onTextColorChanged() {
                setTextColor(properties.textColor)
                onIconModified()
            }

            override fun onTypeModified() {
                setType(type = properties.iconType)
                onIconModified()
            }

            override fun onIconModified() {
                setIcon(MainActivity.iconDrawableList[properties.icon])
            }

            override fun onTextModified() {
                text = properties.text
            }

            override fun onColorModified() {
                dr.setColor(properties.color)
            }

            override fun onIrModified() {

            }

            override fun onPositionModified() {

            }

        })
    }

    @SuppressLint("InlinedApi", "ResourceType")
    fun setButtonProperties(btnProperties:ButtonProperties){
        properties = btnProperties
        setType(type = properties?.iconType!!)
        text = properties?.text
        gravity = Gravity.CENTER
        setTextSize(AUTO_SIZE_TEXT_TYPE_UNIFORM, 12F)
        dr.setStroke(2,MainActivity.colorOnBackground)
        dr.cornerRadius = 100F
        dr.setColor(properties?.color!!)
        background = dr
        setTextColor(properties!!.textColor)
        setIcon(MainActivity.iconDrawableList[properties!!.icon])
    }

    fun getProperties():ButtonProperties{
        return properties!!
    }


    fun setIcon(drawable_resid:Int){
        if(drawable_resid != MainActivity.iconDrawableList[0]){
            var drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) context.getDrawable(drawable_resid)
            else with(context) {
                @Suppress("DEPRECATION")
                resources.getDrawable(drawable_resid)
            }
            drawable = drawable?.mutate()
            DrawableCompat.setTint(drawable!!,properties!!.textColor)
            if(properties!!.iconType == TYPE_RECT_VER) {
                setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
                setPadding(0,0,0,0)
            }
            else {
                setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
                setPadding(10,0,0,0)
            }
        }else{
            setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
        }

    }

    fun setType(type:Int){
        when(type){
            TYPE_RECT_HOR -> {
                layoutParams= LinearLayout.LayoutParams(BTN_WIDTH, MIN_HEIGHT)
            }
            TYPE_ROUND_MINI -> {
                layoutParams= LinearLayout.LayoutParams(MIN_HEIGHT,MIN_HEIGHT)
            }
            TYPE_ROUND_MEDIUM -> {
                layoutParams= LinearLayout.LayoutParams(BTN_WIDTH,BTN_WIDTH)
            }
            TYPE_RECT_VER -> {
                layoutParams= LinearLayout.LayoutParams(MIN_HEIGHT,BTN_WIDTH)
            }
        }
        (layoutParams as LinearLayout.LayoutParams).setMargins(12,12,12,12)
    }

    companion object{

        const val TYPE_ROUND_MINI=0
        const val TYPE_RECT_HOR=1
        const val TYPE_RECT_VER=2
        const val TYPE_ROUND_MEDIUM=3

        var BTN_WIDTH=160
        var MIN_HEIGHT=80

        fun onActivityLoad(){
            BTN_WIDTH = ((MainActivity.size.x-MainActivity.size.x/(1.3*MainActivity.NUM_COLUMNS)) / MainActivity.NUM_COLUMNS).toInt()
            MIN_HEIGHT = BTN_WIDTH / 2
        }
    }
}