package com.folioreader.byobook

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

class ParseAndSuggest(url: URL) {
    var title = ""
    var author = ""
    var bodyStart = ""
    var bodyEnd = ""
    var doc: Document? = Jsoup.connect(url.toString()).get()

    fun populate () {
    }

    fun moveViewPortDown(sections: BookSections) {

    }

    fun moveViewPortUp(sections: BookSections) {

    }

    fun getFinal() {

    }
}