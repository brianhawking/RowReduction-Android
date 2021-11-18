package com.example.rowreduction

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

/*
- What's happening in this activity
1. User enters the coefficients from their system of equations
2. They move around to different coefficients by pressing R (right) or L (left)
3. They can enter integers, make it positive or negative, fractions, or decimals
4. All decimals are converted to fractions
 */

/*
- Error Checks
1. Can't move to different coefficient if current number is invalid
2. Invalid numbers would be fraction without denominator, divide by 0
 */

class MainActivity : AppCompatActivity() {

    // all the IDs of the calculator buttons
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
        R.id.setupMatrix,
        R.id.buttonDot,
        R.id.buttonLeft,
        R.id.buttonRight,
        R.id.buttonFrac,
        R.id.buttonSubEqn,
        R.id.buttonAddEqn,
        R.id.buttonAddVar,
        R.id.buttonSubVar
    )

    // all the textviews IDS of the coefficients
    private var equationIDs = arrayOf(
        arrayOf(R.id.coefficientX1, R.id.coefficientY1,R.id.coefficientZ1, R.id.coefficientC1),
        arrayOf(R.id.coefficientX2, R.id.coefficientY2, R.id.coefficientZ2, R.id.coefficientC2),
        arrayOf(R.id.coefficientX3, R.id.coefficientY3, R.id.coefficientZ3, R.id.coefficientC3)
    )

    // all ids for the z variable column
    // used to change the visibility
    var column3IDs = arrayOf(
        R.id.equation1Z,
        R.id.equation2Z,
        R.id.equation3Z,
        R.id.equation12Plus,
        R.id.equation22Plus,
        R.id.equation32Plus,
        R.id.coefficientZ3,
        R.id.coefficientZ2,
        R.id.coefficientZ1
    )

    // all ids for equation 3
    // used to change the visibility
    var row3IDs = arrayOf(
        R.id.coefficientX3,
        R.id.coefficientY3,
        R.id.coefficientZ3,
        R.id.coefficientC3,
        R.id.equation3X,
        R.id.equation31Plus,
        R.id.equation3Y,
        R.id.equation32Plus,
        R.id.equation3Z,
        R.id.equation3Equals,
    )

    // variable containing current state of all coefficients
    var matrix = Matrix()


    // keep track of position
    var row = 0
    var column = 0
    var numberOfEquations = 3
    var numberOfVariables = 3

    lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // current selected box
        textViewResult = findViewById(equationIDs[row][column])

        // add listeners to all the buttons ================================================
        for(buttonID in buttonIDs) {

            var button: Button
            var imageButton: ImageButton

            if (buttonID == R.id.buttonRight || buttonID == R.id.buttonLeft) {
                imageButton = findViewById(buttonID)
                // user selects to move
                imageButton.setOnClickListener {
                    moveBox(buttonID)
                }
            }
            else {

                button = findViewById(buttonID)

                button.setOnClickListener {

                    // user wants to change size of system
                    if (buttonID == R.id.buttonSubVar || buttonID == R.id.buttonAddVar || buttonID == R.id.buttonAddEqn || buttonID == R.id.buttonSubEqn) {
                        changeSize(buttonID)
                    }
                    // confirm matrix and move to next activity
                    else if (buttonID == R.id.setupMatrix) {
                        if (validate()) {
                            // move to next screen
                            setupAugmentedMatrix()
                        }
                    }
                    // they pressed a number
                    else {
                        editNumber(buttonID)
                    }
                }
            }
        }
    }

    // add or remove the z column or...
    // add or remove the third equation
    private fun changeSize(id: Int) {

        // which size adjustment did they make
        when (id) {
            R.id.buttonSubVar -> {

                // remove the 3rd variable column
                for (element in column3IDs) {
                    val box: TextView = findViewById(element)
                    box.visibility = View.GONE
                }
                numberOfVariables -= 1

                if (numberOfVariables == 3) {
                    numberOfVariables = 2
                }

            }
            // add the third variable column
            R.id.buttonAddVar -> {
                for (element in column3IDs) {
                    val box: TextView = findViewById(element)
                    box.visibility = View.VISIBLE
                }
                if (numberOfEquations == 2) {
                    for (element in row3IDs) {
                        val box: TextView = findViewById(element)
                        box.visibility = View.GONE
                    }
                }

                if (numberOfVariables == 2) {
                    numberOfVariables = 3
                }
            }
            // add 3rd equation
            R.id.buttonAddEqn -> {
                for(element in row3IDs) {
                    val box: TextView = findViewById(element)
                    box.visibility = View.VISIBLE
                }
                if (numberOfVariables == 2) {
                    // hide column 3
                    for (element in column3IDs) {
                        val box: TextView = findViewById(element)
                        box.visibility = View.GONE
                    }
                }

                if (numberOfEquations == 2) {
                    numberOfEquations = 3
                }
            }
            // remove 3rd equation
            R.id.buttonSubEqn -> {
                for(element in row3IDs) {
                    val box: TextView = findViewById(element)
                    box.visibility = View.GONE
                }

                if(numberOfEquations == 3) {
                    numberOfEquations -= 1
                }
            }
        }


    }

    // get data ready to send to next activity
    private fun setupAugmentedMatrix() {

        // set up transition to next activity
        val intent = Intent(this,MainActivity2::class.java)

        // set data up to send to next activity
        intent.putExtra("coefficients1", matrix.coefficients[0])
        intent.putExtra("coefficients2", matrix.coefficients[1])
        intent.putExtra("coefficients3", matrix.coefficients[2])
        intent.putExtra("numberOfEquations", numberOfEquations)
        intent.putExtra("numberOfVariables", numberOfVariables)

        startActivity(intent)
    }

    // make sure the number is valid
    private fun validate() : Boolean {

        // search through coefficients. make sure they're all valid numbers
        // if they're all numbers, convert to Rational object
        for(i in matrix.coefficients.indices) {
            for (j in matrix.coefficients[i].indices) {

                // if the string to rational returns false (invalid number), return false for validate
                if (!matrix.stringToRational(i,j)) {
                    Toast.makeText(
                        this,
                        "One of your coefficients is invalid.",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
                // update the number on screen
                updateBox(i,j)
            }
        }
        return true
    }

    // called when a number needs to be updated on screen
    private fun updateBox(row: Int, column: Int) {
        val box: TextView = findViewById(equationIDs[row][column])
        box.text = matrix.coefficientsAsRationals[row][column].toString()
    }

    // user presses the L (left) or R (right) box
    // remove border from current coefficient
    // add border to next coefficient
    private fun moveBox(direction: Int) : Boolean {

        if(!validate()) {
            println("Your number is invalid.")
            return false
        }
        else {
            textViewResult = findViewById(equationIDs[row][column])
            textViewResult.setBackgroundColor(Color.TRANSPARENT)
        }

        // moves the box
        when (direction) {
            R.id.buttonLeft -> {
                // move left
                column -= 1

                if(numberOfVariables == 2 && column == 2) {
                    column -= 1
                }

                if(column < 0) {
                    row -= 1
                    column = 3

                    if(row < 0) {
                        row = (numberOfEquations-1)
                    }
                }
            }
            R.id.buttonRight -> {
                // move right
                column += 1

                if(numberOfVariables == 2 && column == 2) {
                    column += 1
                }

                if(column > 3) {
                    row += 1
                    column = 0

                    if(row == numberOfEquations) {
                        row = 0
                    }
                }
            }
            else -> {
                println("ERROR")
            }
        }

        textViewResult = findViewById(equationIDs[row][column])
        textViewResult.setBackgroundResource(R.drawable.my_border)

        // return true if you can move box
        return true
    }

    // update number on screen when user presses a number, fraction, or decimal
    private fun editNumber(id: Int) {

        // if number is 0, set it to empty so the user's first number won't lead with 0
        // ex. 0 -> 04, instead of just 4
        if(matrix.coefficients[row][column] == "0") {
            matrix.coefficients[row][column] = ""
        }

        // used to store the current state of coefficient before we modify the number
        val tempResult: String

        when (id) {
            R.id.button0 -> {
                // don't add a 0 if the number is already 0
                if(matrix.coefficients[row][column] == "0") {
                    return
                }
                // don't add a zero if the decimal already ends in a 0
                if( (matrix.coefficients[row][column].contains(".", false)) && (matrix.coefficients[row][column].last() == '0')) {
                    return
                }
                matrix.coefficients[row][column] += "0"
            }
            R.id.button1 -> {
                matrix.coefficients[row][column] += "1"
            }
            R.id.button2 -> {
                matrix.coefficients[row][column] += "2"
            }
            R.id.button3 -> {
                matrix.coefficients[row][column] += "3"
            }
            R.id.button4 -> {
                matrix.coefficients[row][column] += "4"
            }
            R.id.button5 -> {
                matrix.coefficients[row][column] += "5"
            }
            R.id.button6 -> {
                matrix.coefficients[row][column] += "6"
            }
            R.id.button7 -> {
                matrix.coefficients[row][column] += "7"
            }
            R.id.button8 -> {
                matrix.coefficients[row][column] += "8"
            }
            R.id.button9 -> {
                matrix.coefficients[row][column] += "9"
            }
            R.id.buttonDEL -> {

                tempResult = matrix.coefficients[row][column].dropLast(1)
                matrix.coefficients[row][column] = tempResult

                if(matrix.coefficients[row][column] == "") {
                    matrix.coefficients[row][column] = "0"
                }
            }
            R.id.buttonPlusMinus -> {
                if(matrix.coefficients[row][column].contains("-", true)) {
                    // remove -
                    tempResult = matrix.coefficients[row][column].drop(1)
                    matrix.coefficients[row][column] = tempResult
                }
                else {
                    val temp = matrix.coefficients[row][column]
                    matrix.coefficients[row][column] = "-$temp"
                }

                if(matrix.coefficients[row][column] == "-") {
                    matrix.coefficients[row][column] = "0"
                }
            }
            R.id.buttonDot -> {

                // don't add decimal if it already has one
                if(matrix.coefficients[row][column].contains(".")) {
                    return
                }

                // don't add a decimal if it has a fraction
                if(matrix.coefficients[row][column].contains("/")){
                    return
                }

                matrix.coefficients[row][column] += "."

                // if the first button pressed in a decimal, add a 0
                if(matrix.coefficients[row][column] == ".") {
                    matrix.coefficients[row][column] = "0."
                }
            }
            R.id.buttonFrac -> {

                // don't add a fraction if it has a / or decimal.
                if (matrix.coefficients[row][column].contains("/") || matrix.coefficients[row][column].contains(".") ) {
                    return
                }
                matrix.coefficients[row][column] += "/"

                // don't show / if it's the only text shown
                if(matrix.coefficients[row][column] == "/") {
                   matrix.coefficients[row][column] = "0"
                }
            }
            else -> {
                println("Error")
            }
        }

        // update the current coefficient with new number
        textViewResult = findViewById(equationIDs[row][column])
        textViewResult.text = matrix.coefficients[row][column]
    }

}