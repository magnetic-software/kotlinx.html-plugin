package com.data2viz.kotlinx.htmlplugin

import com.data2viz.kotlinx.htmlplugin.conversion.data.HtmlAttribute
import com.data2viz.kotlinx.htmlplugin.conversion.data.HtmlTag
import com.data2viz.kotlinx.htmlplugin.conversion.data.HtmlText
import com.data2viz.kotlinx.htmlplugin.conversion.model.INDENT
import com.data2viz.kotlinx.htmlplugin.conversion.model.isInline
import com.data2viz.kotlinx.htmlplugin.conversion.model.toKotlinX
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class HtmlDataToHtmlKotlinXTest {

    @Test
    fun HtmlTagtoKotlinXBase() {
        Assert.assertEquals("div {\n}", HtmlTag("div").toKotlinX())
    }

    @Test
    fun HtmlTagIsInline() {
        var htmlTag = HtmlTag("div")
        Assert.assertEquals(false, htmlTag.isInline())
        htmlTag.children.add(HtmlText("text"))

        Assert.assertEquals(true, htmlTag.isInline())
        htmlTag.children.add(HtmlText("text"))
        Assert.assertEquals(false, htmlTag.isInline())

        htmlTag = HtmlTag("div")
        htmlTag.children.add(HtmlTag("div"))
        Assert.assertEquals(false, htmlTag.isInline())
        htmlTag.children.add(HtmlTag("div"))
        Assert.assertEquals(false, htmlTag.isInline())

    }

    @Test
    fun HtmlTagtoKotlinXInline() {
        val htmlTag = HtmlTag("div")
        htmlTag.children.add(HtmlText("text"))
        Assert.assertEquals("div { + \"text\"}", htmlTag.toKotlinX())
    }


    @Test
    fun HtmlTagtoKotlinXNested() {
        val htmlTag = HtmlTag("div")
        htmlTag.children.add(HtmlTag("p"))
        htmlTag.children.add(HtmlTag("span"))
        Assert.assertEquals("div {\n${INDENT}p {\n${INDENT}}\n${INDENT}span {\n${INDENT}}\n}", htmlTag.toKotlinX())
    }


    @Test
    fun HtmlTagtoKotlinXNestedTwice() {
        val htmlTag = HtmlTag("div")
        val inner = HtmlTag("p")
        htmlTag.children.add(inner)
        inner.children.add(HtmlTag("span"))
        Assert.assertEquals("div {\n${INDENT}p {\n${INDENT}${INDENT}span {\n${INDENT}${INDENT}}\n${INDENT}}\n}", htmlTag.toKotlinX())
    }


    @Test
    fun HtmlTagtoKotlinXBaseAttributes() {
        val htmlTag = HtmlTag("div")
        htmlTag.attributes.add(HtmlAttribute("attr1"))
        htmlTag.attributes.add(HtmlAttribute("attr2", "value2"))

        Assert.assertEquals("div(attr1 = true, attr2 = \"value2\") {\n}", htmlTag.toKotlinX())
    }

    @Test
    fun HtmlAttributetoKotlinXWithoutValue() {
        Assert.assertEquals("attr_name = true", HtmlAttribute("attr_name").toKotlinX())
    }

    @Test
    fun HtmlAttributetoKotlinXWithValue() {
        Assert.assertEquals("attr_name = \"attr_value\"", HtmlAttribute("attr_name", "attr_value").toKotlinX())
    }
}