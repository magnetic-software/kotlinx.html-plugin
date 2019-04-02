package io.data2viz.kotlinx.htmlplugin.conversion.model

import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.source.html.HtmlDocumentImpl
import com.intellij.psi.impl.source.html.HtmlFileImpl
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlDoctype
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import io.data2viz.kotlinx.htmlplugin.conversion.data.HtmlAttribute
import io.data2viz.kotlinx.htmlplugin.conversion.data.HtmlElement
import io.data2viz.kotlinx.htmlplugin.conversion.data.HtmlTag
import io.data2viz.kotlinx.htmlplugin.conversion.data.HtmlText


object HtmlPsiToHtmlDataConverter {

    private val LOGGER = Logger.getInstance(HtmlPsiToHtmlDataConverter::class.java)


    fun convertAttribute(source: XmlAttribute): HtmlAttribute {
        return HtmlAttribute(source.name, source.value)
    }

    fun convertPsiFileToHtmlTag(psiFile: PsiFile): List<HtmlElement> {
        val result = mutableListOf<HtmlElement>()
        for (child in psiFile.children) {
            when (child) {
                is HtmlDocumentImpl -> {
                    for (docChild in child.children) {
                        val htmlTag = convertPsiElementToHtmlElement(docChild)
                        if (htmlTag != null) {
                            result.add(htmlTag)
                        }
                    }
                }
                else -> {
                    val htmlTag = convertPsiElementToHtmlElement(child)
                    if (htmlTag != null) {
                        result.add(htmlTag)
                    }
                }
            }

        }

        return result
    }

    fun convertPsiElementToHtmlElement(psiElement: PsiElement, parentHtmlTag: HtmlTag? = null): HtmlElement? {

        var htmlElement: HtmlElement? = null

        when (psiElement) {
            is XmlTag -> {

                htmlElement = HtmlTag(psiElement.name)

                for (childPsi in psiElement.children) {
                    val childHtmlElement = convertPsiElementToHtmlElement(childPsi, htmlElement)
                    if (childHtmlElement != null) {
                        htmlElement.children.add(childHtmlElement)
                    }
                }

            }

            is XmlAttribute -> {

                parentHtmlTag?.attributes?.add(convertAttribute(psiElement))
            }

            is XmlText -> {
                val text = psiElement.text.trim()
                if (text.isNotEmpty()) {
                    htmlElement = HtmlText(text)
                }
            }

        }

        return htmlElement

    }


    fun isStartsWithXmlElement(psiElement: PsiElement): Boolean {

        LOGGER.debug("isStartsWithXmlElement type $psiElement")

        var isStartsWithXmlElement: Boolean
        when (psiElement) {

            is HtmlDocumentImpl, is HtmlFileImpl -> {
                val children = psiElement.children
                if (children.isNotEmpty()) {

                    isStartsWithXmlElement = false
                    for (child in children) {
                        isStartsWithXmlElement = isStartsWithXmlElement(child)
                        if (isStartsWithXmlElement) {
                            break
                        }
                    }


                } else {
                    isStartsWithXmlElement = false
                }
            }

            is XmlTag, is XmlDoctype -> isStartsWithXmlElement = true

            else -> isStartsWithXmlElement = false
        }

        LOGGER.debug("isStartsWithXmlElement result=$isStartsWithXmlElement  class ${psiElement.javaClass.name} \n ${psiElement.text}")

        return isStartsWithXmlElement
    }

    fun isLooksLikeHtml(psiFile: PsiFile): Boolean {

        return isStartsWithXmlElement(psiFile)
    }

    fun createHtmlFileFromText(project: Project, fileName: String, text: String): PsiFile {

        val psiFileFactory = PsiFileFactory.getInstance(project)
        return psiFileFactory.createFileFromText(fileName, HTMLLanguage.INSTANCE, text)
    }

    fun createHtmlFileFromText(project: Project, text: String): PsiFile {

        val psiFileFactory = PsiFileFactory.getInstance(project)
        return psiFileFactory.createFileFromText(HTMLLanguage.INSTANCE, text)
    }
}


fun HtmlFileImpl.converToHtmlElements(): List<HtmlElement> =
        HtmlPsiToHtmlDataConverter.convertPsiFileToHtmlTag(this)
