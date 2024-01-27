package com.example.myapplicationexamen.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "registros")
data class RegistroMedidor(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var tipo: String,
    var valor: Int,
    var fechaCreacion: Date = Date()
)