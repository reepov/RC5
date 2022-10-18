package com.example.rc5

import android.widget.EditText
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

@OptIn(ExperimentalUnsignedTypes::class)
class RC5 {
    private val wordCount: Byte = 32
    private val u = (wordCount / 8).toByte()
    private val goldenRatio: Double = (1 + sqrt(5.0)) / 2
    private val nedoP: Double = (Math.E - 2) * (2.0).pow(wordCount.toDouble())
    private val nedoQ: Double = (goldenRatio - 1) * (2.0).pow(wordCount.toDouble())
    private val rounds: Byte = 12
    private val t = (2 * (rounds + 1)).toByte()
    private val keySize: Byte = 16
    private val c = (if (keySize % u > 0) keySize / u + 1 else keySize / u).toByte()
    private val mod: UInt = (2.0).pow(wordCount.toDouble()).toUInt()
    private var keyBytes: ArrayList<Byte> = ArrayList()

    private var L = UIntArray(c.toInt())
    private var S = UIntArray(t.toInt())
    private val P: UInt = if (round(nedoP) % 2 != 0.0) round(nedoP).toUInt() else if (round(nedoP) > nedoP) round(nedoP).toUInt() - 1u else round(nedoP).toUInt() + 1u
    private val Q: UInt = if (round(nedoQ) % 2 != 0.0) round(nedoQ).toUInt() else if (round(nedoQ) > nedoQ) round(nedoQ).toUInt() - 1u else round(nedoQ).toUInt() + 1u

    fun FillLSArrays() : UIntArray {
        S = UIntArray(t.toInt())
        L = UIntArray(c.toInt())
        L[c - 1] = 0u
        for (ind in keyBytes.size - 1 downTo 0) {
            L[ind / u] = ROL(L[ind / u], 8) + keyBytes[ind].toUInt()
        }
        S[0] = P
        for (ind in 1 until t) {
            S[ind] = (S[ind - 1] + Q) % mod
        }
        var x: UInt = 0u
        var y: UInt = 0u
        var i = 0
        var j = 0
        val n: Int = 3 * max(t.toInt(), c.toInt())
        for (k in 0 until n) {
            S[i] = ROL((S[i] + x + y) % mod, 3) % mod
            x = S[i]
            L[j] = ROL((L[j] + x + y) % mod, ((x + y) % wordCount.toUInt()).toInt()) % mod
            y = L[j]
            i = (i + 1) % t
            j = (j + 1) % c
        }
        return S
    }
    fun setKeyBytes(keyText : EditText, keyIsHex:Boolean, keyIsASCII:Boolean) : ArrayList<Byte>{
        keyBytes.clear()
        if(keyIsHex)
        {
            val item = keyText.text.split(' ')
            val maxind: Int = item.size
            for (ind in 0 until maxind) {
                keyBytes.add(item[ind].toInt(16).toByte())
            }
        }
        else if (keyIsASCII)
        {
            val item = keyText.text.toString().toCharArray()
            val maxind : Int = item.size
            for (ind in 0 until maxind)
            {
                keyBytes.add(item[ind].code.toByte())
            }
        }
        return keyBytes
    }
    private fun setAB(mainText : EditText, textIsHex : Boolean, textIsASCII : Boolean) : Pair<UInt, UInt>{
        var a : UInt = 0u
        var b : UInt = 0u
        if(textIsHex)
        {
            a = (mainText.text.split(' ')[0]).toUInt(16)
            b = (mainText.text.split(' ')[1]).toUInt(16)
        }
        else if(textIsASCII)
        {
            val temp = mainText.text.toString()
            val tempA = temp.substring(0, temp.length / 2)
            val tempB = temp.substring(temp.length / 2)
            var strA = ""
            var strB = ""
            tempA.forEach {
                strA += it.code.toString(16)
            }
            tempB.forEach {
                strB += it.code.toString(16)
            }
            a = strA.toUInt(16)
            b = strB.toUInt(16)
        }
        return a to b
    }
    fun decrypt(mainText : EditText, keyText: EditText, keyIsHex: Boolean, keyIsASCII: Boolean, textIsHex: Boolean, textIsASCII: Boolean) : Pair<String, String> {
        keyBytes = setKeyBytes(keyText,keyIsHex, keyIsASCII)
        S = FillLSArrays()
        val ab = setAB(mainText, textIsHex, textIsASCII)
        var a = ab.first
        var b = ab.second
        for (i in rounds downTo 1) {
            b = ROL(b - S[2 * i + 1], 32 - (a % wordCount.toUInt()).toInt()) xor a
            a = ROL(a - S[2 * i], 32 - (b % wordCount.toUInt()).toInt()) xor b
        }
        b -= S[1]
        a -= S[0]
        var temp = ""
        var res = ""
        for (i in 0 until wordCount / 4 step 2) {
            temp = a.toString(16).padStart(8, '0')[i].toString() + a.toString(16).padStart(8, '0')[i + 1].toString()
            res += temp.toInt(16).toChar()
        }
        for (i in 0 until wordCount / 4 step 2) {
            temp = b.toString(16).padStart(8, '0')[i].toString() + b.toString(16).padStart(8, '0')[i + 1].toString()
            res += temp.toInt(16).toChar()
        }
        return (a.toString(16).padStart(8, '0') + " " + b.toString(16).padStart(8, '0')).uppercase() to res
    }
    fun encrypt(mainText: EditText, keyText: EditText, keyIsHex: Boolean, keyIsASCII: Boolean, textIsHex: Boolean, textIsASCII: Boolean) : Pair<String, String>{
        keyBytes = setKeyBytes(keyText,keyIsHex, keyIsASCII)
        S = FillLSArrays()
        val ab = setAB(mainText, textIsHex, textIsASCII)
        var a = (ab.first + S[0]) % mod
        var b = (ab.second + S[1]) % mod
        for (ind in 1 until rounds + 1) {
            a = (ROL(a xor b, (b % wordCount.toUInt()).toInt()) + S[2 * ind]) % mod
            b = (ROL(b xor a, (a % wordCount.toUInt()).toInt()) + S[2 * ind + 1]) % mod
        }
        var temp = ""
        var res = ""
        for (i in 0 until wordCount / 4 step 2) {
            temp = a.toString(16).padStart(8, '0')[i].toString() + a.toString(16).padStart(8, '0')[i + 1].toString()
            res += temp.toInt(16).toChar()
        }
        for (i in 0 until wordCount / 4 step 2) {
            temp = b.toString(16).padStart(8, '0')[i].toString() + b.toString(16).padStart(8, '0')[i + 1].toString()
            res += temp.toInt(16).toChar()
        }
        return (a.toString(16) + " " + b.toString(16)).uppercase() to res
    }
    private fun ROL(a: UInt, newOffset: Int): UInt {
        val bitarr = a.toString(2)
        val arr = ByteArray(wordCount.toInt())
        val res = ByteArray(wordCount.toInt())
        for (i in bitarr.length - 1 downTo 0) arr[wordCount - (bitarr.length - i)] =
            bitarr[i].toString().toByte()
        if (newOffset == 0) return a
        System.arraycopy(arr, newOffset, res, 0, arr.size - newOffset)
        System.arraycopy(arr, 0, res, arr.size - newOffset, newOffset)
        var toConv = ""
        res.forEach {
            toConv += it.toString()
        }
        return toConv.toUInt(2)
    }
}