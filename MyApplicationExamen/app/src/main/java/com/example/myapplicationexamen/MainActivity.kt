package com.example.myapplicationexamen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationexamen.data.RegistroMedidor
import com.example.myapplicationexamen.ui.RegistroListViewModel
import com.example.myapplicationexamen.ui.theme.MyApplicationExamenTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationExamenTheme {
                Scaffold { paddingValues ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "medicionesList", //destino inicial
                        modifier = Modifier.padding(paddingValues)
                    ) { //Ruta de navegación y composables asociados
                        composable("medicionesList") { MedicionesListScreen(navController) }
                        composable("addMedicion") { AddMedicionScreen(navController) }
                    }
                }
            }
        }
    }

}


@Composable // Composable que muestra una lista de mediciones.
fun MedicionesListScreen(navController: NavController) {
    val viewModel: RegistroListViewModel = viewModel()
    val mediciones by viewModel.registros.collectAsState()

    Scaffold(
        floatingActionButton = { // FAB que, al presionarlo, navega hacia "addMedicion".
            FloatingActionButton(onClick = { navController.navigate("addMedicion") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Medición")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(mediciones) { medicion ->
                MedicionItem(medicion)
            }
        }
    }
}


@Composable // Composable que muestra la información de una medición individual.
fun MedicionItem(medicion: RegistroMedidor) {
    val context = LocalContext.current

    // Obtenemos el recurso string correspondiente al tipo de medición.
    val tipoStringRes = when (medicion.tipo) {
        context.getString(R.string.water_key) -> R.string.water
        context.getString(R.string.light_key) -> R.string.light
        context.getString(R.string.gas_key) -> R.string.gas
        else -> null // No se define un string por defecto.
    }

    // Accede al string traducido, si es nulo utiliza el tipo de medición directamente.
    val tipoString = tipoStringRes?.let { stringResource(id = it) } ?: medicion.tipo

    val iconMap = mapOf(
        context.getString(R.string.water_key) to ImageVector.vectorResource(R.drawable.gota_agua),
        context.getString(R.string.light_key) to ImageVector.vectorResource(R.drawable.ampolleta),
        context.getString(R.string.gas_key) to ImageVector.vectorResource(R.drawable.gas)
    )

    val icon = iconMap[medicion.tipo]
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(medicion.fechaCreacion)
    val formattedValue = NumberFormat.getNumberInstance().format(medicion.valor)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Image(imageVector = icon, contentDescription = tipoString)
        }
        Spacer(Modifier.width(16.dp))
        Text(text = tipoString, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text(text = formattedValue)
        Spacer(Modifier.width(16.dp))
        Text(text = formattedDate)
    }
    HorizontalDivider()
}

@Composable // Interfaz para agregar nueavs mediciones.
fun AddMedicionScreen(navController: NavController, viewModel: RegistroListViewModel = viewModel()) {
    var valor by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var tipoMedidor by remember { mutableStateOf("agua") }

    val tipoMedidorLabel = when (tipoMedidor) { //Supongo que con esto funcionará por que no podía cambiar directamente en la variable
        "agua" -> stringResource(id = R.string.water)
        "luz" -> stringResource(id = R.string.light)
        "gas" -> stringResource(id = R.string.gas)
        else -> tipoMedidor  // O manejar un caso por defecto
    }

    Column(modifier = Modifier.padding(16.dp)) {
        InputField(stringResource(id = R.string.input_label_medidor), valor) { valor = it.filter { char -> char.isDigit() } }
        InputField(stringResource(id = R.string.input_label_fecha), fecha) { fecha = it.filter { char -> char.isDigit() || char == '-' } }
        TipoMedidorRadioGroup(tipoMedidorLabel) { tipoMedidor = it }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { handleButtonClick(valor, fecha, tipoMedidor, viewModel, navController) }) {
            Text(stringResource(id = R.string.button_registrar_medicion))
        }
    }
}


// Campo de texto con etiqueta.
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

// Grupo de botones de radio para seleccionar el tipo de medidor.
@Composable
fun TipoMedidorRadioGroup(selectedTipo: String, onTipoSelected: (String) -> Unit) {

    val tipos = listOf("agua", "luz", "gas")
    val labels = listOf(
        stringResource(id = R.string.water),
        stringResource(id = R.string.light),
        stringResource(id = R.string.gas)
    )

    Row {
        tipos.zip(labels).forEach { (tipo, label) ->
            RadioButton(
                selected = selectedTipo == tipo,
                onClick = { onTipoSelected(tipo) }
            )
            Text(text = label)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

// Manejo del clic en el botón de registro de medición
private fun handleButtonClick(valor: String, fecha: String, tipoMedidor: String, viewModel: RegistroListViewModel, navController: NavController) {
    if (validateInput(valor, fecha)) {
        val registroMedidor = RegistroMedidor(
            tipo = tipoMedidor.uppercase(Locale.getDefault()),
            valor = valor.toInt(),
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fecha) ?: Date()
        )
        viewModel.agregaRegistro(registroMedidor)
        navController.popBackStack()
    } else { //ver
    }
}


//Para validar los valores de entrada.
private fun validateInput(valor: String, fecha: String): Boolean {
    val isValorValid = valor.isNotEmpty() && valor.toIntOrNull() != null
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        isLenient = false
    }
    var isFechaValid = false
    if (fecha.isNotEmpty()) {
        isFechaValid = try {
            dateFormat.parse(fecha)
            true
        } catch (e: Exception) {
            false
        }
    }
    // Retorna verdadero si ambas validaciones son correctas.
    return isValorValid && isFechaValid
}


@Preview(showBackground = true) // Composable para previsualizar los componibles.
@Composable
fun DefaultPreview() {
    MyApplicationExamenTheme {
        MedicionesListScreen(rememberNavController())
        AddMedicionScreen(rememberNavController())
    }
}