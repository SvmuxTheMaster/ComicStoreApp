package com.example.comicstoreapp.data.local.database

import android.content.Context
import androidx.room.*
import com.example.comicstoreapp.data.local.detalle.DetalleDao
import com.example.comicstoreapp.data.local.detalle.DetalleEntity
import com.example.comicstoreapp.data.local.inventario.InventarioDao
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.data.local.pedido.PedidoDao
import com.example.comicstoreapp.data.local.pedido.PedidoEntity
import com.example.comicstoreapp.data.local.user.UserDao
import com.example.comicstoreapp.data.local.user.UserEntity

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

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "comicstore_db"
                )
                    .fallbackToDestructiveMigration() // Si cambias versión, destruye y recrea
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
