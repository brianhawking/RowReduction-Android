package com.example.rowreduction

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts


/*
- What's happening in this activity
1. I have a variable called matrices, which is an array holding the current and previous matrices (states)
2. Everytime an operation is completed, I add the new state to the matrices array
3. I programmed an UNDO button, which removes the last matrix from the matrices array
4. Then the screen is updated, showing the previous matrix state
 */

/*
- How the operations work
1. There are three row reduction operations
2. When you select an operation, the current matrix state coefficients, number of equations,
number of variables are sent to that operation activity
3. The user will complete the operation and the information is sent back to this activity
4. The operation is completed, a new matrix state is added to matrices, and screen is updated
 */

class MainActivity2 : AppCompatActivity() {

    // this is called when data is returned from the swapRows activity screen
    // Operation: R_i <-> R_j
    // Data sent back: The two rows you want to swap
    // rows = array containing integers of the rows you want to swap
    private var resultContractForSwap = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult? ->
        if((result?.resultCode == Activity.RESULT_OK)){

            // get data back from activity
            val intent = result.data
            val bundle: Bundle? = intent?.extras
            val rows = bundle?.getIntArray("rows")

            // proceed if data is valid
            if (rows != null) {

                /* make copy of matrix
                swap rows
                add new matrix to matrices array
                copy new matrix back
                 */
                val tempMatrix = matrix.copy()
                tempMatrix.swapRows(rows[0],rows[1])
                addMatrixToMatrices(tempMatrix)
                matrix = tempMatrix.copy()
            }

            // update screen with new matrix
            updateScreen()
        }
    }

    // this is called when data is returned from the multiply by constant activity screen
    // Operation: constant * R_final = R_final
    // Data sent back: the constant and row
    // constant = what you want to multiply the row by
    // row = the row you want to multiply
    private var resultContractForConstant = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult? ->
        if((result?.resultCode == Activity.RESULT_OK)){

            // get data back from activity
            val intent = result.data
            val bundle: Bundle? = intent?.extras
            val constant = bundle?.getString("constant")
            val row = bundle?.getInt("row")

            // proceed if data is valid
            if (row != null) {
                if (constant != null) {

                    /* make copy of matrix
                    multiply row by constant
                    add new matrix to matrices array
                    copy new matrix back
                     */
                    val tempMatrix = matrix.copy()
                    tempMatrix.multiplyRowByConstant(row,constant)
                    addMatrixToMatrices(tempMatrix)
                    matrix = tempMatrix.copy()
                }
            }

            // update screen with new matrix
            updateScreen()
        }
    }

    // this is called when data is returned from the row plus constant row operation activity screen
    // Operation: R_final + constant * R_pivot = R_final
    // Data sent back: pivot row, row you want to change
    // pivotRow = pivot row
    // finalRow = row you want to change
    // constant = number you multiply the pivot row by
    private var resultContractRowPlusConstantRow = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult? ->
        if((result?.resultCode == Activity.RESULT_OK)){

            // get data back from activity
            val intent = result.data
            val bundle: Bundle? = intent?.extras
            val constant = bundle?.getString("constant")
            val finalRow = bundle?.getInt("finalRow")
            val pivotRow = bundle?.getInt("pivotRow")

            // proceed if data is valid
            if (pivotRow != null && finalRow != null && constant != null) {

                /* make copy of matrix
                perform operation
                add new matrix to matrices array
                copy new matrix back
                 */
                val tempMatrix = matrix.copy()
                tempMatrix.rowPlusConstantRow(finalRow, constant, pivotRow)
                addMatrixToMatrices(tempMatrix)
                matrix = tempMatrix.copy()

            }

            // update screen with new matrix
            updateScreen()
        }
    }

    // IDS of all the coefficients.
    // Used to loop through coefficients to update
    private var equationIDs = arrayOf(
        arrayOf(R.id.coefficientX1, R.id.coefficientY1,R.id.coefficientZ1, R.id.coefficientC1),
        arrayOf(R.id.coefficientX2, R.id.coefficientY2, R.id.coefficientZ2, R.id.coefficientC2),
        arrayOf(R.id.coefficientX3, R.id.coefficientY3, R.id.coefficientZ3, R.id.coefficientC3)
    )

    // current matrix state
    var matrix = Matrix()

    // array containing current and previous matrix states
    var matrices: Array<Matrix> = emptyArray()

    // current row in matrix
    var row = 0

    var numberOfEquations: Int = 0
    var numberOfVariables: Int = 0

//    lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // get coefficients (by row) from previous activity
        val bundle: Bundle? = intent.extras
        val coefficients1 = bundle?.getStringArray("coefficients1")
        val coefficients2 = bundle?.getStringArray("coefficients2")
        val coefficients3 = bundle?.getStringArray("coefficients3")
        numberOfEquations = bundle?.getInt("numberOfEquations")!!
        numberOfVariables = bundle.getInt("numberOfVariables")

        // get data from bundle. Store it in the matrix as string and rational
        for (i in matrix.coefficients.indices) {
            for (j in matrix.coefficients[i].indices) {
                when (i) {
                    0 ->  matrix.coefficients[i][j] = coefficients1?.get(j).toString()
                    1 ->  matrix.coefficients[i][j] = coefficients2?.get(j).toString()
                    2 ->  matrix.coefficients[i][j] = coefficients3?.get(j).toString()
                    else -> println("SOMETHING BAD HAPPENED")
                }
                // create the Rational version of the number
                matrix.stringToRational(i,j)
            }
        }


        // display data to the matrix on screen
        addMatrixToMatrices(matrix.copy())
        printMatrix(matrix.copy())
        updateScreen()

        // set listeners on the operation buttons ==================================================
        val swapRowsButton: ImageButton = findViewById(R.id.swapRows)
        val multByConstantButton: ImageButton = findViewById(R.id.multByConstant)
        val rowPlusConstantRow: ImageButton = findViewById(R.id.rowPlusConstantRow)
        val undoButton: Button = findViewById(R.id.undoButton)
        val hintButton: Button = findViewById(R.id.hint)

        swapRowsButton.setOnClickListener {
            val intent = Intent(this,SwapRows::class.java)
            intent.putExtra("numberOfEquations",numberOfEquations)
            resultContractForSwap.launch(intent)
        }

        multByConstantButton.setOnClickListener {
            val intent = Intent(this,MultiplyByConstantActivity::class.java)
            intent.putExtra("numberOfEquations",numberOfEquations)
            resultContractForConstant.launch(intent)
        }

        rowPlusConstantRow.setOnClickListener {
            val intent = Intent(this,RowPlusConstantRow::class.java)
            intent.putExtra("numberOfEquations", numberOfEquations)
            resultContractRowPlusConstantRow.launch(intent)
        }

        // remove current state from matrices, if it's not the initial matrix
        undoButton.setOnClickListener {
            if(matrices.size > 1) {
                removeMatrixFromMatrices()
            }
        }

        // send all data to hint screen
        // *** Haven't done this one yet ***
        hintButton.setOnClickListener {
            // go to hint screen
//            val intent = Intent(this,Hint::class.java)
//            intent.putExtra("coefficients1", matrix.coefficients[0])
//            intent.putExtra("coefficients2", matrix.coefficients[1])
//            intent.putExtra("coefficients3", matrix.coefficients[2])
//            intent.putExtra("numberOfEquations", numberOfEquations)
//            intent.putExtra("numberOfVariables", numberOfVariables)
//            startActivity(intent)
        }

        // ===========================================================================

    }

    // add the new matrix from row operation to the matrices array
    private fun addMatrixToMatrices(tempMatrix: Matrix) {

        // convert to mutable list so I can add a new element to array
        val mutableMatrices = matrices.toMutableList()
        mutableMatrices.add(tempMatrix)
        matrices = mutableMatrices.toTypedArray()
    }

    private fun removeMatrixFromMatrices() {

        // delete last matrix from matrices
        val mutableMatrices = matrices.toMutableList()
        mutableMatrices.removeAt(matrices.size-1)
        matrices = mutableMatrices.toTypedArray()

        // set current matrix state to last one in matrices
        matrix = matrices.last()

        updateScreen()
    }

    // print matrix to console for debugging
    private fun printMatrix(tempMatrix: Matrix) {
        for(row in tempMatrix.coefficients.indices) {
            for(column in tempMatrix.coefficientsAsRationals[row].indices) {
                print("${tempMatrix.coefficientsAsRationals[row][column]}")
            }
            print("\n")
        }
    }

    private fun updateScreen() {

        // grab all the textviews, update with new matrix value
        for(i in equationIDs.indices) {
            for(j in equationIDs[i].indices) {
                val box: TextView = findViewById(equationIDs[i][j])
                box.text = matrix.coefficientsAsRationals[i][j].toString()
            }
        }

        // hide equation 3
        if(numberOfEquations == 2) {
            // hide row 3
            val row3: TableRow = findViewById(R.id.row3)
            row3.visibility = View.GONE
            val row3Divider: View = findViewById(R.id.row3Divider)
            row3Divider.visibility = View.GONE
        }

        // hide the z variable column, if needed
        if(numberOfVariables == 2) {
            // hide column 3
            for(i in equationIDs.indices) {
                val row: TextView = findViewById(equationIDs[i][2])
                row.visibility = View.GONE
            }
        }
    }

}