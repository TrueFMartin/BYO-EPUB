package com.folioreader.byobook

import org.jsoup.nodes.Element

data class ParsedBook(val title: String, val author: String,
                      val body: Element, val others: Map<String,Element>)
