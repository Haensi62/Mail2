package com.example.mail2

import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider

import androidx.core.view.WindowCompat
import com.example.mail2.ui.theme.Mail2Theme
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    lateinit var sSubject: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// enableEdgeToEdge sorgt dafür, dass der ganze Bildschirm benutzt wird,
// alles vom Betriebssystem wird ausgeblendet
// enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // setContentView(R.layout.activity_main)
        // return

        setContent {
           MyApp {
                Greeting("World")
            }
        }
    }


    // Haupt-App Composable
    @Composable
    fun MyApp(content: @Composable () -> Unit) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
                content() // Hier wird der übergebene Inhalt angezeigt
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewGreeting(){
        Greeting("Fuck You")
    }

    @Composable
    fun Greeting(name: String)
    {
        val view = LocalView.current

        Column () {
            Spacer(modifier = Modifier.height(28.dp))
                Row (modifier = Modifier.padding(all = 8.dp)) {
                    Button(onClick = { onClickBtn1(view) }) {
                        Text(text = "Stuff")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onClickBtn2(view) }) {
                        Text(text = "Strom")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onClickBtn3(view) }) {
                        Text(text = "Wasser")
                    }
                }
                Row (modifier = Modifier.padding(all = 8.dp)){
                    Button(onClick = { onClickBtn4(view) }) {
                        Text(text = "Heizung")
                    }
                }
            }
    }

    // Preview-Funktion für die IDE-Vorschau
    @Composable
    fun DefaultPreview() {
        MyApp {
            Greeting("Preview")
        }
    }

    lateinit var photoFile: File
    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Bild wurde erfolgreich aufgenommen und gespeichert
            val thread = Thread {

                try {
                    val sender = GMailSender(
                        "termikalf@gmail.com",
                        "oqqb nrnu lfei frzs"
                    )
                    sender.addAttachment(photoFile)
                    val subject = sSubject

                    val dateStamp: String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                    val timeStamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                    val message = "Das war die Datei am $dateStamp Tag um $timeStamp Uhr"
                    val recipients = "Hans-Joachim.Fritz@kerberos.de"
                    sender.sendMail(subject, message, "termikalf@gmail.com", recipients)
                    runOnUiThread{
                        Toast.makeText(applicationContext, "Email sent: $sSubject", Toast.LENGTH_LONG).show();
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            thread.start()
        }
    }

    // Methode zum Starten der Kamera und Bildaufnahme
    fun takePicture() {
        photoFile = createImageFile() // Erstelle eine Datei für das Bild
        val photoURI: Uri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
        takePictureLauncher.launch(photoURI)
    }

    // Bilddatei erstellen
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${timeStamp}",     ".jpg", /* Suffix */
            storageDir /* Verzeichnis */
        )
    }

    fun onClickBtn1(view: View){
        sSubject = "Stuff"
        takePicture()
    }

    fun onClickBtn2(view: View){
        sSubject = "Strom"
        takePicture()
    }

    fun onClickBtn3(view: View){
        sSubject = "Wasser"
        takePicture()
    }

    fun onClickBtn4(view: View){
        sSubject = "Heizung"
        takePicture()
    }

}

