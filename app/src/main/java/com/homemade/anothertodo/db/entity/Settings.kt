package com.homemade.anothertodo.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homemade.anothertodo.add_classes.MyCalendar

@Entity(tableName = "settings_table")
data class Settings(
    @PrimaryKey val id: Int = 1,

    /** Single tasks */
    @Embedded
    val singleTask: SettingsSingleTask = SettingsSingleTask()


) {

    data class SettingsSingleTask(
        var dateActivation: MyCalendar = MyCalendar(),                                  // время, когда задача запланирована
        var frequency: Int = FREQUENCY_GENERATE_S_TASKS,                                // частота генерации задач в часах (в среднем равная 1/2 от значения)
        var rewards: Boolean = true,                                                    // включение системы наказаний/вознаграждений
        var points: Int = 0,                                                            // баллы завыполнение/невыполнение задач
        var pointsForTask: Int = POINTS_FOR_TASK,                                       // баллы за выполнение запланированной задачи
        var pointsForOutOfOrderTask: Int = POINTS_FOR_OUT_OF_ORDER_TASK,                // баллы за внеочередное выполнение задачи
        var daysToConsiderTaskOld: Int = DAYS_TO_CONSIDER_TASK_OLD,                     // количество дней жизни задачи чтобы считать ее пригодной для внеочередного выполнения (заработать баллы)
        var pointsForRoll: Int = POINTS_FOR_ROLL,                                       // баллы за рандомную замену текущей задачи
        var numberPossibleRolls: Int = NUMBER_POSSIBLE_ROLLS,                           // сколько раз можно заменять одну задачу
        var currentTaskTakePartInRoll: Boolean = true,                                  // текущая задача учавствует при генерации новой задачи (т. е. замена может не сработать)
        var postponeCurrentTaskForOnePoint: Int = POSTPONE_CURRENT_TASK_FOR_ONE_POINT,  // количество часов, на которые можно отложить текущую задачу за 1 балл
        var postponeNextTaskForOnePoint: Int = POSTPONE_NEXT_TASK_FOR_ONE_POINT,        // количество часов, на которые можно отложить следующую задачу за 1 балл
    ) {

        val canRoll: Boolean
            get() = points >= pointsForRoll

        companion object {
            private const val FREQUENCY_GENERATE_S_TASKS = 96
            private const val POINTS_FOR_TASK = 1
            private const val POINTS_FOR_OUT_OF_ORDER_TASK = 3
            private const val DAYS_TO_CONSIDER_TASK_OLD = 7
            private const val POINTS_FOR_ROLL = 3
            private const val NUMBER_POSSIBLE_ROLLS = 1
            private const val POSTPONE_CURRENT_TASK_FOR_ONE_POINT = 2
            private const val POSTPONE_NEXT_TASK_FOR_ONE_POINT = 1
        }

    }


}