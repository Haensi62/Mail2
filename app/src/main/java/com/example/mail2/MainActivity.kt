package com.example.mail2

import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mail2.data.MailerViewModel
import com.example.mail2.mailer.GMailSender
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale



/**
 * enum values that represent the screens in the app
 */
enum class Mail2Screen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.enter_email_account),
    Pickup(title = R.string.enter_target),
    Summary(title = R.string.show_log)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    currentScreen: Mail2Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun Mail2App(
    viewModel: MailerViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    var text by remember { mutableStateOf("Hans-Joachim.Fritz@kerberos.de") }
    var text1 by remember { mutableStateOf("Waldemar.Hirsch") }

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = Mail2Screen.valueOf(
        backStackEntry?.destination?.route ?: Mail2Screen.Start.name
    )

    Scaffold(
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = Mail2Screen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = Mail2Screen.Start.name) {
                StartButtonScreen(5,
                    onNextButtonClick = {navController.navigate(Mail2Screen.Flavor.name)},
                    modifier = Modifier
                        .fillMaxSize())
            }
            composable(route = Mail2Screen.Flavor.name
            ) {
                EnterAccountInfoScreen(viewModel,
                    onNextClick = {navController.navigate(Mail2Screen.Pickup.name)},
                    onPrevClick = { navController.navigateUp() }
                )
            }
            composable(route = Mail2Screen.Pickup.name) {
                EnterButtonConfig(
                    onNextButtonClick = {  },
                    onButtonUp = { navController.navigateUp() })
            }
            composable(route = Mail2Screen.Summary.name) {
                Column {
                    Text("Vierte Screen")
                    HorizontalDivider(color = Color.Gray, thickness = 2.dp)
                    Text("Noch ein Text")
                    HorizontalDivider(color = Color.Gray, thickness = 2.dp)
                    Button(onClick = {
                        navController.navigateUp()
                    }) {
                        Text("Zurück")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartButtonScreenPreview(){
    StartButtonScreen(
        numberOption = 0,
        onNextButtonClick = {},
        modifier = Modifier
    )
}

@Composable
fun StartButtonScreen(
    numberOption: Int,                          // Number of Buttons to handle
    onNextButtonClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column {
        Text("Button Screen")
        HorizontalDivider(color = Color.Gray, thickness = 16.dp)
        Text("Noch ein Text")
        HorizontalDivider(color = Color.Gray, thickness = 16.dp)
        Button(onClick = onNextButtonClick) {
            Text("Vor")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnterAccountInfoPreview(){
    var text by remember { mutableStateOf("Hans-Joachim.Fritz@kerberos.de") }
    var text1 by remember { mutableStateOf("Waldemar.Hirsch") }

    EnterAccountInfo(
        text,
        text1,
        onUsernameChange = { newText -> text = newText },
        onPasswordChange = { newText ->  text1 = newText },
        onNextButtonClick = {},
        onButtonUp = {}
    )
}

@Composable
fun EnterAccountInfoScreen(mail2ViewModel: MailerViewModel,
                           onNextClick: () -> Unit,
                           onPrevClick: () -> Unit){

    val name: String by mail2ViewModel.name.observeAsState("")
    val passWd: String by mail2ViewModel.passWd.observeAsState("")

    EnterAccountInfo(name,
                    passWd,
                    onUsernameChange = {mail2ViewModel.onNameChange(it)},
                    onPasswordChange = { mail2ViewModel.onPassWdChange(it) },
                    onNextButtonClick = onNextClick,
                    onButtonUp = onPrevClick
    )
}

@Composable
fun EnterAccountInfo(
    text: String,
    text1: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNextButtonClick: () -> Unit,
    onButtonUp: () -> Unit ){

    Column {
        Text("Sending Account Info")
        HorizontalDivider(color = Color.Gray, thickness = 16.dp)
        Text("Noch ein Text")
        HorizontalDivider(color = Color.Gray, thickness = 16.dp)

        // OutlinedTextField zur Eingabe
        OutlinedTextField(
            value = text,
            onValueChange = onUsernameChange,  // Textänderung abfangen und weitergeben
            label = { Text("E-mail Account Username") },
            modifier = Modifier.fillMaxWidth() // Das Textfeld füllt die volle Breite aus
        )
        HorizontalDivider(color = Color.Gray, thickness = 2.dp)
        OutlinedTextField(
            value = text1,
            onValueChange = onPasswordChange,
            label = { Text("E-mail Account Password") },
            modifier = Modifier.fillMaxWidth() // Das Textfeld füllt die volle Breite aus
        )
        HorizontalDivider(color = Color.Gray, thickness = 2.dp)
        Row {
            Button(onClick = onNextButtonClick) {
                Text("Vor")
            }
            VerticalDivider(color = Color.Gray, thickness = 2.dp)
            Button(onClick = onButtonUp) {
                Text("kkk")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EnterButtonConfigPreview(){
    EnterButtonConfig(
        onNextButtonClick = {},
        onButtonUp = {}
    )
}

@Composable
fun EnterButtonConfig(
    onNextButtonClick: () -> Unit,
    onButtonUp: () -> Unit ){

    Column {

        var text by remember { mutableStateOf("") }
        var text1 by remember { mutableStateOf("") }

        Text("Target Configuration")
        HorizontalDivider(color = Color.Gray, thickness = 16.dp)

        // OutlinedTextField zur Eingabe
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText // Aktualisiere den Zustand mit dem neuen Text
            },
            label = { Text("E-mail Account Username") },
            modifier = Modifier.fillMaxWidth() // Das Textfeld füllt die volle Breite aus
        )
        HorizontalDivider(color = Color.Gray, thickness = 2.dp)
        OutlinedTextField(
            value = text1,
            onValueChange = { newText ->
                text1 = newText // Aktualisiere den Zustand mit dem neuen Text
            },
            label = { Text("E-mail Account Password") },
            modifier = Modifier.fillMaxWidth() // Das Textfeld füllt die volle Breite aus
        )
        HorizontalDivider(color = Color.Gray, thickness = 2.dp)
        Row {
            Button(onClick = onNextButtonClick) {
                Text("Vor")
            }
            VerticalDivider(color = Color.Gray, thickness = 2.dp)
            Button(onClick = onButtonUp) {
                Text("Zurück")
            }
        }
    }
}




/**
 * Customizable button composable that displays the [labelResourceId]
 * and triggers [onClick] lambda when this composable is clicked
 */
@Composable
fun SelectQuantityButton(
    @StringRes labelResourceId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.widthIn(min = 250.dp)
    ) {
        Text(stringResource(labelResourceId))
    }
}

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
               Mail2App()
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
            Spacer(modifier = Modifier.height(2.dp))
            Row (modifier = Modifier
                .fillMaxWidth()  // Row füllt die gesamte Bildschirmbreite
                .padding(16.dp),  // Padding um die Row herum
                horizontalArrangement = Arrangement.SpaceEvenly){
                Text(text = "Hier werden alle Buttons angezeigt, die ein Bild machen " +
                        "und anschließend eine Mail an mich schicken, " +
                        "die den Buttoninhalt als Betreff hat und das Bild " +
                        "als Anhang.", fontSize = 21.sp)
            }
                Row (modifier = Modifier
                    .fillMaxWidth()  // Row füllt die gesamte Bildschirmbreite
                    .padding(16.dp),  // Padding um die Row herum
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { onClickBtn1(view) }) {
                        Text(text = "Stuff", fontSize = 21.sp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(onClick = { onClickBtn2(view) }) {
                        Text(text = "Strom", fontSize = 21.sp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(onClick = { onClickBtn3(view) }) {
                        Text(text = "Wasser", fontSize = 21.sp)
                    }
                }
                Row (modifier = Modifier
                    .fillMaxWidth()  // Row füllt die gesamte Bildschirmbreite
                    .padding(16.dp),  // Padding um die Row herum
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    Button(onClick = { onClickBtn4(view) }) {
                        Text(text = "Heizung", fontSize = 21.sp)
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

