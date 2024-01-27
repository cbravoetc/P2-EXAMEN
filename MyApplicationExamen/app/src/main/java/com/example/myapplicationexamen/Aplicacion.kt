package com.example.myapplicationexamen

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplicationexamen.data.AppDatabase
import java.util.concurrent.Executors

// Clase Aplicacion que extiende de Application para realizar configuraciones globales.
class Aplicacion : Application(){

    // Instancia lazy de la base de datos. Se inicializa cuando se necesita por primera vez.
    val db by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "registros.db")
            .addCallback(roomCallback) // Agregas callback que se llama cuando la base de datos se crea.
            .build()
    }
    // Instancia lazy del DAO. Se inicializa cuando se necesita por primera vez.
    val registroMedidorDao by lazy { db.registroMedidorDao() }

    companion object {
        // Callback para la base de datos Room. Pregrabamos la base de datos.
        val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Inserto datos pregrabados
                val prepopulateData = Runnable {
                    db.execSQL("INSERT INTO registros (tipo, valor, fechaCreacion) VALUES ('AGUA', 1000, '2024-01-10')")
                    db.execSQL("INSERT INTO registros (tipo, valor, fechaCreacion) VALUES ('LUZ', 15000, '2024-01-10')")
                    db.execSQL("INSERT INTO registros (tipo, valor, fechaCreacion) VALUES ('LUZ', 15500, '2024-01-11')")
                    db.execSQL("INSERT INTO registros (tipo, valor, fechaCreacion) VALUES ('GAS', 2000, '2024-01-10')")
                    db.execSQL("INSERT INTO registros (tipo, valor, fechaCreacion) VALUES ('AGUA', 1500, '2024-01-11')")

                }
                Executors.newSingleThreadExecutor().execute(prepopulateData) // Hilo separado de ejecuciòn para prepoblar.
            }
        }
    }
}
