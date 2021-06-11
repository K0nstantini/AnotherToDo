package com.homemade.anothertodo.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homemade.anothertodo.db.dao.RegularTaskDao
import com.homemade.anothertodo.db.dao.SettingsDao
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.RegularTask
import com.homemade.anothertodo.db.entity.Settings
import com.homemade.anothertodo.db.entity.SingleTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Settings::class, SingleTask::class, RegularTask::class],
    version = 1,
    exportSchema = false,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun SettingsDao(): SettingsDao
    abstract fun SingleTaskDao(): SingleTaskDao
    abstract fun RegularTaskDao(): RegularTaskDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.SingleTaskDao())
                    populateDatabase(database.SettingsDao())
                }
            }
        }

        suspend fun populateDatabase(dao: SettingsDao) {
            dao.deleteAll()
            dao.insert(Settings())
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

            val pc = dao.insert(SingleTask(name = "Компьютер, телефон и пр.", group = true))
            dao.insert(SingleTask(name = "Придумать систему бэкапов", parent = pc))
            dao.insert(SingleTask(name = "Вкладки Chrome (ноут)", parent = pc))
            dao.insert(SingleTask(name = "Вкладки Chrome (комп)", parent = pc))
            dao.insert(SingleTask(name = "Рабочий стол (ноут)", parent = pc))
            dao.insert(SingleTask(name = "Рабочий стол (комп)", parent = pc))
            dao.insert(SingleTask(name = "Разобраться с телефоном, бэкап и пр.", parent = pc))
            dao.insert(SingleTask(name = "Купить что-нибудь в форе", deadline = 72, parent = pc))

            val poker = dao.insert(SingleTask(name = "Покер", group = true))
            dao.insert(SingleTask(name = "Сыграть в покер", parent = poker))
            dao.insert(SingleTask(name = "Кэшаут Старзы", parent = poker))
            dao.insert(SingleTask(name = "Кэшаут Покерок", parent = poker))

            val music = dao.insert(SingleTask(name = "Музыка", group = true))

            val mOthers = dao.insert(SingleTask(name = "Прочее", parent = music, group = true))
            dao.insert(SingleTask(name = "Подключить синтезатор", parent = mOthers))
            dao.insert(SingleTask(name = "Найти/заказать дисковод/дискеты", parent = mOthers))
            dao.insert(SingleTask(name = "Выбрать 'песню' для аранжировки", parent = mOthers))

            val mPractice = dao.insert(SingleTask(name = "Практика", parent = music, group = true))
            dao.insert(SingleTask(name = "Сольфеджио", parent = mPractice))
            dao.insert(SingleTask(name = "Электрогитара", parent = mPractice))
            dao.insert(SingleTask(name = "Тренажер слуха", parent = mPractice))

            val mTheory = dao.insert(SingleTask(name = "Теория", parent = music, group = true))
            dao.insert(SingleTask(name = "Дослушать Баха", parent = mTheory))
            dao.insert(SingleTask(name = "Музыкофилия 30 мин.", parent = mTheory))


            val english = dao.insert(SingleTask(name = "Английский", group = true))
            dao.insert(SingleTask(name = "Дочитать главу HPMOR", parent = english))
            dao.insert(SingleTask(name = "Досмотреть форд против феррари", parent = english))
            dao.insert(SingleTask(name = "Серия How I Met Your Mother", parent = english))
            dao.insert(SingleTask(name = "Bill Perkins 1 chapter or 30 min", parent = english))

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
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}