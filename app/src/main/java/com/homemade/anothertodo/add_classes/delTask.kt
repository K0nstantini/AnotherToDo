package com.homemade.anothertodo.add_classes

sealed class delTask {
    abstract val id: Long
    abstract var name: String
    abstract var group: Boolean
    abstract var groupOpen: Boolean
    abstract var parent: Long
}