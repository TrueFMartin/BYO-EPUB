package com.folioreader.byobook

import org.jsoup.nodes.Element

data class ParsedBook(var title: String, var author: String,
                      var body: Element, var others: Map<String,Element>) {
}
