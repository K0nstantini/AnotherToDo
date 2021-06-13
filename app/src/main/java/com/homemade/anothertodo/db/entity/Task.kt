package com.homemade.anothertodo.db.entity

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.enums.TypeTask
import kotlinx.parcelize.Parcelize

const val DEFAULT_DEADLINE_SINGLE_TASK = 24

@Parcelize
@Entity(tableName = "task_table")
data class Task(

    /** General */
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var name: String = "",
    var group: Boolean = false,
    var groupOpen: Boolean = false,
    var parent: Long = 0L,
    var type: TypeTask = TypeTask.SINGLE_TASK,

    @Embedded
    val regular: RegularTask = RegularTask(),
    @Embedded
    val single: SingleTask = SingleTask(),

    ) : Parcelable {

    /** Regular task */
    @Parcelize
    data class RegularTask(
        var frequencyFrom: Int = 0,
        var frequencyTo: Int = 0,
        var timeFrom: Int = 0,                                   // Время, когда задача может стартовать
        var timeTo: Int = 0,
        var periodGeneration: Int = 0,                           // Период генерации задач в днях, например 0-3 задач за 2 дня
        var workingTime: String = "",                            // Дни, когда будет активироваться задача (например только в будни или 2 через 2 дня)
        var chooseFromGroup: Boolean = false,                      // Задачи будут рандомно выбираться из всей группы
        var dateActivated: Long = 0L,                            // Дата активации задачи
        var finishDate: Long = 0L,                               // Задача работает до этой даты
    ) : Parcelable

    /** Single task */
    @Parcelize
    data class SingleTask(
        var dateActivation: MyCalendar = MyCalendar(),           // Дата активации задачи
        var dateStart: MyCalendar = MyCalendar().today(),        // Дата, начиная с которой, задача становиться активной
        var dateUntilToDo: MyCalendar = MyCalendar(),            // Задача должна быть сгенерирована до этой даты
        var deadline: Int = DEFAULT_DEADLINE_SINGLE_TASK,
        var toDoAfterTask: Long = 0L,                            // Задача будет сегенрирована только после выполнения другой задачи
        var rolls: Int = 0,                                      // количество замен задачи
    ) : Parcelable

    val readyToActivate: Boolean
        get() = !group && single.dateActivation.isEmpty() && single.dateStart < MyCalendar().now()

    fun canRoll(settings: Settings) = single.rolls < settings.singleTask.numberPossibleRolls

    private fun setName(_name: LiveData<String>) = _name.value?.let { name = it }
    private fun setGroup(_group: LiveData<Boolean>) = _group.value?.let { group = it }
    private fun setParent(_parent: LiveData<Long>) = _parent.value?.let { parent = it }
    private fun setDateStart(date: LiveData<MyCalendar>) = date.value?.let { single.dateStart = it }
    private fun setDeadline(_deadline: LiveData<Int>) = _deadline.value?.let { single.deadline = it }

    fun setData(
        _name: LiveData<String>,
        _group: LiveData<Boolean>,
        _parent: LiveData<Long>,
        _dateStart: LiveData<MyCalendar>,
        _deadline: LiveData<Int>
    ) {
        setName(_name)
        setGroup(_group)
        setParent(_parent)

        if (!group) {
            setDateStart(_dateStart)
            setDeadline(_deadline)
        }
    }

}