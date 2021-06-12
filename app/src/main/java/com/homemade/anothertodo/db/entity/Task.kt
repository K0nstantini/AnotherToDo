package com.homemade.anothertodo.db.entity

import android.os.Parcelable
import androidx.lifecycle.LiveData
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

    /** Single task */
    var dateActivation: MyCalendar = MyCalendar(),           // Дата активации задачи
    var dateStart: MyCalendar = MyCalendar().today(),        // Дата, начиная с которой, задача становиться активной
    var dateUntilToDo: MyCalendar = MyCalendar(),            // Задача должна быть сгенерирована до этой даты
    var deadline: Int = DEFAULT_DEADLINE_SINGLE_TASK,
    var toDoAfterTask: Long = 0L,                            // Задача будет сегенрирована только после выполнения другой задачи
    var rolls: Int = 0,                                      // количество замен задачи

) : Parcelable {

    val readyToActivate: Boolean
        get() = !group && dateActivation.isEmpty() && dateStart < MyCalendar().now()

    fun canRoll(settings: Settings) = rolls < settings.singleTask.numberPossibleRolls

    private fun setName(_name: LiveData<String>) = _name.value?.let { name = it }
    private fun setGroup(_group: LiveData<Boolean>) = _group.value?.let { group = it }
    private fun setParent(_parent: LiveData<Long>) = _parent.value?.let { parent = it }
    private fun setDateStart(date: LiveData<MyCalendar>) = date.value?.let { dateStart = it }
    private fun setDeadline(_deadline: LiveData<Int>) = _deadline.value?.let { deadline = it }

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