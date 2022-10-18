package com.example.rc5

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    private val rc5 = RC5()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val encrypt = findViewById<Button>(R.id.encrypt)
        val decrypt = findViewById<Button>(R.id.decrypt)
        val keyText = findViewById<EditText>(R.id.EnterKey)
        val mainText = findViewById<EditText>(R.id.EnterText)
        val result = findViewById<TextView>(R.id.result)
        val resultAscii = findViewById<TextView>(R.id.resultAscii)
        val radioGroup1 = findViewById<RadioGroup>(R.id.choosingText)
        val radioGroup2 = findViewById<RadioGroup>(R.id.choosingKey)
        val textHexadecimal = findViewById<RadioButton>(R.id.choose_1)
        val textASCII = findViewById<RadioButton>(R.id.choose_2)
        val keyHexadecimal = findViewById<RadioButton>(R.id.chooseKey_1)
        val keyASCII = findViewById<RadioButton>(R.id.chooseKey_2)
        val enterResult = findViewById<Button>(R.id.enterResult)
        val enterResultAscii = findViewById<Button>(R.id.enterResultAscii)
        var textIsHex: Boolean
        var textIsASCII = false
        var keyIsHex = false
        var keyIsASCII = false

        encrypt.setOnClickListener {
            keyIsASCII = false
            keyIsHex = false
            textIsASCII = false
            textIsHex = false
            val idText: Int = radioGroup1.checkedRadioButtonId
            if (idText !=-1){
                val radio : RadioButton = findViewById(idText)
                when(radio.text)
                {
                    textHexadecimal.text -> textIsHex = true
                    textASCII.text -> textIsASCII = true
                }
            }
            else {
                Toast.makeText(this, "Не выбран тип текста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val idKey: Int = radioGroup2.checkedRadioButtonId
            if (idKey !=-1){
                val radio : RadioButton = findViewById(idKey)
                when(radio.text)
                {
                    keyHexadecimal.text -> keyIsHex = true
                    keyASCII.text -> keyIsASCII = true
                }
            }
            else {
                Toast.makeText(this, "Не выбран тип ключа", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val res = rc5.encrypt(mainText, keyText, keyIsHex, keyIsASCII, textIsHex, textIsASCII)
            result.text = res.first
            resultAscii.text = res.second
        }
        decrypt.setOnClickListener {
            keyIsASCII = false
            keyIsHex = false
            textIsASCII = false
            textIsHex = false
            val idText: Int = radioGroup1.checkedRadioButtonId
            if (idText !=-1){
                val radio : RadioButton = findViewById(idText)
                when(radio.text)
                {
                    textHexadecimal.text -> textIsHex = true
                    textASCII.text -> textIsASCII = true
                }
            }
            else {
                Toast.makeText(this, "Не выбран тип текста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val idKey: Int = radioGroup2.checkedRadioButtonId
            if (idKey !=-1){
                val radio : RadioButton = findViewById(idKey)
                when(radio.text)
                {
                    keyHexadecimal.text -> keyIsHex = true
                    keyASCII.text -> keyIsASCII = true
                }
            }
            else {
                Toast.makeText(this, "Не выбран тип ключа", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val res = rc5.decrypt(mainText, keyText, keyIsHex, keyIsASCII, textIsHex, textIsASCII)
            result.text = res.first
            resultAscii.text = res.second
        }
        enterResult.setOnClickListener {
            mainText.setText(result.text.toString())
        }
        enterResultAscii.setOnClickListener {
            mainText.setText(resultAscii.text.toString())
        }
    }
}