package com.example.rowreduction

class Matrix {

    var coefficients = arrayOf(
        arrayOf("0", "0", "0", "0"),
        arrayOf("0", "0", "0", "0"),
        arrayOf("0", "0", "0", "0")
    )

    var coefficientsAsRationals = arrayOf(
        arrayOf(Rational(0,1),Rational(0,1),Rational(0,1),Rational(0,1) ),
        arrayOf(Rational(0,1),Rational(0,1),Rational(0,1),Rational(0,1) ),
        arrayOf(Rational(0,1),Rational(0,1),Rational(0,1),Rational(0,1) )
    )

    fun copy() : Matrix {
        val temp = Matrix()
        for(row in coefficientsAsRationals.indices) {
            for (column in coefficientsAsRationals[row].indices) {
                temp.coefficientsAsRationals[row][column] = this.coefficientsAsRationals[row][column]
                temp.coefficients[row][column] = this.coefficients[row][column]
            }
        }

        return temp
    }

    fun stringToRational(i: Int, j: Int) : Boolean {

        if (this.coefficients[i][j] == "0") {
            this.coefficientsAsRationals[i][j].num = 0
            this.coefficientsAsRationals[i][j].den = 1
            return true
        }

        if (this.coefficients[i][j].contains("/")) {
            val numbers = coefficients[i][j].split("/")

            // check if the numerator and denominator are actual numbers
            return try {
                val numerator = numbers[0].toFloat()
                val denominator = numbers[1].toFloat()
                this.coefficientsAsRationals[i][j].num = numerator.toLong()
                this.coefficientsAsRationals[i][j].den = denominator.toLong()
                this.coefficientsAsRationals[i][j].reduce()
                println("REDUCE")
                true
            } catch (e: NumberFormatException) {
                println(e)
                false
            }
        }

        if (!this.coefficients[i][j].contains('.')) {
            this.coefficientsAsRationals[i][j].num = this.coefficients[i][j].toLong()
            this.coefficientsAsRationals[i][j].den = 1
            return true
        }

        val ds = this.coefficients[i][j].trimEnd('0').trimEnd('.')
        val index = ds.indexOf('.')
        if (index == -1) {
            this.coefficientsAsRationals[i][j].num = ds.toLong()
            this.coefficientsAsRationals[i][j].den = 1L
        }
        var num = ds.replace(".", "").toLong()
        var den = 1L
        for (n in 1 until ds.length - index) den *= 10L
        while (num % 2L == 0L && den % 2L == 0L) {
            num /= 2L
            den /= 2L
        }
        while (num % 5L == 0L && den % 5L == 0L) {
            num /= 5L
            den /= 5L
        }
        this.coefficientsAsRationals[i][j].num = num
        this.coefficientsAsRationals[i][j].den = den
//        println("$num/$den")

        this.coefficientsAsRationals[i][j].reduce()
        println("REDUCE THIS FRACTION")
        return true

    }

    fun swapRows(rowI: Int, rowJ: Int) {

        val i = rowI - 1
        val j = rowJ - 1

        // swap rows i and j
        // then convert rationals to strings
        for (column in coefficientsAsRationals[i].indices) {
            // store i row in tempRow
            val temp = this.coefficientsAsRationals[i][column]
            this.coefficientsAsRationals[i][column] = this.coefficientsAsRationals[j][column]
            this.coefficientsAsRationals[j][column] = temp
            this.coefficients[i][column] = this.coefficientsAsRationals[i][column].toString()
            this.coefficients[j][column] = this.coefficientsAsRationals[j][column].toString()
        }
    }

    fun multiplyRowByConstant(row: Int, constant: String) {

        var rational = Rational(0,1)

        if (rational.isRational(constant)) {
            rational = rational.stringToRational(constant)
            for (column in coefficientsAsRationals[row-1].indices) {
                val temp = coefficientsAsRationals[row-1][column] * rational
                this.coefficientsAsRationals[row-1][column] = temp
                this.coefficients[row-1][column] = this.coefficientsAsRationals[row-1][column].toString()
            }
        }
    }

    fun rowPlusConstantRow(finalRow: Int, constant: String, pivotRow: Int) {

        var rational = Rational(0,1)

        if(rational.isRational(constant)) {
            rational = rational.stringToRational(constant)
            for (column in coefficientsAsRationals[finalRow-1].indices) {
                val temp = coefficientsAsRationals[finalRow-1][column] + (rational * coefficientsAsRationals[pivotRow-1][column])
                this.coefficientsAsRationals[finalRow-1][column] = temp
                this.coefficients[finalRow-1][column] = this.coefficientsAsRationals[finalRow-1][column].toString()
            }
        }
    }

    // https://rosettacode.org/wiki/Convert_decimal_number_to_rational#Kotlin
//    fun decimalToRational(d: Double): Rational {
//        val ds = d.toString().trimEnd('0').trimEnd('.')
//        val index = ds.indexOf('.')
//        if (index == -1) return Rational(ds.toLong(), 1L)
//        var num = ds.replace(".", "").toLong()
//        var den = 1L
//        for (n in 1 until ds.length - index) den *= 10L
//        while (num % 2L == 0L && den % 2L == 0L) {
//            num /= 2L
//            den /= 2L
//        }
//        while (num % 5L == 0L && den % 5L == 0L) {
//            num /= 5L
//            den /= 5L
//        }
//
//        println("$num/$den")
//        return Rational(num, den)
//    }

}