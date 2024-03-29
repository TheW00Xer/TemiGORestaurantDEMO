package com.thew00xer.temigorestaurantdemoapp

import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnSerialRawDataListener
import com.robotemi.sdk.permission.Permission
import com.robotemi.sdk.permission.Permission.Companion.GRANTED
import com.robotemi.sdk.serial.Serial
import com.robotemi.sdk.serial.Serial.cmd
import com.robotemi.sdk.serial.Serial.dataFrame
import com.robotemi.sdk.serial.Serial.dataHex
import com.robotemi.sdk.serial.Serial.getLcdBytes
import com.robotemi.sdk.serial.Serial.weight
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class MainActivity : AppCompatActivity(), OnSerialRawDataListener, OnRobotReadyListener,
    OnGoToLocationStatusChangedListener {

    private var trayStatus = hashMapOf<Int, Boolean>()

    private lateinit var robot: Robot

    private val orderList = ArrayList<String>()
    private val orderListT = ArrayList<String>()
    private lateinit var customAdapter: CustomAdapter

    private lateinit var exitbtn: ImageButton

    private lateinit var order1btn: Button
    private lateinit var order2btn: Button
    private lateinit var order3btn: Button

    private lateinit var tray1btn: Button
    private lateinit var tray2btn: Button
    private lateinit var tray3btn: Button

    private lateinit var table1btn: Button
    private lateinit var table2btn: Button
    private lateinit var table3btn: Button
    private lateinit var table4btn: Button
    private lateinit var table5btn: Button
    private lateinit var table6btn: Button
    private lateinit var table7btn: Button
    private lateinit var table8btn: Button
    private lateinit var table9btn: Button
    private lateinit var table10btn: Button

    private lateinit var clrbtn1: Button
    private lateinit var clrbtn2: Button
    private lateinit var clrbtn3: Button
    private lateinit var clrhistorybtn: Button

    private lateinit var deliverybtn: Button

    private lateinit var homebasebtn: Button
    private lateinit var loadingbaybtn: Button

    private lateinit var testbtn: Button

    private lateinit var batteryImage: ImageView
    private lateinit var batteryLevel: ProgressBar
    private lateinit var batteryCharging: ImageView
    private lateinit var batteryPercentage: TextView

    private lateinit var orderHistory: RecyclerView

    private lateinit var clockView: TextView

    private lateinit var order1: TextView
    private lateinit var order2: TextView
    private lateinit var order3: TextView

    private lateinit var order1tray: TextView
    private lateinit var order2tray: TextView
    private lateinit var order3tray: TextView
    private lateinit var order1tray2: TextView
    private lateinit var order2tray2: TextView
    private lateinit var order3tray2: TextView
    private lateinit var order1tray3: TextView
    private lateinit var order2tray3: TextView
    private lateinit var order3tray3: TextView

    private lateinit var order1location: TextView
    private lateinit var order2location: TextView
    private lateinit var order3location: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)

        // Initialize Robot instance
        robot = Robot.getInstance()

        requestMap()
        requestSettings()

        // Initialize UI
        initInterface()

        super.onCreate(savedInstanceState)
    }

    private fun requestMap() {
        if (robot.checkSelfPermission(Permission.MAP) == Permission.GRANTED) {
            Toast.makeText(this, "You already have MAP permission.", Toast.LENGTH_SHORT).show()
            return
        }
        val permissions: MutableList<Permission> = ArrayList()
        permissions.add(Permission.MAP)
        robot.requestPermissions(permissions, GRANTED)
    }

    private fun requestSettings() {
        if (robot.checkSelfPermission(Permission.SETTINGS) == Permission.GRANTED) {
            Toast.makeText(this, "You already have SETTINGS permission.", Toast.LENGTH_SHORT).show()
            return
        }
        val permissions: MutableList<Permission> = ArrayList()
        permissions.add(Permission.SETTINGS)
        robot.requestPermissions(permissions, GRANTED)
    }

    private fun initInterface() {
        // Declaring UI elements
        // Buttons
        order1btn = findViewById(R.id.order1button)
        order2btn = findViewById(R.id.order2button)
        order3btn = findViewById(R.id.order3button)

        tray1btn = findViewById(R.id.tray1button)
        tray2btn = findViewById(R.id.tray2button)
        tray3btn = findViewById(R.id.tray3button)

        table1btn = findViewById(R.id.table1button)
        table2btn = findViewById(R.id.table2button)
        table3btn = findViewById(R.id.table3button)
        table4btn = findViewById(R.id.table4button)
        table5btn = findViewById(R.id.table5button)
        table6btn = findViewById(R.id.table6button)
        table7btn = findViewById(R.id.table7button)
        table8btn = findViewById(R.id.table8button)
        table9btn = findViewById(R.id.table9button)
        table10btn = findViewById(R.id.table10button)

        clrbtn1 = findViewById(R.id.clearbutton1)
        clrbtn2 = findViewById(R.id.clearbutton2)
        clrbtn3 = findViewById(R.id.clearbutton3)
        clrhistorybtn = findViewById(R.id.clearHistoryButton)

        deliverybtn = findViewById(R.id.deliverybutton)
        homebasebtn = findViewById(R.id.homebasebutton)
        loadingbaybtn = findViewById(R.id.loadingbaybutton)

        // Exit Button
        exitbtn = findViewById(R.id.exitButton)

        // Test Button
        testbtn = findViewById(R.id.buttonTest)

        orderHistory = findViewById(R.id.recyclerViewHistory)
        customAdapter = CustomAdapter(orderList, orderListT)
        val layoutManager = LinearLayoutManager(applicationContext)
        orderHistory.layoutManager = layoutManager
        orderHistory.adapter = customAdapter

        // Displaying battery charging status and level graphically and with numerical value
        batteryImage = findViewById(R.id.imageBattery)
        batteryLevel = findViewById(R.id.progressBarBatteryLevel)
        batteryCharging = findViewById(R.id.imageBatteryCharging)
        batteryPercentage = findViewById(R.id.textBatteryLevel)
        val batteryHandler = Handler(Looper.getMainLooper())
        batteryHandler.post(object : Runnable {
            override fun run() {
                val batteryLevelData = robot.batteryData ?: return
                batteryLevel.progress = batteryLevelData.level
                batteryPercentage.text = buildString {
                    append(batteryLevelData.level)
                    append("%")
                }
                // Checking if robot is charging
                if (batteryLevelData.isCharging) {
                    batteryCharging.isVisible
                    batteryLevel.progressTintList =
                        ColorStateList.valueOf(Color.parseColor("#20D199"))
                } else {
                    batteryCharging.isVisible = false
                }
                // Checking for Battery level to adjust progress bar color accordingly
                if (batteryLevelData.level <= 15) {
                    batteryLevel.progressTintList = ColorStateList.valueOf(Color.RED)
                } else if (batteryLevelData.level in 16..30) {
                    batteryLevel.progressTintList =
                        ColorStateList.valueOf(Color.parseColor("#FF9E1B"))
                }
                batteryHandler.postDelayed(this, 1000)
            }
        })

        //Text Views
        clockView = findViewById(R.id.textViewClock)
        // "val mainHandler" and "fun run" to display time that is continuously updating.
        val timeHandler = Handler(Looper.getMainLooper())
        timeHandler.post(object : Runnable {
            override fun run() {
                clockView.text = LocalTime.now().truncatedTo(ChronoUnit.MINUTES)
                    .toString() // To display current time as timestamp
                timeHandler.postDelayed(this, 1000)
            }
        })

        order1 = findViewById(R.id.textViewOrder1)
        order2 = findViewById(R.id.textViewOrder2)
        order3 = findViewById(R.id.textViewOrder3)

        order1tray = findViewById(R.id.textViewOrder1Tray)
        order2tray = findViewById(R.id.textViewOrder2Tray)
        order3tray = findViewById(R.id.textViewOrder3Tray)
        order1tray2 = findViewById(R.id.textViewOrder1Tray2)
        order2tray2 = findViewById(R.id.textViewOrder2Tray2)
        order3tray2 = findViewById(R.id.textViewOrder3Tray2)
        order1tray3 = findViewById(R.id.textViewOrder1Tray3)
        order2tray3 = findViewById(R.id.textViewOrder2Tray3)
        order3tray3 = findViewById(R.id.textViewOrder3Tray3)

        order1location = findViewById(R.id.textViewOrder1Location)
        order2location = findViewById(R.id.textViewOrder2Location)
        order3location = findViewById(R.id.textViewOrder3Location)

        // Set on click listeners for buttons
        // Select order number
        order1btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1location.text.isEmpty()) {
                order1.setTextColor(Color.parseColor("#00843D"))
                order1btn.background.setTint((Color.parseColor("#BBBCBC")))
            }
        }
        val order2btnHandler = Handler(Looper.getMainLooper())
        order2btnHandler.post(object : Runnable {
            override fun run() {
                if (order1tray.text.isEmpty() && order1location.text.isEmpty()) {
                    order2btn.background.setTint((Color.parseColor("#BBBCBC")))
                    order2btn.isClickable = false
                } else if (order1tray.text.isNotEmpty() && order1location.text.isNotEmpty() && order2tray.text.isEmpty() && order2location.text.isEmpty()) {
                    order2btn.isClickable = true
                    order2btn.background.setTint((Color.parseColor("#8DC8E8")))
                    order2btn.setOnClickListener {
                        if (order2tray.text.isEmpty() && order2location.text.isEmpty()) {
                            order2.setTextColor(Color.parseColor("#00843D"))
                            order2btn.background.setTint((Color.parseColor("#BBBCBC")))
                        }
                    }
                }
                timeHandler.postDelayed(this, 1000)
            }
        })
        val order3btnHandler = Handler(Looper.getMainLooper())
        order3btnHandler.post(object : Runnable {
            override fun run() {
                if (order2tray.text.isEmpty() && order2location.text.isEmpty()) {
                    order3btn.background.setTint((Color.parseColor("#BBBCBC")))
                    order3btn.isClickable = false
                } else if (order2tray.text.isNotEmpty() && order2location.text.isNotEmpty() && order3tray.text.isEmpty() && order3location.text.isEmpty()) {
                    order3btn.isClickable = true
                    order3btn.background.setTint((Color.parseColor("#8DC8E8")))
                    order3btn.setOnClickListener {
                        if (order3tray.text.isEmpty() && order3location.text.isEmpty()) {
                            order3.setTextColor(Color.parseColor("#00843D"))
                            order3btn.background.setTint((Color.parseColor("#BBBCBC")))
                        }
                    }
                }
                timeHandler.postDelayed(this, 1000)
            }
        })

        // Selecting which Tray we place food on
        tray1btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1tray.text = getString(R.string.tray_1)
            } else if (order2tray.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")) {
                order2tray.text = getString(R.string.tray_1)
            } else if (order3tray.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3tray.text = getString(R.string.tray_1)
            }
            if (order1tray.text == getString(R.string.tray_1) || order2tray.text == getString(R.string.tray_1) || order3tray.text == getString(
                    R.string.tray_1
                )
            ) {
                tray1btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        tray2btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1tray.text = getString(R.string.tray_2)
            } else if (order2tray.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")) {
                order2tray.text = getString(R.string.tray_2)
            } else if (order3tray.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3tray.text = getString(R.string.tray_2)
            }
            if (order1tray.text == getString(R.string.tray_2) || order2tray.text == getString(R.string.tray_2) || order3tray.text == getString(
                    R.string.tray_2
                )
            ) {
                tray2btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        tray3btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1tray.text = getString(R.string.tray_3)
            } else if (order2tray.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")) {
                order2tray.text = getString(R.string.tray_3)
            } else if (order3tray.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3tray.text = getString(R.string.tray_3)
            }
            if (order1tray.text == getString(R.string.tray_3) || order2tray.text == getString(R.string.tray_3) || order3tray.text == getString(
                    R.string.tray_3
                )
            ) {
                tray3btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }

        // Selecting which Table should the food go to
        table1btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_1)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_1)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_1)
            }
            if (order1location.text == getString(R.string.table_1) || order2location.text == getString(
                    R.string.table_1
                ) || order3location.text == getString(R.string.table_1)
            ) {
                table1btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table2btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_2)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_2)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_2)
            }
            if (order1location.text == getString(R.string.table_2) || order2location.text == getString(
                    R.string.table_2
                ) || order3location.text == getString(R.string.table_2)
            ) {
                table2btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table3btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_3)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_3)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_3)
            }
            if (order1location.text == getString(R.string.table_3) || order2location.text == getString(
                    R.string.table_3
                ) || order3location.text == getString(R.string.table_3)
            ) {
                table3btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table4btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_4)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_4)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_4)
            }
            if (order1location.text == getString(R.string.table_4) || order2location.text == getString(
                    R.string.table_4
                ) || order3location.text == getString(R.string.table_4)
            ) {
                table4btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table5btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_5)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_5)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_5)
            }
            if (order1location.text == getString(R.string.table_5) || order2location.text == getString(
                    R.string.table_5
                ) || order3location.text == getString(R.string.table_5)
            ) {
                table5btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table6btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_6)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_6)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_6)
            }
            if (order1location.text == getString(R.string.table_6) || order2location.text == getString(
                    R.string.table_6
                ) || order3location.text == getString(R.string.table_6)
            ) {
                table6btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table7btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_7)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_7)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_7)
            }
            if (order1location.text == getString(R.string.table_7) || order2location.text == getString(
                    R.string.table_7
                ) || order3location.text == getString(R.string.table_7)
            ) {
                table7btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table8btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_8)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_8)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_8)
            }
            if (order1location.text == getString(R.string.table_8) || order2location.text == getString(
                    R.string.table_8
                ) || order3location.text == getString(R.string.table_8)
            ) {
                table8btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table9btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_9)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_9)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_9)
            }
            if (order1location.text == getString(R.string.table_9) || order2location.text == getString(
                    R.string.table_9
                ) || order3location.text == getString(R.string.table_9)
            ) {
                table9btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        table10btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order1location.text = getString(R.string.table_10)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.table_10)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.table_10)
            }
            if (order1location.text == getString(R.string.table_10) || order2location.text == getString(
                    R.string.table_10
                ) || order3location.text == getString(R.string.table_10)
            ) {
                table10btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }

        // Clear orders and reset buttons
        clrbtn1.setOnClickListener {
            when (order1tray.text) {
                getString(R.string.tray_1) -> {
                    tray1btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
                getString(R.string.tray_2) -> {
                    tray2btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
                getString(R.string.tray_3) -> {
                    tray3btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
            }
            when (order1location.text) {
                getString(R.string.table_1) -> {
                    table1btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_2) -> {
                    table2btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_3) -> {
                    table3btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_4) -> {
                    table4btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_5) -> {
                    table5btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_6) -> {
                    table6btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_7) -> {
                    table7btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_8) -> {
                    table8btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_9) -> {
                    table9btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_10) -> {
                    table10btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
            }
            order1.setTextColor(Color.parseColor("#000000"))
            order1tray.text = ""
            order1location.text = ""
            order1btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#8DC8E8"))
            }
        }
        clrbtn2.setOnClickListener {
            when (order2tray.text) {
                getString(R.string.tray_1) -> {
                    tray1btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
                getString(R.string.tray_2) -> {
                    tray2btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
                getString(R.string.tray_3) -> {
                    tray3btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
            }
            when (order2location.text) {
                getString(R.string.table_1) -> {
                    table1btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_2) -> {
                    table2btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_3) -> {
                    table3btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_4) -> {
                    table4btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_5) -> {
                    table5btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_6) -> {
                    table6btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_7) -> {
                    table7btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_8) -> {
                    table8btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_9) -> {
                    table9btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_10) -> {
                    table10btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
            }
            order2.setTextColor(Color.parseColor("#000000"))
            order2tray.text = ""
            order2location.text = ""
            order2btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#8DC8E8"))
            }
        }
        clrbtn3.setOnClickListener {
            when (order3tray.text) {
                getString(R.string.tray_1) -> {
                    tray1btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
                getString(R.string.tray_2) -> {
                    tray2btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
                getString(R.string.tray_3) -> {
                    tray3btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#8DC8E8"))
                    }
                }
            }
            when (order3location.text) {
                getString(R.string.table_1) -> {
                    table1btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_2) -> {
                    table2btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_3) -> {
                    table3btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_4) -> {
                    table4btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_5) -> {
                    table5btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_6) -> {
                    table6btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_7) -> {
                    table7btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_8) -> {
                    table8btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_9) -> {
                    table9btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
                getString(R.string.table_10) -> {
                    table10btn.apply {
                        isClickable = true
                        background.setTint(Color.parseColor("#B9975B"))
                    }
                }
            }
            order3.setTextColor(Color.parseColor("#000000"))
            order3tray.text = ""
            order3location.text = ""
            order3btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#8DC8E8"))
            }
        }

        clrhistorybtn.setOnClickListener {
            val builderHist: AlertDialog.Builder =
                AlertDialog.Builder(this, R.style.CustomAlertDialog)
            builderHist.setMessage("Are you sure you want to clear order History?")
                .setTitle("Clear order History")
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                    // Order history will be deleted.
                ) { _, _ ->
                    orderList.clear()
                    orderListT.clear()
                    customAdapter = CustomAdapter(orderList, orderListT)
                    val recyclerLayoutManager = LinearLayoutManager(applicationContext)
                    orderHistory.layoutManager = recyclerLayoutManager
                    orderHistory.adapter = customAdapter
                    Toast.makeText(this, "Order history was deleted.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialog, _ -> dialog.cancel() }
            val alert: AlertDialog = builderHist.create()
            alert.show()
        }

        testbtn.setOnClickListener {
            val locationList = arrayListOf<String>()
            if (order1location.text.isNotEmpty()) {
                orderList.add(order1location.text.toString().lowercase().trim { it <= ' ' })
                orderListT.add(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            }
            if (order2location.text.isNotEmpty()) {
                orderList.add(order2location.text.toString().lowercase().trim { it <= ' ' })
                orderListT.add(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            }
            if (order3location.text.isNotEmpty()) {
                orderList.add(order3location.text.toString().lowercase().trim { it <= ' ' })
                orderListT.add(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            }

            customAdapter = CustomAdapter(orderList, orderListT)
            val recyclerLayoutManager = LinearLayoutManager(applicationContext)
            orderHistory.layoutManager = recyclerLayoutManager
            orderHistory.adapter = customAdapter

            locationList.add(getString(R.string.kitchen))

            val ttsRequest =
                TtsRequest.create(
                    "Your food is on " + (order1tray.text.toString()) + ". " + "Please take your food.",
                    true
                )
            robot.speak(ttsRequest)
        }

        // Start delivery
        deliverybtn.setOnClickListener {
            val locationList = arrayListOf<String>()
            if (order1location.text.isNotEmpty()) {
                locationList.add(order1location.text.toString().lowercase().trim { it <= ' ' })
                orderList.add(order1location.text.toString().lowercase().trim { it <= ' ' })
                orderListT.add(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            }
            if (order2location.text.isNotEmpty()) {
                locationList.add(order2location.text.toString().lowercase().trim { it <= ' ' })
                orderList.add(order2location.text.toString().lowercase().trim { it <= ' ' })
                orderListT.add(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            }
            if (order3location.text.isNotEmpty()) {
                locationList.add(order3location.text.toString().lowercase().trim { it <= ' ' })
                orderList.add(order3location.text.toString().lowercase().trim { it <= ' ' })
                orderListT.add(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString())
            }
            /**
            for (location in robot.locations) {
            if (location == location1.lowercase()
            .trim { it <= ' ' }
            ) {
            robot.goTo(
            location1.lowercase().trim { it <= ' ' },
            backwards = false,
            noBypass = false,
            speedLevel = robot.goToSpeed
            )
            //Robot.getInstance().sendSerialCommand(
            //    Serial.CMD_LCD_TEXT,
            //    Serial.getLcdBytes(location)
            //)
            } else if (location == location2.lowercase().trim {it <= ' ' }) {
            robot.goTo(
            location2.lowercase().trim { it <= ' ' },
            backwards = false,
            noBypass = false,
            speedLevel = robot.goToSpeed
            )
            } else if (location == location3.lowercase().trim {it <= ' ' }) {
            robot.goTo(
            location3.lowercase().trim { it <= ' ' },
            backwards = false,
            noBypass = false,
            speedLevel = robot.goToSpeed
            )
            }
            } */

            customAdapter = CustomAdapter(orderList, orderListT)
            orderHistory.adapter = customAdapter

            locationList.add(getString(R.string.kitchen))

            robot.patrol(locationList, false, 1, 10)
            // robot.goTo(locationList[0].lowercase().trim { it <= ' ' }, backwards = false, noBypass = false, speedLevel = robot.goToSpeed)
            Toast.makeText(this, buildString {
                append(getString(R.string.planned_route))
                append(locationList)
            }, Toast.LENGTH_SHORT).show()
        }

        // Exit button
        exitbtn.setOnClickListener {
            val builderExit: AlertDialog.Builder =
                AlertDialog.Builder(this, R.style.CustomAlertDialog)
            builderExit.setMessage(getString(R.string.exit_app_message))
                .setTitle(getString(R.string.exit_app_title))
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                    // Activity will be moved to the background and Temi menu with app tiles will be displayed.
                ) { _, _ -> robot.showAppList() }
                // Activity will be closed
                //) { _, _ -> this@MainActivity.finish() }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialog, _ -> dialog.cancel() }
            val alert: AlertDialog = builderExit.create()
            alert.show()
        }

        // Go to Home Base
        homebasebtn.setOnClickListener {
            robot.goTo("home base")
        }

        // Go to loading area
        loadingbaybtn.setOnClickListener {
            robot.goTo(getString(R.string.kitchen))
        }
    }

    /**
     * Setting up all the event listeners
     */
    override fun onStart() {
        super.onStart()
        robot.addOnRobotReadyListener(this)
        robot.addOnGoToLocationStatusChangedListener(this)
        robot.addOnSerialRawDataListener(this)
        //robot.addOnDistanceToLocationChangedListener(this)
        //robot.addOnCurrentPositionChangedListener(this)
        //robot.addOnMovementVelocityChangedListener(this)
        //robot.addOnDistanceToDestinationChangedListener(this)
        //robot.addOnRobotDragStateChangedListener(this)
        //window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //        or View.SYSTEM_UI_FLAG_FULLSCREEN
        //        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    /**
     * Removing the event listeners upon leaving the app.
     */
    override fun onStop() {
        super.onStop()
        robot.removeOnRobotReadyListener(this)
        robot.removeOnGoToLocationStatusChangedListener(this)
        //robot.removeOnDistanceToLocationChangedListener(this)
        //robot.removeOnCurrentPositionChangedListener(this)
        robot.stopMovement()
        //if (robot.checkSelfPermission(Permission.FACE_RECOGNITION) == Permission.GRANTED) {
        //    robot.stopFaceRecognition()
        //}
        //robot.removeOnMovementVelocityChangedListener(this)
        //robot.removeOnDistanceToDestinationChangedListener(this)
        //robot.removeOnRobotDragStateChangedListener(this)

    }

    override fun onPause() {
        customAdapter = CustomAdapter(orderList, orderListT)
        val layoutManager = LinearLayoutManager(applicationContext)
        orderHistory.layoutManager = layoutManager
        orderHistory.adapter = customAdapter
        super.onPause()
    }

    override fun onDestroy() {
        robot.removeOnSerialRawDataListener(this)
        //robot.removeOnRequestPermissionResultListener(this)
        //robot.removeOnMovementStatusChangedListener(this)
        //val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        //if (appInfo.metaData != null
        //    && appInfo.metaData.getBoolean(SdkConstants.METADATA_OVERRIDE_TTS, false)
        //) {
        //    printLog("Unbind TTS service")
        //    tts = null
        //    robot.setTtsService(null)
        //}
        //if (debugReceiver != null) {
        //    unregisterReceiver(debugReceiver)
        //}
        super.onDestroy()
    }

    // Tray LEDs
    override fun onSerialRawData(data: ByteArray) {
        // Command id of response
        val cmd = data.cmd
        // Data frame of response
        val dataFrame = data.dataFrame
        // To see the hex array of raw data
        Log.d("Serial", "cmd $cmd raw data ${data.dataHex}")
        //tvResp.text = data.dataHex.toString()
        when (cmd) {
            Serial.RESP_TRAY_SENSOR -> {
                // The first place in data frame stands for tray number, starts from 0
                val trayNum = dataFrame[0].toInt() + 1
                // The second place in data frame stands for tray occupied(1) or empty(0).
                val loaded = dataFrame[1].toInt() == 1
                Log.i("weight$trayNum", dataFrame.weight.toString())
                //val speech = if (loaded) {
                // "Tray $trayNum is loaded"
                //    "トレイ $trayNum にものがあります" // Japanese translation added by me TheW00Xer
                //} else {
                // "Tray $trayNum is empty"
                //    "トレイ $trayNum 何もありません"
                //}
                if (trayStatus[trayNum] != loaded) { // Broadcast when the loaded status is changed.
                    if (loaded) {
                        // Set tray light to red when it is occupied
                        Robot.getInstance().sendSerialCommand(
                            Serial.CMD_TRAY_LIGHT,
                            byteArrayOf(data[6], 0xFF.toByte(), 0x00, 0x00)
                        )
                    } else {
                        // Set tray light to off when it is empty
                        Robot.getInstance().sendSerialCommand(
                            Serial.CMD_TRAY_LIGHT,
                            byteArrayOf(data[6], 0x00, 0x00, 0x00)
                        )
                    }
                    // Set tray light to teal when it is empty
                    //Robot.getInstance().sendSerialCommand(
                    //    Serial.CMD_TRAY_LIGHT,
                    //    byteArrayOf(data[6], 0x20, 0xD1.toByte(), 0x99.toByte())
                    //)
                }
                trayStatus[trayNum] = loaded
            }
            Serial.RESP_TRAY_BACK_BUTTON -> {
                // Just in case it doesn't exist
                val event = dataFrame.firstOrNull() ?: return
                Log.d("Serial", "Button data frame $event")
                val speech = when (event.toInt()) {
                    0 -> "touch"
                    1 -> "press"
                    2 -> "" // release after press
                    else -> ""
                }
                Robot.getInstance().speak(
                    TtsRequest.create(
                        speech,
                        isShowOnConversationLayer = false,
                        cached = true
                    )
                )
            }
        }
    }

    /**
     * Places this application in the top bar for a quick access shortcut.
     */
    override fun onRobotReady(isReady: Boolean) {
        // Removing top bar and pull down bar from screen
        if (isReady) {
            try {
                val activityInfo =
                    packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
                Robot.getInstance().onStart(activityInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
            // Navigation Billboard can only be toggled in Kiosk Mode.
            robot.toggleNavigationBillboard(true)
        }
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        val deliveryLocation1: String = order1location.text.toString()
        // Showing animation if robot is moving around the map
        if (location != getString(R.string.kitchen) && location != "home base" && status == "going" || location == getString(
                R.string.kitchen
            ) && status == "going"
            || location == "home base" && status == "going"
        ) {
            this.setContentView(R.layout.in_delivery)
            // Declaring hidden Exit button and setting onLongClick listener so staff can exit delivery screen in case there is an issue.
            this.findViewById<Button>(R.id.exitDeliveryButton).setOnLongClickListener {
                robot.stopMovement()
                Toast.makeText(this, "Exit button pushed", Toast.LENGTH_SHORT).show()
                this@MainActivity.setContentView(R.layout.activity_main)
                initInterface()
                return@setOnLongClickListener true
            }
            robot.sendSerialCommand(
                Serial.CMD_LCD_TEXT,
                getLcdBytes(location)
            )
        } else if (location == deliveryLocation1 && status == "complete") {
            Toast.makeText(this, deliveryLocation1, Toast.LENGTH_LONG).show()
            val ttsRequest =
                TtsRequest.create(
                    "Your food is on " + (order1tray.text.toString()) + ". " + "Please take your food.",
                    true
                )
            robot.speak(ttsRequest)
        }
        // Showing staff screen if robot is in "Kitchen" or at "Home base"
        if (location == getString(R.string.kitchen) && status == "complete" || location == "home base" && status == "complete") {
            robot.sendSerialCommand(
                Serial.CMD_LCD_TEXT,
                getLcdBytes(location)
            )
            setContentView(R.layout.activity_main)
            initInterface()
        }
        // Return to location Kitchen if you can not proceed to set location (table).
        // This is a preventive measure so robot won't stay stuck in middle of restaurant if it's not able to deliver food.
        if (location != (getString(R.string.kitchen)) && status == "abort" || location != "home base" && status == "abort") {
            robot.goTo(getString(R.string.kitchen))
        }
    }
}

// Toast.makeText(this, "Your text", Toast.LENGTH_SHORT).show()