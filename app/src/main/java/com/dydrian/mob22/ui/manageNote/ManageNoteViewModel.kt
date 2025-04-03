package com.dydrian.mob22.ui.manageNote

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class ManageNoteViewModel : ViewModel() {
    protected val _finish = MutableSharedFlow<Unit>()
    val finish = _finish.asSharedFlow()

    abstract fun submitTask(title: String, desc: String)
}