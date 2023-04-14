package com.thew00xer.temigorestaurantdemoapp

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnSerialRawDataListener
import com.robotemi.sdk.permission.Permission
import com.robotemi.sdk.serial.Serial
import com.robotemi.sdk.serial.Serial.cmd
import com.robotemi.sdk.serial.Serial.dataFrame
import com.robotemi.sdk.serial.Serial.dataHex
import com.robotemi.sdk.serial.Serial.getLcdBytes
import com.robotemi.sdk.serial.Serial.weight
import pl.droidsonroids.gif.GifImageView

class MainActivity : AppCompatActivity(), OnSerialRawDataListener, OnRobotReadyListener,
    OnGoToLocationStatusChangedListener {

    private var trayStatus = hashMapOf<Int, Boolean>()

    private lateinit var gifView: GifImageView

    private lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Robot instance
        robot = Robot.getInstance()
        robot.addOnSerialRawDataListener(this)
        robot.showTopBar()

        // Buttons
        val order1btn = findViewById<Button>(R.id.order1button)
        val order2btn = findViewById<Button>(R.id.order2button)
        val order3btn = findViewById<Button>(R.id.order3button)

        val tray1btn = findViewById<Button>(R.id.tray1button)
        val tray2btn = findViewById<Button>(R.id.tray2button)
        val tray3btn = findViewById<Button>(R.id.tray3button)

        val table1btn = findViewById<Button>(R.id.table1button)
        val table2btn = findViewById<Button>(R.id.table2button)
        val table3btn = findViewById<Button>(R.id.table3button)
        val table4btn = findViewById<Button>(R.id.table4button)
        val table5btn = findViewById<Button>(R.id.table5button)
        val table6btn = findViewById<Button>(R.id.table6button)
        val table7btn = findViewById<Button>(R.id.table7button)
        val table8btn = findViewById<Button>(R.id.table8button)
        val table9btn = findViewById<Button>(R.id.table9button)
        val table10btn = findViewById<Button>(R.id.table10button)

        val clrbtn = findViewById<Button>(R.id.clearbutton)

        val deliverybtn = findViewById<Button>(R.id.deliverybutton)
        val homebasebtn = findViewById<Button>(R.id.homebasebutton)
        val loadingbaybtn = findViewById<Button>(R.id.loadingbaybutton)

        // TEST
        val exitbtn = findViewById<Button>(R.id.exitButton)

        //Text Views
        val order1 = findViewById<TextView>(R.id.textViewOrder1)
        val order2 = findViewById<TextView>(R.id.textViewOrder2)
        val order3 = findViewById<TextView>(R.id.textViewOrder3)

        val order1tray = findViewById<TextView>(R.id.textViewOrder1Tray)
        val order2tray = findViewById<TextView>(R.id.textViewOrder2Tray)
        val order3tray = findViewById<TextView>(R.id.textViewOrder3Tray)

        val order1location = findViewById<TextView>(R.id.textViewOrder1Location)
        val order2location = findViewById<TextView>(R.id.textViewOrder2Location)
        val order3location = findViewById<TextView>(R.id.textViewOrder3Location)

        gifView = findViewById(R.id.gifView)

        // Select order number
        order1btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1location.text.isEmpty()) {
                order1.setTextColor(Color.parseColor("#00843D"))
                order1btn.background.setTint((Color.parseColor("#BBBCBC")))
            }
        }
        order2btn.setOnClickListener {
            if (order2tray.text.isEmpty() && order2location.text.isEmpty()) {
                order2.setTextColor(Color.parseColor("#00843D"))
                order2btn.background.setTint((Color.parseColor("#BBBCBC")))
            }
        }
        order3btn.setOnClickListener {
            if (order3tray.text.isEmpty() && order3location.text.isEmpty()) {
                order3.setTextColor(Color.parseColor("#00843D"))
                order3btn.background.setTint((Color.parseColor("#BBBCBC")))
            }
        }

        // Selecting which Tray we place food on
        tray1btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1tray.text = getString(R.string.Tray_1)
            } else if (order2tray.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")) {
                order2tray.text = getString(R.string.Tray_1)
            } else if (order3tray.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3tray.text = getString(R.string.Tray_1)
            }
            if (order1tray.text == getString(R.string.Tray_1) || order2tray.text == getString(R.string.Tray_1) || order3tray.text == getString(R.string.Tray_1)) {
                tray1btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        tray2btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1tray.text = getString(R.string.Tray_2)
            } else if (order2tray.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")) {
                order2tray.text = getString(R.string.Tray_2)
            } else if (order3tray.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3tray.text = getString(R.string.Tray_2)
            }
            if (order1tray.text == getString(R.string.Tray_2) || order2tray.text == getString(R.string.Tray_2) || order3tray.text == getString(R.string.Tray_2)) {
                tray2btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }
        tray3btn.setOnClickListener {
            if (order1tray.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1tray.text = getString(R.string.Tray_3)
            } else if (order2tray.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")) {
                order2tray.text = getString(R.string.Tray_3)
            } else if (order3tray.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3tray.text = getString(R.string.Tray_3)
            }
            if (order1tray.text == getString(R.string.Tray_3) || order2tray.text == getString(R.string.Tray_3) || order3tray.text == getString(R.string.Tray_3)) {
                tray3btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }

        // Selecting which Table should the food go to
        table1btn.setOnClickListener {
            if (order1tray.text.isNotEmpty() && order1location.text.isEmpty() && order1.currentTextColor == Color.parseColor("#00843D")) {
                order1location.text = getString(R.string.Table_1)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor("#00843D")
            ) {
                order2location.text = getString(R.string.Table_1)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor("#00843D")) {
                order3location.text = getString(R.string.Table_1)
            }
            if (order1location.text == getString(R.string.Table_1) || order2location.text == getString(R.string.Table_1) || order3location.text == getString(R.string.Table_1)) {
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
                order1location.text = getString(R.string.Table_2)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_2)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_2)
            }
            if (order1location.text == getString(R.string.Table_2) || order2location.text == getString(R.string.Table_2) || order3location.text == getString(R.string.Table_2)) {
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
                order1location.text = getString(R.string.Table_3)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_3)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_3)
            }
            if (order1location.text == getString(R.string.Table_3) || order2location.text == getString(R.string.Table_3) || order3location.text == getString(R.string.Table_3)) {
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
                order1location.text = getString(R.string.Table_4)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_4)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_4)
            }
            if (order1location.text == getString(R.string.Table_4) || order2location.text == getString(R.string.Table_4) || order3location.text == getString(R.string.Table_4)) {
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
                order1location.text = getString(R.string.Table_5)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_5)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_5)
            }
            if (order1location.text == getString(R.string.Table_5) || order2location.text == getString(R.string.Table_5) || order3location.text == getString(R.string.Table_5)) {
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
                order1location.text = getString(R.string.Table_6)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_6)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_6)
            }
            if (order1location.text == getString(R.string.Table_6) || order2location.text == getString(R.string.Table_6) || order3location.text == getString(R.string.Table_6)) {
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
                order1location.text = getString(R.string.Table_7)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_7)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_7)
            }
            if (order1location.text == getString(R.string.Table_7) || order2location.text == getString(R.string.Table_7) || order3location.text == getString(R.string.Table_7)) {
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
                order1location.text = getString(R.string.Table_8)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_8)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_8)
            }
            if (order1location.text == getString(R.string.Table_8) || order2location.text == getString(R.string.Table_8) || order3location.text == getString(R.string.Table_8)) {
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
                order1location.text = getString(R.string.Table_9)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_9)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_9)
            }
            if (order1location.text == getString(R.string.Table_9) || order2location.text == getString(R.string.Table_9) || order3location.text == getString(R.string.Table_9)) {
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
                order1location.text = getString(R.string.Table_10)
            } else if (order2tray.text.isNotEmpty() && order2location.text.isEmpty() && order2.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order2location.text = getString(R.string.Table_10)
            } else if (order3tray.text.isNotEmpty() && order3location.text.isEmpty() && order3.currentTextColor == Color.parseColor(
                    "#00843D"
                )
            ) {
                order3location.text = getString(R.string.Table_10)
            }
            if (order1location.text == getString(R.string.Table_10) || order2location.text == getString(R.string.Table_10) || order3location.text == getString(R.string.Table_10)) {
                table10btn.apply {
                    isClickable = false
                    background.setTint(Color.parseColor("#BBBCBC"))
                }
            }
        }

        // Clear orders and reset buttons
        clrbtn.setOnClickListener {
            order1.setTextColor(Color.parseColor("#000000"))
            order2.setTextColor(Color.parseColor("#000000"))
            order3.setTextColor(Color.parseColor("#000000"))

            order1tray.text = ""
            order2tray.text = ""
            order3tray.text = ""

            order1location.text = ""
            order2location.text = ""
            order3location.text = ""

            order1btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            order2btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            order3btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }

            tray1btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            tray2btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            tray3btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }

            table1btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table2btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table3btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table4btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table5btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table6btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table7btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table8btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table9btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
            table10btn.apply {
                isClickable = true
                background.setTint(Color.parseColor("#526DFF"))
            }
        }

        // Start delivery
        deliverybtn.setOnClickListener {
            val locationList = arrayListOf<String>()
            if (order1location.text.isNotEmpty()) {
                locationList.add(order1location.text.toString().lowercase().trim { it <= ' ' })
            }
            if (order2location.text.isNotEmpty()) {
                locationList.add(order2location.text.toString().lowercase().trim { it <= ' ' })
            }
            if (order3location.text.isNotEmpty()) {
                locationList.add(order3location.text.toString().lowercase().trim { it <= ' ' })
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
            locationList.add(getString(R.string.kitchen))
            robot.patrol(locationList, false, 1, 10)
            // robot.goTo(locationList[0].lowercase().trim { it <= ' ' }, backwards = false, noBypass = false, speedLevel = robot.goToSpeed)
            //Toast.makeText(this, "You pushed DELIVERY button.", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, buildString {
        append(getString(R.string.Planned_route))
        append(locationList)
    }, Toast.LENGTH_SHORT).show()
        }

        // Hidden exit button
        exitbtn.setOnLongClickListener {
            gifView.apply {
                isVisible = false
                isFocusable = false
            }
            deliverybtn.isVisible = true
            return@setOnLongClickListener true
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
        if (isReady) {
            try {
                val activityInfo =
                    packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
                // Robot.getInstance().onStart() method may change the visibility of top bar.
                robot.onStart(activityInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun onGoToLocationStatusChanged(location: String, status: String, descriptionId: Int, description: String) {
        // Showing animation if robot is moving around the map
        if (location != getString(R.string.kitchen) && location != "home base" && status == "going" || status == "complete" || location == getString(R.string.kitchen) && status == "going") {
            gifView.apply {
                isVisible = true
                isFocusable = true
            }
            robot.sendSerialCommand(
                Serial.CMD_LCD_TEXT,
                getLcdBytes(location)
            )
        }
        // Showing staff screen if robot is in "Kitchen" or at "Home base"
        if (location == getString(R.string.kitchen) && status == "complete" || location == "home base" && status == "complete") {
            gifView.apply {
                isVisible = false
                isFocusable = false
            }
            robot.sendSerialCommand(
                Serial.CMD_LCD_TEXT,
                getLcdBytes(location)
            )
        }
    }

    /**
     * Setting up all the event listeners
     */
    override fun onStart() {
        super.onStart()
        robot.addOnRobotReadyListener(this)
        robot.addOnGoToLocationStatusChangedListener(this)
        //robot.addTtsListener(this)
        //robot.addOnDistanceToLocationChangedListener(this)
        //robot.addOnCurrentPositionChangedListener(this)
        //robot.addOnMovementVelocityChangedListener(this)
        //robot.addOnDistanceToDestinationChangedListener(this)
        //robot.addOnRobotDragStateChangedListener(this)
        robot.showTopBar()
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
        robot.removeOnRobotReadyListener(this)
        robot.removeOnGoToLocationStatusChangedListener(this)
        //robot.removeTtsListener(this)
        //robot.removeOnDistanceToLocationChangedListener(this)
        //robot.removeOnCurrentPositionChangedListener(this)
        robot.stopMovement()
        if (robot.checkSelfPermission(Permission.FACE_RECOGNITION) == Permission.GRANTED) {
            robot.stopFaceRecognition()
        }
        //robot.removeOnMovementVelocityChangedListener(this)
        //robot.removeOnDistanceToDestinationChangedListener(this)
        //robot.removeOnRobotDragStateChangedListener(this)
        super.onStop()
    }

    override fun onDestroy() {
        Robot.getInstance().removeOnSerialRawDataListener(this)
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
}

//{
//    Toast.makeText(this, "No such location is saved.", Toast.LENGTH_SHORT).show()
//}