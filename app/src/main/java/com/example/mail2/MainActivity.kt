package com.example.mail2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mail2.ui.theme.Mail2Theme
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*setContent {
            Mail2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Hans-Joachim Fritz",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }*/

// get reference to button
        val btn_click_me = findViewById(R.id.button2) as Button
// set on-click listener
        btn_click_me.setOnClickListener {
            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickBtn2(view: View){
        Toast.makeText(this@MainActivity, "Button 2 wurde geklickt.", Toast.LENGTH_LONG).show()

        val thread = Thread {
            try {
                val sender = GMailSender(
                    "termikalf@gmail.com",
                    "oqqb nrnu lfei frzs"
                )

                val subject = "Mein Gott"
                val message = "Herrgott"
                val recipients = "Hans-Joachim.Fritz@kerberos.de"
                sender.sendMail(subject, message, "termikalf@gmail.com", recipients)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Thread starten
        thread.start()
    }





}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Mail2Theme {
        Greeting("Android")
    }
}