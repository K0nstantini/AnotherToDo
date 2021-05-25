package com.homemade.anothertodo.settingItem

import androidx.annotation.StringRes
import kotlin.reflect.KFunction0

class SettingItem(@StringRes val title: Int) {

    private var _showValue = true
    val showValue: Boolean get() = _showValue

    private var _value = ""
    val value: String get() = _value

    private var _showSwitch = false
    val showSwitch: Boolean get() = _showSwitch

    private var _stateSwitch = false
    val stateSwitch: Boolean get() = _stateSwitch

    private var actionSwitch: KFunction0<Unit>? = null

    private var _action: KFunction0<Unit>? = null
    val action: KFunction0<Unit>? get() = _action

    private var _enabled = true
    val enabled: Boolean get() = _enabled

    private var _showClear = false
    val showClear: Boolean get() = _showClear

    private var _clicked = true
    val clicked: Boolean get() = _clicked

    private var actionClear: KFunction0<Unit>? = null

    fun setValue(str: String): SettingItem {
        _showValue = true
        _value = str
        return this
    }

    fun setSwitch(fun0: KFunction0<Unit>, state: Boolean = false): SettingItem {
        actionSwitch = fun0
        _showSwitch = true
        _stateSwitch = state
        return this
    }

    fun setStateSwitch(state: Boolean): SettingItem {
        _showSwitch = true
        _stateSwitch = state
        return this
    }

    fun setAction(fun0: KFunction0<Unit>) = this.apply { _action = fun0 }

    fun setEnabled(value: Boolean): SettingItem {
        _showClear = false
        _enabled = value
        return this
    }

    fun setClear(fun0: KFunction0<Unit>): SettingItem {
        _showClear = true
        actionClear = fun0
        return this
    }

    fun setShowClear(value: Boolean) = this.apply { _showClear = value }

    fun setClicked(value: Boolean): SettingItem {
        _clicked = value
        _showClear = false
        return this
    }

    fun onClearClicked() = actionClear?.invoke()

    fun onSwitchClicked() = actionSwitch?.invoke()

}