package com.folioreader.builder

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.select.Elements

class WebParser(val url: String) {
    fun getContent(): Document {
        return Jsoup.connect(url).get()
    }
    fun parser(document: Document): Elements {
        var doc = getContent()
        var l : Node
        doc.removeAttr("someattribute")
        return doc.select("#dsc ul.listchap")
    }
    private fun toXHTML(html: String): String? {
        val document = Jsoup.parse(html)
        document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return document.html()
    }
    // ParserConfig is used to pass in specialized html tag attributes to search for
    inner class ParserConfig{
        var titleKey: String = ""
        var nextChapter: String = ""
        var bodyStart: String = ""
        var bodyEnd: String = ""
    }

    enum class AttributeType {
        CLASS, ID,
    }
}