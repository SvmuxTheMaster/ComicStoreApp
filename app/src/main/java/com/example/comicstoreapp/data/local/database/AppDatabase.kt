package com.example.comicstoreapp.data.local.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.comicstoreapp.data.local.detalle.DetalleDao
import com.example.comicstoreapp.data.local.detalle.DetalleEntity
import com.example.comicstoreapp.data.local.inventario.InventarioDao
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.data.local.pedido.PedidoDao
import com.example.comicstoreapp.data.local.pedido.PedidoEntity
import com.example.comicstoreapp.data.local.user.UserDao
import com.example.comicstoreapp.data.local.user.UserEntity
import kotlinx.coroutines.*

@Database(
    entities = [
        UserEntity::class,
        DetalleEntity::class,
        InventarioEntity::class,
        PedidoEntity::class], // Agrega aquí tus otras entidades: InventarioEntity, PedidoEntity, DetalleEntity
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun inventarioDao(): InventarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detalleDao(): DetalleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "comicstore_db"


        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {

                                val userDao = getInstance(context).userDao()
                                userDao.insert(
                                    UserEntity(
                                        nombre = "Administrador",
                                        correo = "admin@admin.com",
                                        rut = "00000000-0",
                                        contrasena = "Admin1234",
                                        rol = "admin"
                                    )
                                )
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
