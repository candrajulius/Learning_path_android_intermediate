package com.candra.latihanpagingtigafromnetwork.di

import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem

object DataDummy {

    fun generateDummyQuoteResponse(): List<QuoteResponseItem> {
        val items: MutableList<QuoteResponseItem> = arrayListOf()
        for (i in 0..100) {
            val quote = QuoteResponseItem(
                i.toString(),
                "author + $i",
                "quote $i",
            )
            items.add(quote)
        }
        return items
    }

}