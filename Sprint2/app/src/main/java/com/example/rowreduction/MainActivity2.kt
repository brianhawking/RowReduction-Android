package com.example.rowreduction

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity2 : AppCompatActivity() {

    var resultContractForSwap = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

                // reset hint to 1
                hintNumber = 1
            }

            // update screen with new matrix
            updateScreen()
        }
    }

    var resultContractForConstant = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

                    // reset hint to 1
                    hintNumber = 1
                }
            }

            // update screen with new matrix
            updateScreen()
        }
    }

    var resultContractRowPlusConstantRow = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

                // reset hint to 1
                hintNumber = 1
            }

            // update screen with new matrix
            updateScreen()
        }
    }

    private var equationIDs = arrayOf(
        arrayOf(R.id.coefficientX1, R.id.coefficientY1,R.id.coefficientZ1, R.id.coefficientC1),
        arrayOf(R.id.coefficientX2, R.id.coefficientY2, R.id.coefficientZ2, R.id.coefficientC2),
        arrayOf(R.id.coefficientX3, R.id.coefficientY3, R.id.coefficientZ3, R.id.coefficientC3)
    )

    // determines which hint the user has available
    // options are 1, 2, 3
    var hintNumber = 1
    var matrix = Matrix()
    var matrices: Array<Matrix> = emptyArray()

    var row = 0
//    var column = 0
    var numberOfEquations: Int = 0
    var numberOfVariables: Int = 0

//    lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // get coefficients from previous activity
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

        undoButton.setOnClickListener {
            if(matrices.size > 1) {
                removeMatrixFromMatrices()
            }
        }

        hintButton.setOnClickListener {

            val intent = Intent(this,Hint::class.java)
            intent.putExtra("hintNumber", hintNumber)
            intent.putExtra("coefficients1", matrix.coefficients[0])
            intent.putExtra("coefficients2", matrix.coefficients[1])
            intent.putExtra("coefficients3", matrix.coefficients[2])
            intent.putExtra("numberOfEquations", numberOfEquations)
            intent.putExtra("numberOfVariables", numberOfVariables)

            hintNumber += 1
            if (hintNumber > 3) {
                hintNumber = 3
            }

            startActivity(intent)
        }

        // ===========================================================================

    }

    // add the new matrix from row operation to the matrices array
    fun addMatrixToMatrices(tempMatrix: Matrix) {

        // convert to mutable list so I can add a new element to array
        val mutableMatrices = matrices.toMutableList()
        mutableMatrices.add(tempMatrix)
        matrices = mutableMatrices.toTypedArray()
    }

    fun removeMatrixFromMatrices() {

        // delete last matrix from matrices
        val mutableMatrices = matrices.toMutableList()
        mutableMatrices.removeAt(matrices.size-1)
        matrices = mutableMatrices.toTypedArray()
        matrix = matrices.last()

        updateScreen()
    }

    // print matrix to console for debugging
    fun printMatrix(tempMatrix: Matrix) {
        for(row in tempMatrix.coefficients.indices) {
            for(column in tempMatrix.coefficientsAsRationals[row].indices) {
                print("${tempMatrix.coefficientsAsRationals[row][column]}")
            }
            print("\n")
        }
    }

    fun updateScreen() {

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

        // hide the z variable column
        if(numberOfVariables == 2) {
            // hide column 3
            for(i in equationIDs.indices) {
                val row: TextView = findViewById(equationIDs[i][2])
                row.visibility = View.GONE
            }
        }
    }



}