package com.example.mail2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mail2.ui.theme.Mail2Theme
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

         pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Hier können Sie das Resultat verarbeiten
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                // Beispiel: URI in eine Datei umwandeln oder anzeigen
                selectedImageUri?.let { uri ->
                    val fileName = getFileNameFromUri(uri)
                    Log.d("File Name", "Selected file name: $fileName")
                    Toast.makeText(this@MainActivity, fileName, Toast.LENGTH_LONG).show()
                }
            }
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
                    val subject = "Stuff"

                    val dateStamp: String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                    val timeStamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                    val message = "Das war die Datei am $dateStamp Tag um $timeStamp Uhr"
                    val recipients = "Hans-Joachim.Fritz@kerberos.de"
                    sender.sendMail(subject, message, "termikalf@gmail.com", recipients)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
//            sendEmailWithAttachment(photoFile)
        }
    }

    fun sendEmailWithAttachment(file: File) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_SUBJECT, "Subject: Bild von meiner App")
            putExtra(Intent.EXTRA_TEXT, "Hier ist das Bild, das ich aufgenommen habe.")

            // Datei als Anhang hinzufügen
            val fileUri: Uri = FileProvider.getUriForFile(this@MainActivity, "${packageName}.provider", file)
            putExtra(Intent.EXTRA_STREAM, fileUri)

            // Zugriff auf die Datei erlauben
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(emailIntent, "Bild per E-Mail senden..."))
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



    fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }



    fun onClickBtn2(view: View){
        Toast.makeText(this@MainActivity, "Button 2 wurde geklickt, sending Mail.", Toast.LENGTH_LONG).show()

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
        thread.start()
    }

    fun onClickBtn1(view: View){
        takePicture()
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