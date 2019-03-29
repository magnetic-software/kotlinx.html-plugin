package com.data2viz.kotlinx.htmlplugin

import java.lang.StringBuilder


fun HtmlTag.toKotlinX(): String {
    val sb = StringBuilder();

    sb.append("$name {\n")


    for (attribute in attributes) {
        sb.append(attribute.toKotlinX())
        sb.append("\n")
    }

    body?.let {
        sb.append("+ \"${convertBody(it)}\"")
        sb.append("\n")
    }

    for (child in children) {
        sb.append(child)
        sb.append("\n")
    }
    sb.append("}\n")

    return sb.toString();
}


fun HtmlAttribute.toKotlinX(): String {

    val result: String
    if (value != null) {
        result = "$name = \"$value\""
    } else {
        result = name

    }

    return result
}

private fun convertBody(text: String): String {
    if (text.contains("\n") || text.contains("\"")) {
        return "\"" + "\"" + "\"" + text + "\"" + "\"" + "\""
    } else {
        return "\"" + text + "\""
    }
}