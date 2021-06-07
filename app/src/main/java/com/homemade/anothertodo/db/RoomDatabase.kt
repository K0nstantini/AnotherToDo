package com.homemade.anothertodo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.SingleTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [SingleTask::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun SingleTaskDao(): SingleTaskDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.SingleTaskDao())
                }
            }
        }

        suspend fun populateDatabase(dao: SingleTaskDao) {
            dao.deleteAll()

            val routine = dao.insert(SingleTask(name = "Быт", group = true))
            dao.insert(SingleTask(name = "Убрать на столе", parent = routine))
            dao.insert(SingleTask(name = "Убраться в отделении на столе", parent = routine))
            dao.insert(SingleTask(name = "Убраться в верхнем ящике стола", parent = routine))
            dao.insert(SingleTask(name = "Убраться в среднем ящике стола", parent = routine))
            dao.insert(SingleTask(name = "Убраться в нижнем ящике стола", parent = routine))
            dao.insert(SingleTask(name = "Разобрать пакет под стулом", parent = routine))
            dao.insert(SingleTask(name = "Разметить турник", parent = routine))
            dao.insert(SingleTask(name = "Заказ Aliexpress (тестер, ключ и пр.)", parent = routine))
            dao.insert(SingleTask(name = "Заказ/Выбор IHerb", parent = routine))
            dao.insert(SingleTask(name = "Компьютер в зале", parent = routine))
            dao.insert(SingleTask(name = "Сиденье унитаза", parent = routine))
            dao.insert(SingleTask(name = "Почистить кофемашину", parent = routine))
            dao.insert(SingleTask(name = "Сдать анализы", deadline = 168, parent = routine))

            val pc = dao.insert(SingleTask(name = "Компьютер", group = true))
            dao.insert(SingleTask(name = "Придумать систему бэкапов", parent = pc))
            dao.insert(SingleTask(name = "Вкладки Chrome (ноут)", parent = pc))
            dao.insert(SingleTask(name = "Вкладки Chrome (комп)", parent = pc))
            dao.insert(SingleTask(name = "Рабочий стол (ноут)", parent = pc))
            dao.insert(SingleTask(name = "Рабочий стол (комп)", parent = pc))
            dao.insert(SingleTask(name = "Купить что-нибудь в форе", deadline = 72, parent = pc))

            val poker = dao.insert(SingleTask(name = "Покер", group = true))
            dao.insert(SingleTask(name = "Сыграть в покер", parent = poker))
            dao.insert(SingleTask(name = "Кэшаут Старзы", parent = poker))
            dao.insert(SingleTask(name = "Кэшаут Покерок", parent = poker))

            val music = dao.insert(SingleTask(name = "Музыка", group = true))
            dao.insert(SingleTask(name = "Сольфеджио", parent = music))
            dao.insert(SingleTask(name = "Подключить синтезатор", parent = music))
            dao.insert(SingleTask(name = "Найти/заказать дисковод/дискеты", parent = music))
            dao.insert(SingleTask(name = "Дослушать Баха", parent = music))
            dao.insert(SingleTask(name = "Электрогитара", parent = music))

            val english = dao.insert(SingleTask(name = "Английский", group = true))
            dao.insert(SingleTask(name = "Дочитать главу HPMOR", parent = english))
            dao.insert(SingleTask(name = "Досмотреть форд против феррари", parent = english))
            dao.insert(SingleTask(name = "Серия How I Met Your Mother", parent = english))

        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "another_to_do_database"
                )
//                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}