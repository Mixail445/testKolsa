package com.example.testkolsa.presentation.traininglist

import com.example.testkolsa.R
import com.example.testkolsa.databinding.ItemTrainingBinding
import com.example.testkolsa.presentation.common.BaseItem
import com.example.testkolsa.presentation.common.TrainingType
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun reviewItemAdapterDelegate(
    onItemClicked: (Long) -> Unit,
) = adapterDelegateViewBinding<TrainingUi, BaseItem, ItemTrainingBinding>(
    viewBinding = { layoutInflater, parent ->
        ItemTrainingBinding.inflate(layoutInflater, parent, false)
    },
) {
    binding.root.setOnClickListener {
        onItemClicked(item.id)
    }
    bind {
        binding.run {
            with(item) {
//                tvBody.text = context.resources.getQuantityString(R.plurals.minutes, this.duration.toInt(), this.duration)
                tvBody.text = context.getString(R.string.minute, duration)
                tvTitle.text = title
                tvDate.text = TrainingType.fromId(type)?.title
                tvDesc.text = this.description
            }
        }
    }
}