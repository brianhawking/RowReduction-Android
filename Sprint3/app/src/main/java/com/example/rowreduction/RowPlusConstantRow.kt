package com.example.rowreduction

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class RowPlusConstantRow : AppCompatActivity() {

    private var buttonIDs = arrayOf(
        R.id.button0,
        R.id.button1,
        R.id.button2,
        R.id.button3,
        R.id.button4,
        R.id.button5,
        R.id.button6,
        R.id.button7,
        R.id.button8,
        R.id.button9,
        R.id.buttonDEL,
        R.id.buttonPlusMinus,
        R.id.buttonFrac,
    )

    private var rowButtonIDs = arrayOf(
        R.id.row1Button,
        R.id.row2Button,
        R.id.row3Button
    )

    var numberOfEquations = 0
    var finalRow = 1
    var constant = "0"
    var pivotRow = 2
    var whichBox = "Initial"
    var constantSign = 1
    lateinit var rowImageView: ImageView
    lateinit var constantView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_row_plus_constant_row)

        // get data from previous activity
        val bundle: Bundle? = intent.extras
        numberOfEquations = bundle?.getInt("numberOfEquations")!!

        // hide row 3, if necessary
        if(numberOfEquations == 2) {
            val row: Button = findViewById(R.id.row3Button)
            row.visibility = View.GONE
        }

        // set up interacted views
        rowImageView = findViewById(R.id.initialRow)
        constantView = findViewById(R.id.constant)

        setupCalculator()
        setupRowButtons()
        setupDoneButtons()
        setupMoveButton()
    }

    fun setupCalculator() {
        // cycle through available buttons
        for(buttonID in buttonIDs) {

            // grab current button
            val b: Button = findViewById(buttonID)

            // set up listeners
            b.setOnClickListener {
                editNumber(buttonID)
            }
        }
    }

    fun setupMoveButton() {
        val b: ImageButton = findViewById(R.id.buttonMove)
        b.setOnClickListener {

            when(whichBox) {
                "Initial" -> {
                    rowImageView.setBackgroundColor(Color.TRANSPARENT)
                    whichBox = "Constant"
                    // highlight that box
                    constantView = findViewById(R.id.constant)
                    constantView.setBackgroundResource(R.drawable.my_border)

                }
                "Constant" -> {
                    constantView.setBackgroundColor(Color.TRANSPARENT)
                    whichBox = "Pivot"
                    // highlight that box
                    rowImageView = findViewById(R.id.pivotRow)
                    rowImageView.setBackgroundResource(R.drawable.my_border)
                }
                "Pivot" -> {
                    rowImageView.setBackgroundColor(Color.TRANSPARENT)
                    whichBox = "Initial"
                    // highlight that box
                    rowImageView = findViewById(R.id.initialRow)
                    rowImageView.setBackgroundResource(R.drawable.my_border)

                }
            }

        }

    }

    private fun editNumber(id: Int) {

        if(whichBox != "Constant") {
            return
        }

        // temp
        val tempResult: String

        if(constant == "0") {
            constant = ""
        }

        when (id) {
            R.id.button0 -> {
                constant += "0"
            }
            R.id.button1 -> {
                constant += "1"
            }
            R.id.button2 -> {
                constant += "2"
            }
            R.id.button3 -> {
                constant += "3"
            }
            R.id.button4 -> {
                constant += "4"
            }
            R.id.button5 -> {
                constant += "5"
            }
            R.id.button6 -> {
                constant += "6"
            }
            R.id.button7 -> {
                constant += "7"
            }
            R.id.button8 -> {
                constant += "8"
            }
            R.id.button9 -> {
                constant += "9"
            }
            R.id.buttonDEL -> {

                tempResult = constant.dropLast(1)
                constant = tempResult

                if(constant == "") {
                    constant = ""
                }
            }
            R.id.buttonPlusMinus -> {

                val view: TextView = findViewById(R.id.Plus)
                if (view.text == "+") {
                    view.text = "-"
                    constantSign = -1
                }
                else {
                    view.text = "+"
                    constantSign = 1
                }

//                if(constant.contains("-", true)) {
//                    // remove -
////                    tempResult = constant.drop(1)
////                    constant = tempResult
//                    val view: TextView = findViewById(R.id.Plus)
//                    view.text = "+"
//                }
//                else {
//                    val temp = constant
////                    constant = "-$temp"
//                    val view: TextView = findViewById(R.id.Plus)
//                    view.text = "-"
//                }

            }
            R.id.buttonFrac -> {

                // don't add a fraction if it has a / or decimal.
                if (constant.contains("/") || constant.contains(".") ) {
                    return
                }
                constant += "/"

                // don't show / if it's the only text shown
                if(constant == "/") {
                    constant = "0"
                }
            }
            else -> {
                println("Error")
            }
        }

        val textViewResult: TextView = findViewById(R.id.constant)
        textViewResult.text = constant
    }


    fun setupRowButtons() {
        // go through the row selections
        for (element in rowButtonIDs) {

            val rowButton: Button = findViewById(element)

            // set up the listeners
            rowButton.setOnClickListener {

                val initialRowImage: ImageView = findViewById(R.id.initialRow)
                val pivotRowImage: ImageView = findViewById(R.id.pivotRow)
                val finalRowImage: ImageView = findViewById(R.id.finalRow)

                // change row image based on user input
                when (element) {
                    R.id.row1Button -> {
                        if (whichBox == "Initial") {
                            initialRowImage.setImageResource(R.drawable.r1)
                            finalRowImage.setImageResource(R.drawable.r1)
                            finalRow = 1
                        }
                        else if(whichBox == "Pivot") {
                            pivotRowImage.setImageResource(R.drawable.r1)
                            pivotRow = 1
                        }

                    }
                    R.id.row2Button -> {
                        if (whichBox == "Initial") {
                            initialRowImage.setImageResource(R.drawable.r2)
                            finalRowImage.setImageResource(R.drawable.r2)
                            finalRow = 2
                        }
                        else if (whichBox == "Pivot"){
                            pivotRowImage.setImageResource(R.drawable.r2)
                            pivotRow = 2
                        }
                    }
                    R.id.row3Button -> {
                        if (whichBox == "Initial") {
                            initialRowImage.setImageResource(R.drawable.r3)
                            finalRowImage.setImageResource(R.drawable.r3)
                            finalRow = 3
                        }
                        else if(whichBox == "Pivot"){
                            pivotRowImage.setImageResource(R.drawable.r3)
                            pivotRow = 3
                        }
                    }
                }
            }
        }
    }

    fun setupDoneButtons() {
        val cancelButton: ImageButton = findViewById(R.id.buttonCancel)
        cancelButton.setOnClickListener {
            // go back without sending any data
            prepareReturn(false)
        }

        val confirm: ImageButton = findViewById(R.id.buttonDone)
        confirm.setOnClickListener {
            // go back with data
            prepareReturn(true)
        }
    }

    fun prepareReturn(b: Boolean) {

        // b = true means they want to perform the operation
        if (!b) {
            // set up return with no data
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        else if (b && validate()) {
            // get data set up to send back
            val data = Intent().apply {
                putExtra("finalRow", finalRow)
                if (constantSign == -1) {
                    val temp = "-$constant"
                    constant = temp
                }
                putExtra("constant", constant)
                putExtra("pivotRow", pivotRow)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }



    }

    fun validate() : Boolean {

        // check if final and pivot rows are the same
        if (finalRow == pivotRow) {
            Toast.makeText(
                this,
                "Your final row and pivot rows can't be the same",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        val rational = Rational(0,1)
        println("CHECKING IF $constant is rational")
        return if(!rational.isRational(constant)) {
            Toast.makeText(
                this,
                "Your number is not valid.",
                Toast.LENGTH_LONG
            ).show()
            false
        } else {
            true
        }

    }
}