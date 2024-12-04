package com.yosua.authentication

import com.yosua.authentication.model.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse() : List<ListStoryItem> {
        val items : MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl + $i",
                "cratedAt + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                i.toString(),
                i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}