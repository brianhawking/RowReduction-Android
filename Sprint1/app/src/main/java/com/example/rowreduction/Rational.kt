package com.example.rowreduction

/*
1. Instead of using integers, doubles, etc, I created a Rational object
2. Every number that can typed on the calculator can be written as a fraction (i.e., rational)
3. Rational = (numerator, denominator)
4. Ex. 4 = 4/1 = (4,1), 2/3 = (2,3), 0 = 0/1 = (0,1), 0.333... = (1,3)
 */

class Rational(var num: Long, var den: Long)  {
    override fun toString() : String {

        // when a rational is created, immediately reduce it to lowest terms
        // Ex. 4/6 = 2/3
        this.reduce()

        // if the numerator is negative, don't do anything
        // if the denominator is negative, make it positive and numerator negative
        if (den < 0) {
            den *= -1L
            num *= -1L
        }

        // if denominator = 1, don't display the denominator to user. Ex. 4/1 = 4
        return if (den == 1L) {
            "$num"
        } else {
            "$num/$den"
        }
    }

    // reduce the rational to lowest terms
    // return it as a Rational
    fun returnedReduced(): Rational {
        var x = this.num
        var y = this.den
        val d = gcd(x,y)
        x /= d
        y /= d
        return Rational(x,y)
    }

    // reduce the rational to lowest terms
    // doesn't return anything
    fun reduce() {
        var x = this.num
        var y = this.den
        val d = gcd(x,y)
        x /= d
        y /= d
        this.num = x
        this.den = y
    }

    // needed to reduce fraction to lowest terms
    private fun gcd(a: Long, b: Long): Long {
        return if (b == 0L) a else gcd(b, a % b)
    }

    // take in a string and check if it's rational
    fun isRational(number: String) : Boolean {
        
        val rational = Rational(0,1)

        if (number == "") {
            return false
        }

        if (number == "0") {
            rational.num = 0
            rational.den = 1
            return true
        }

        if (number.contains("/")) {
            val numbers = number.split("/")

            // check if the numerator and denominator are actual numbers
            return try {
                val numerator = numbers[0].toFloat()
                val denominator = numbers[1].toFloat()
                rational.num = numerator.toLong()
                rational.den = denominator.toLong()
                true
            } catch (e: NumberFormatException) {
                println(e)
                false
            }
        }

        if (!number.contains('.')) {
            rational.num = number.toLong()
            rational.den = 1
            return true
        }

        val ds = number.trimEnd('0').trimEnd('.')
        val index = ds.indexOf('.')
        if (index == -1) {
            rational.num = ds.toLong()
            rational.den = 1L
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
        rational.num = num
        rational.den = den

        return true

    }

    // convert the string to rational
    // return the string number as a rational
    // ex. "2/3" = (2,3)
    fun stringToRational(number: String) : Rational {

        val rational = Rational(0,1)

        if (number == "0") {
            rational.num = 0
            rational.den = 1
            return Rational(0,1)
        }

        if (number.contains("/")) {
            val numbers = number.split("/")

            // check if the numerator and denominator are actual numbers
            return try {
                val numerator = numbers[0].toFloat()
                val denominator = numbers[1].toFloat()
                rational.num = numerator.toLong()
                rational.den = denominator.toLong()
                Rational(rational.num,rational.den)
            } catch (e: NumberFormatException) {
                println(e)
                Rational(0,1)
            }
        }

        if (!number.contains('.')) {
            rational.num = number.toLong()
            rational.den = 1
            return Rational(rational.num, rational.den)
        }

        val ds = number.trimEnd('0').trimEnd('.')
        val index = ds.indexOf('.')
        if (index == -1) {
            rational.num = ds.toLong()
            rational.den = 1L
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
        rational.num = num
        rational.den = den

        return Rational(rational.num, rational.den)

    }

    // flip the fraction
    // ex. Reciprocal of 2/3 is 3/2
    fun reciprocal() : Rational {
        return Rational(this.den, this.num).returnedReduced()
    }

    // multiply the numerator by -1
    fun negate(): Rational {
        return Rational(-1*num, den)
    }


    // overloading operator

    // a + b
    operator fun plus(other: Rational) : Rational {
        return Rational((num * other.den)+(den * other.num), den * other.den).returnedReduced()
    }

    // a - b
    operator fun minus(other: Rational) : Rational {
        return Rational((num * other.den)-(den * other.num), den * other.den).returnedReduced()
    }

    // a * b
    operator fun times(other: Rational) : Rational {
        return Rational(num * other.num, den * other.den).returnedReduced()
    }

    // a / b
    operator fun div(other: Rational) :Rational {
        return Rational(num * other.den, den * other.num).returnedReduced()
    }

    // couldn't figure out how to overload the = operator
    // wrote a function that checks true if the numbers are the same
    fun equals(other: Rational) : Boolean {
        return num == other.num && den == other.den
    }
}