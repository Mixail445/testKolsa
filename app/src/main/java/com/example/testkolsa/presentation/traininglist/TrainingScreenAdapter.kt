package com.example.testkolsa.presentation.traininglist

import com.example.testkolsa.presentation.common.BaseItem
import com.example.testkolsa.utils.itemCallback
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class TrainingScreenAdapter(val onItemClicked: (Long) -> Unit) :
    AsyncListDifferDelegationAdapter<BaseItem>(diffUtils()) {
    init {
        delegatesManager.addDelegate(
                TRAINING_ITEM_VIEW_TYPE,
                reviewItemAdapterDelegate(onItemClicked),
            )
    }

    companion object {
        const val TRAINING_ITEM_VIEW_TYPE = -1001
    }
}

private fun diffUtils() = itemCallback<BaseItem>(
    areItemsTheSame = { oldItem, newItem -> oldItem.itemId == newItem.itemId },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    getChangePayload = { _, _ ->
        Any()
    },
)