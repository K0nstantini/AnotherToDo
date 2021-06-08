package com.homemade.anothertodo.utils

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import com.homemade.anothertodo.R


class PrimaryActionModeCallback : ActionMode.Callback {

    var onActionItemClickListener: OnActionItemClickListener? = null
    var destroyListener: DestroyListener? = null

    private var mode: ActionMode? = null

    @MenuRes
    private var menuResId: Int = 0
    private var title: String? = null
    private var subtitle: String? = null

    fun interface OnActionItemClickListener {
        fun onActionItemClick(item: MenuItem)
    }

    fun interface DestroyListener {
        fun destroy()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        this.mode = mode
        mode.menuInflater.inflate(menuResId, menu)
        mode.title = title
        mode.subtitle = subtitle
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        destroyListener?.destroy()
        this.mode = null
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        onActionItemClickListener?.onActionItemClick(item)
        if (item.itemId != R.id.menu_delete) {
            mode.finish()
        }
        return true
    }

    fun startActionMode(
        view: View,
        @MenuRes menuResId: Int,
        title: String? = null,
        subtitle: String? = null
    ) {
        this.menuResId = menuResId
        this.title = title
        this.subtitle = subtitle
        view.startActionMode(this)
    }

    fun finishActionMode() {
        mode?.finish()
    }

}