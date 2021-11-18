package com.example.rowreduction

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView

/* - What's happening in this activity
1. Displays the rows to the user
2. User selects two rows they want to swap
3. This activity returns two integers to previous activity
 */
class SwapRows : AppCompatActivity() {

    // imageView showing the selected images
    var imageIDs = arrayOf(
        R.id.firstRowImage,
        R.id.secondRowImage
    )

    // row buttons for user to select
    var buttonIDs = arrayOf(
        R.id.row1Button,
        R.id.row2Button,
        R.id.row3Button
    )

    // initialize array containing the rows you want to swap
    var swap = intArrayOf(0,0)

    var numberOfEquations = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swap_rows)

        // get number of equations so it knows how many row buttons to display
        val bundle: Bundle? = intent.extras
        numberOfEquations = bundle?.getInt("numberOfEquations")!!

        // hide initial rows
        val firstImageView: ImageView = findViewById(R.id.firstRowImage)
        val secondImageView: ImageView = findViewById(R.id.secondRowImage)
        firstImageView.visibility = View.INVISIBLE
        secondImageView.visibility = View.INVISIBLE

        // set up listeners for row buttons
        for(i in buttonIDs.indices) {
            val b: Button = findViewById(buttonIDs[i])
            b.setOnClickListener {
                // user selects a row. it's added to the swap rows array
                rowChosen(buttonIDs[i])
            }
        }
        if(numberOfEquations == 2) {
            // hide row 3 button if there are only two equations
            val b: Button = findViewById(buttonIDs[2])
            b.visibility = View.GONE
        }

        val cancelButton: ImageButton = findViewById(R.id.cancelSwap)
        cancelButton.setOnClickListener {
            // go back without sending any data
            prepareReturn(false)
        }

        val confirmSwap: ImageButton = findViewById(R.id.confirmSwap)
        confirmSwap.setOnClickListener {
            // go back with data
            prepareReturn(true)
        }

    }

    // user wants to return. get the rows array ready
    fun prepareReturn(b: Boolean) {

        if (b) {

            // check if data can be sent back
                if (swap.contains(0)) {
                    return
                }

            // array contains the rows user wants to swap
            val array = intArrayOf(0,0)
            for(i in swap.indices) {
                when (swap[i]) {
                    R.id.row1Button -> array[i] = 1
                    R.id.row2Button -> array[i] = 2
                    R.id.row3Button -> array[i] = 3
                }
            }

            val data = Intent().apply {
                putExtra("rows",array)
            }
            setResult(Activity.RESULT_OK, data)
        }
        else {
            // set up return with no data
            setResult(Activity.RESULT_CANCELED)
        }

        finish()

    }

    // user selects a row, add it to rows array
    // covers what happens if user already selected it
    // covers deselecting a row
    private fun rowChosen(id: Int) {
        if(swap.contains(id)) {

            // get index (either 0 or 1)
            val index = swap.indexOf(id)

            // make it 0
            swap[index] = 0

            // hide imageview
            val image: ImageView = findViewById(imageIDs[index])
            image.visibility = View.INVISIBLE
        }
        else if(swap.contains(0)){

            // get index of open slot
            val index = swap.indexOf(0)

            // put id in open slot
            swap[index] = id

            // show imageview
            val image: ImageView = findViewById(imageIDs[index])
            image.visibility = View.VISIBLE

            // change image to correct row
            when(id) {
                R.id.row1Button -> image.setImageResource(R.drawable.r1)
                R.id.row2Button -> image.setImageResource(R.drawable.r2)
                R.id.row3Button -> image.setImageResource(R.drawable.r3)
            }
        }
    }


}