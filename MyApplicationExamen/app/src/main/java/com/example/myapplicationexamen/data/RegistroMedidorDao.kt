package com.example.myapplicationexamen.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface RegistroMedidorDao {

    // Retorna todos los registros de la tabla 'registros', ordenados por fecha de creación de forma ascendente.
    @Query("SELECT * FROM registros ORDER BY fechaCreacion ASC")
    fun getAllRegistros(): List<RegistroMedidor>

    // Retorna un registro específico por su ID de la tabla 'registros'.
    @Query("SELECT * FROM registros WHERE id = :id")
    fun getRegistroById(id: Int): RegistroMedidor

    // Inserta un registro en la tabla 'registros'....
    @Insert
    fun insertRegistro(registro: RegistroMedidor)

    // Elimina un registro en la tabla 'registros'....
    @Delete
    fun deleteRegistro(registro: RegistroMedidor)

    // Actualiza un registro específico en la tabla 'registros'
    @Update
    fun updateRegistro(registro: RegistroMedidor)
}