package com.example.pfc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPierre = findViewById<ImageButton>(R.id.btnPierre)
        val btnFeuille = findViewById<ImageButton>(R.id.btnFeuille)
        val btnCiseaux = findViewById<ImageButton>(R.id.btnCiseaux)

        btnPierre.setOnClickListener {
            CoroutineScope(IO).launch {
                playWithServer("1")
            }
        }

        btnFeuille.setOnClickListener {
            CoroutineScope(IO).launch {
                playWithServer("2")
            }
        }

        btnCiseaux.setOnClickListener {
            CoroutineScope(IO).launch {
                playWithServer("3")
            }
        }
    }

    private fun setUpdateUI(computChoice: String, computResult: String) {
        when {
            computChoice.contains("1") -> {
                val imgComput = findViewById<ImageView>(R.id.comput)
                imgComput.setImageResource(R.drawable.pierre)
            }

            computChoice.contains("2") -> {
                val imgComput = findViewById<ImageView>(R.id.comput)
                imgComput.setImageResource(R.drawable.feuille)
            }

            computChoice.contains("3") -> {
                val imgComput = findViewById<ImageView>(R.id.comput)
                imgComput.setImageResource(R.drawable.ciseaux)
            }
        }
        when (computResult) {
            "WON" -> {
                val imgResult = findViewById<ImageView>(R.id.result)
                imgResult.setImageResource(R.drawable.winner)
            }
            "LOST" -> {
                val imgResult = findViewById<ImageView>(R.id.result)
                imgResult.setImageResource(R.drawable.looser)
            }
            "DRAW" -> {
                val imgResult = findViewById<ImageView>(R.id.result)
                imgResult.setImageResource(R.drawable.draw)
            }
        }
    }

    private suspend fun setUpdateMainThread(computChoice: String, computResult: String) {
        withContext(Dispatchers.Main) {
            setUpdateUI(computChoice, computResult)
        }
    }

    private suspend fun playWithServer(choix: String) {
        var computChoice: String = ""
        var playerChoice: String = ""
        var computResult: String = ""
        val socket: Socket = Socket("172.16.0.68", 2005)
        val writer: OutputStream = socket.getOutputStream()
        val reader: Scanner = Scanner(socket.getInputStream())
        try {
            reader.nextLine() // réception TRY
            writer.write((choix + '\n').toByteArray(Charset.defaultCharset())) // envoi du choix joueur
            computChoice = reader.nextLine()
            playerChoice = reader.nextLine()
            computResult = reader.nextLine()
        } catch (ex: Exception) {
        } finally {
            writer.close()
            reader.close()
            socket.close()
        }
        setUpdateMainThread(computChoice, computResult)
    }
}
