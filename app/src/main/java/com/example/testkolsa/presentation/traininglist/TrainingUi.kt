package com.example.testkolsa.presentation.traininglist

import com.example.testkolsa.presentation.common.BaseItem

data class TrainingUi(
    val id: Long,
    val title: String,
    val description: String?,
    val type: Long,
    val duration: String,
    override val itemId: String,
) : BaseItem
