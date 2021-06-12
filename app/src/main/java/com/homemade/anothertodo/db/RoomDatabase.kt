package com.homemade.anothertodo.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homemade.anothertodo.db.dao.delRegularTaskDao
import com.homemade.anothertodo.db.dao.SettingsDao
import com.homemade.anothertodo.db.dao.TaskDao
import com.homemade.anothertodo.db.entity.delRegularTask
import com.homemade.anothertodo.db.entity.Settings
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TypeTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Settings::class, Task::class],
    version = 1,
    exportSchema = false,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun SettingsDao(): SettingsDao
    abstract fun TaskDao(): TaskDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.TaskDao())
                    populateDatabase(database.SettingsDao())
                }
            }
        }

        suspend fun populateDatabase(dao: SettingsDao) {
            dao.deleteAll()
            dao.insert(Settings())
        }

        suspend fun populateDatabase(dao: TaskDao) {
            dao.deleteAll()

            /** Regular Tasks */

            dao.insert(Task(name = "Игра на гитаре", type = TypeTask.REGULAR_TASK))

            /** Single Tasks */

            val routine = dao.insert(Task(name = "Быт", group = true))
            dao.insert(Task(name = "Убрать на столе", parent = routine))
            dao.insert(Task(name = "Убраться в отделении на столе", parent = routine))
            dao.insert(Task(name = "Убраться в верхнем ящике стола", parent = routine))
            dao.insert(Task(name = "Убраться в среднем ящике стола", parent = routine))
            dao.insert(Task(name = "Убраться в нижнем ящике стола", parent = routine))
            dao.insert(Task(name = "Разобрать пакет под стулом", parent = routine))
            dao.insert(Task(name = "Разметить турник", parent = routine))
            dao.insert(Task(name = "Заказ Aliexpress (тестер, ключ и пр.)", parent = routine))
            dao.insert(Task(name = "Заказ/Выбор IHerb", parent = routine))
            dao.insert(Task(name = "Компьютер в зале", parent = routine))
            dao.insert(Task(name = "Сиденье унитаза", parent = routine))
            dao.insert(Task(name = "Почистить кофемашину", parent = routine))
            dao.insert(Task(name = "Сдать анализы", deadline = 168, parent = routine))

            val pc = dao.insert(Task(name = "Компьютер, телефон и пр.", group = true))
            dao.insert(Task(name = "Придумать систему бэкапов", parent = pc))
            dao.insert(Task(name = "Вкладки Chrome (ноут)", parent = pc))
            dao.insert(Task(name = "Вкладки Chrome (комп)", parent = pc))
            dao.insert(Task(name = "Рабочий стол (ноут)", parent = pc))
            dao.insert(Task(name = "Рабочий стол (комп)", parent = pc))
            dao.insert(Task(name = "Разобраться с телефоном, бэкап и пр.", parent = pc))
            dao.insert(Task(name = "Купить что-нибудь в форе", deadline = 72, parent = pc))

            val poker = dao.insert(Task(name = "Покер", group = true))
            dao.insert(Task(name = "Сыграть в покер", parent = poker))
            dao.insert(Task(name = "Кэшаут Старзы", parent = poker))
            dao.insert(Task(name = "Кэшаут Покерок", parent = poker))

            val music = dao.insert(Task(name = "Музыка", group = true))

            val mOthers = dao.insert(Task(name = "Прочее", parent = music, group = true))
            dao.insert(Task(name = "Подключить синтезатор", parent = mOthers))
            dao.insert(Task(name = "Найти/заказать дисковод/дискеты", parent = mOthers))
            dao.insert(Task(name = "Выбрать 'песню' для аранжировки", parent = mOthers))

            val mPractice = dao.insert(Task(name = "Практика", parent = music, group = true))
            dao.insert(Task(name = "Сольфеджио", parent = mPractice))
            dao.insert(Task(name = "Электрогитара", parent = mPractice))
            dao.insert(Task(name = "Тренажер слуха", parent = mPractice))

            val mTheory = dao.insert(Task(name = "Теория", parent = music, group = true))
            dao.insert(Task(name = "Дослушать Баха", parent = mTheory))
            dao.insert(Task(name = "Музыкофилия 30 мин.", parent = mTheory))


            val english = dao.insert(Task(name = "Английский", group = true))
            dao.insert(Task(name = "Дочитать главу HPMOR", parent = english))
            dao.insert(Task(name = "Досмотреть форд против феррари", parent = english))
            dao.insert(Task(name = "Серия How I Met Your Mother", parent = english))
            dao.insert(Task(name = "Bill Perkins 1 chapter or 30 min", parent = english))

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