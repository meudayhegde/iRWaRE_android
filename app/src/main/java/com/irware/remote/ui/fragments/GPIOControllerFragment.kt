package com.irware.remote.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.textfield.TextInputEditText
import com.irware.ThreadHandler
import com.irware.remote.MainActivity
import com.irware.remote.R
import com.irware.remote.holders.DeviceProperties
import com.irware.remote.holders.GPIOObject
import com.irware.remote.holders.RemoteProperties
import com.irware.remote.net.SocketClient
import com.irware.remote.ui.adapters.GPIOListAdapter
import com.irware.remote.ui.adapters.RemoteListAdapter
import org.json.JSONObject
import java.io.File
import kotlin.math.min

class GPIOControllerFragment : androidx.fragment.app.Fragment()  {
    private var listener: OnFragmentInteractionListener? = null
    private var recyclerView: RecyclerView? = null
    private var viewAdapter: GPIOListAdapter? = null
    private var viewManager: RecyclerView.LayoutManager? = null
    private var rootView: RelativeLayout? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(rootView == null ){
            rootView = inflater.inflate(R.layout.fragment_gpio_controller, container, false) as RelativeLayout
            viewManager = LinearLayoutManager(context)
            viewAdapter = GPIOListAdapter(MainActivity.gpioObjectList)
            recyclerView = rootView!!.findViewById<RecyclerView>(R.id.manage_remotes_recycler_view).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
            rootView!!.findViewById<FloatingActionMenu>(R.id.fam_manage_gpio).setClosedOnTouchOutside(true)

            val refreshLayout = rootView!!.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
            refreshLayout.setOnRefreshListener {
                refreshLayout.isRefreshing = true
                MainActivity.devicePropList.forEach {
                    it.updateStatus()
                }
                MainActivity.threadHandler?.runOnFreeThread{
                    Thread.sleep(100)
                    MainActivity.threadHandler?.runOnThread(ThreadHandler.ESP_MESSAGE) {
                        refreshLayout.isRefreshing = false
                    }
                }
            }

            val dialog = AlertDialog.Builder(context!!)
                .setIcon(R.drawable.icon_lamp)
                .setTitle("Add new GPIO switch")
                .setNegativeButton("Cancel") { p0, _ -> p0.dismiss() }
                .setPositiveButton("Confirm") {_, _ -> }
                .setView(R.layout.layout_new_gpio_switch)
                .create()
            rootView!!.findViewById<FloatingActionButton>(R.id.fab_new_switch).setOnClickListener { dialog.show() }
            dialog.setOnShowListener {
                val width = min(MainActivity.size.x,MainActivity.size.y)
                dialog.window?.setLayout(width*7/8, WindowManager.LayoutParams.WRAP_CONTENT)
                dialog.window?.setBackgroundDrawableResource(R.drawable.layout_border_round_corner)

                val devicesSpinner = dialog.findViewById<Spinner>(R.id.select_device)!!
                val gpioSpinner = dialog.findViewById<Spinner>(R.id.pin_number)!!

                gpioSpinner.adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, arrayListOf(
                    context!!.getString(R.string.pin), "GPIO0 (D3)", "GPIO1 (TX)", "GPIO2 (D4)", "GPIO3 (RX)", "GPIO4 (D2)", "GPIO5 (D1)",
                    "GPIO9 (SD2)", "GPIO10 (SD3)", "GPIO12 (D6)", "GPIO13 (D7)", "GPIO14 (D5)", "GPIO15 (D8)", "GPIO16 (D0)"
                ))
                val devicePropList = arrayListOf<Any>(context!!.getString(R.string.select_device))
                devicePropList.addAll(MainActivity.devicePropList)
                devicesSpinner.adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, devicePropList)
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val nameText = dialog.findViewById<TextInputEditText>(R.id.edit_text_switch_name)!!
                    val description = dialog.findViewById<TextInputEditText>(R.id.switch_desc)!!

                    when {
                        devicesSpinner.selectedItemPosition == 0 -> {
                            Toast.makeText(context!!, "Please select Device", Toast.LENGTH_SHORT).show()
                        }
                        gpioSpinner.selectedItemPosition == 0 -> {
                            Toast.makeText(context!!, "Please select GPIO Pin number", Toast.LENGTH_SHORT).show()
                        }
                        nameText.text?.isEmpty()?: false -> {
                            nameText.error = "Name field cannot be empty"
                        }
                        else -> {
                            val jsonObj = JSONObject()
                            jsonObj.put("title", nameText.text.toString())
                            jsonObj.put("subTitle", description.text.toString())
                            jsonObj.put("macAddr", MainActivity.devicePropList[devicesSpinner.selectedItemPosition - 1].macAddr)
                            jsonObj.put("gpioNumber", gpioSpinner.selectedItem.toString().split(" ")[0].filter { it.isDigit() }.toInt())
                            MainActivity.gpioObjectList.add(GPIOObject(jsonObj, MainActivity.gpioConfig!!))
                            viewAdapter?.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                    }
                }
            }
        }
        val manageMenu = rootView!!.findViewById<FloatingActionMenu>(R.id.fam_manage_gpio)
        if(!manageMenu.isOpened)
            manageMenu.hideMenuButton(false)
        Handler().postDelayed({
            if(manageMenu.isMenuButtonHidden)
                manageMenu.showMenuButton(true)
            if(MainActivity.remotePropList.isEmpty())
                Handler().postDelayed({manageMenu.showMenu(true)},400)
        },400)
        return rootView
    }

    fun notifyDataChanged(){
        viewAdapter?.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
