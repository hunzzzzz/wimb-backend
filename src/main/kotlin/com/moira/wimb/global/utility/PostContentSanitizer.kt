package com.moira.wimb.global.utility

import org.owasp.html.HtmlPolicyBuilder
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class PostContentSanitizer {
    private val policy = HtmlPolicyBuilder()
        .allowElements(
            "p", "h1", "h2", "h3",
            "ul", "ol", "li",
            "blockquote", "pre", "code",
            "hr", "strong", "em", "s", "br", "a", "img", "input"
        )
        .allowAttributes("href").onElements("a")
        .allowUrlProtocols("http", "https")  // mailto 등 제외, http/https만
        .allowAttributes("src", "alt").onElements("img")
        .allowAttributes("data-type").onElements("ul")
        .allowAttributes("data-checked").onElements("li")
        .allowAttributes("type", "checked", "disabled").onElements("input")
        .allowAttributes("class")
        .matching(Pattern.compile("^language-[a-z0-9]+$"))
        .onElements("code")
        .toFactory()

    fun sanitize(html: String): String = policy.sanitize(html)
}