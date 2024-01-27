package com.example.myapplicationexamen.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplicationexamen.data.RegistroMedidor
import com.example.myapplicationexamen.data.RegistroMedidorDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// Clase pa manejar la lógica negocio y estado de la UI con registros de medidor.
class RegistroListViewModel(
    private val registroMedidorDao: RegistroMedidorDao,
    private val context: Context?
) : ViewModel() {

    // Flujo mutable para manejar la lista de registros. Privado para evitar modificaciones externas.
    private val _registros = MutableStateFlow<List<RegistroMedidor>>(emptyList())
    // Flujo público para observar los cambios en la lista de registros.
    val registros: StateFlow<List<RegistroMedidor>> = _registros

    // Función pa agregar un registro a la base de datos, lanza coroutine y actualiza lista de registros.
    fun agregaRegistro(registroMedidor: RegistroMedidor) {
        viewModelScope.launch(Dispatchers.IO) {
            registroMedidorDao.insertRegistro(registroMedidor)
            obtieneRegistros()
        }
    }
    // Obtiene todos los registros de la base de datos, lanza coroutine  y actualizar flujo de registros.
    fun obtieneRegistros() {
        viewModelScope.launch(Dispatchers.IO) {
            val nuevosRegistros = registroMedidorDao.getAllRegistros()
            withContext(Dispatchers.Main) {
                _registros.value = nuevosRegistros
            }
        }
    }
}