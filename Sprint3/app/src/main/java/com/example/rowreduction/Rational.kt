package com.example.rowreduction

class Rational(var num: Long, var den: Long)  {
    override fun toString() : String {

        this.reduce()

        println("NUM: $num")
        println("DEN: $den")

        if (den < 0) {
            den *= -1L
            num *= -1L
        }

        return if (den == 1L) {
            "$num"
        } else {
            "$num/$den"
        }
    }

    fun returnedReduced(): Rational {
        var x = this.num
        var y = this.den
        val d = gcd(x,y)
        x /= d
        y /= d
        return Rational(x,y)
    }

    fun reduce() {
        var x = this.num
        var y = this.den
        val d = gcd(x,y)
        x /= d
        y /= d
        this.num = x
        this.den = y
    }

    private fun gcd(a: Long, b: Long): Long {
        return if (b == 0L) a else gcd(b, a % b)
    }

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
//        println("$num/$den")

        return true

    }

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
            return return try {
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
//        println("$num/$den")

        return Rational(rational.num, rational.den)

    }

    fun reciprocal() : Rational {
        return Rational(this.den, this.num).returnedReduced()
    }

    fun negate(): Rational {
        return Rational(-1*num, den)
    }


    operator fun plus(other: Rational) : Rational {
        return Rational((num * other.den)+(den * other.num), den * other.den).returnedReduced()
    }

    operator fun minus(other: Rational) : Rational {
        return Rational((num * other.den)-(den * other.num), den * other.den).returnedReduced()
    }

    operator fun times(other: Rational) : Rational {
        return Rational(num * other.num, den * other.den).returnedReduced()
    }

    operator fun div(other: Rational) :Rational {
        return Rational(num * other.den, den * other.num).returnedReduced()
    }

    fun equals(other: Rational) : Boolean {
        return num == other.num && den == other.den
    }
}