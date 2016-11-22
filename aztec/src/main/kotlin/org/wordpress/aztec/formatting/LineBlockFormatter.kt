package org.wordpress.aztec.formatting

import android.support.v4.content.ContextCompat
import android.text.Spanned
import android.text.TextUtils
import org.wordpress.aztec.AztecText
import org.wordpress.aztec.R
import org.wordpress.aztec.TextFormat
import org.wordpress.aztec.spans.AztecBlockSpan
import org.wordpress.aztec.spans.AztecCommentSpan
import org.wordpress.aztec.spans.AztecHeadingSpan
import java.util.*


class LineBlockFormatter(editor: AztecText) : AztecFormatter(editor) {

    fun applyHeading(textFormat: TextFormat) {
        headingClear()

        if (textFormat != TextFormat.FORMAT_PARAGRAPH) {
            headingFormat(textFormat)
        }
    }

    fun applyMoreComment() {
        applyComment(AztecCommentSpan.Comment.MORE)
    }

    fun applyPageComment() {
        applyComment(AztecCommentSpan.Comment.PAGE)
    }


    fun headingClear() {
        val lines = TextUtils.split(editableText.toString(), "\n")

        for (i in lines.indices) {
            if (!containsHeading(i)) {
                continue
            }

            var lineStart = 0

            for (j in 0..i - 1) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length

            if (lineStart >= lineEnd) {
                continue
            }

            var headingStart = 0
            var headingEnd = 0

            if ((lineStart <= selectionStart && selectionEnd <= lineEnd) ||
                    (lineStart >= selectionStart && selectionEnd >= lineEnd) ||
                    (lineStart <= selectionStart && selectionEnd >= lineEnd && selectionStart <= lineEnd) ||
                    (lineStart >= selectionStart && selectionEnd <= lineEnd && selectionEnd >= lineStart)) {
                headingStart = lineStart
                headingEnd = lineEnd
            }

            if (headingStart < headingEnd) {
                val spans = editableText.getSpans(headingStart, headingEnd, AztecHeadingSpan::class.java)

                for (span in spans) {
                    editableText.removeSpan(span)
                }
            }
        }

        editor.refreshText()
    }

    fun headingFormat(textFormat: TextFormat) {
        val lines = TextUtils.split(editableText.toString(), "\n")

        for (i in lines.indices) {
            var lineStart = 0

            for (j in 0..i - 1) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length

            if (lineStart >= lineEnd) {
                continue
            }

            var headingStart = 0
            var headingEnd = 0

            if ((lineStart <= selectionStart && selectionEnd <= lineEnd) ||
                    (lineStart >= selectionStart && selectionEnd >= lineEnd) ||
                    (lineStart <= selectionStart && selectionEnd >= lineEnd && selectionStart <= lineEnd) ||
                    (lineStart >= selectionStart && selectionEnd <= lineEnd && selectionEnd >= lineStart)) {
                headingStart = lineStart
                headingEnd = lineEnd
            }

            if (headingStart < headingEnd) {
                when (textFormat) {
                    TextFormat.FORMAT_HEADING_1 ->
                        editableText.setSpan(AztecHeadingSpan(AztecHeadingSpan.Heading.H1), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    TextFormat.FORMAT_HEADING_2 ->
                        editableText.setSpan(AztecHeadingSpan(AztecHeadingSpan.Heading.H2), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    TextFormat.FORMAT_HEADING_3 ->
                        editableText.setSpan(AztecHeadingSpan(AztecHeadingSpan.Heading.H3), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    TextFormat.FORMAT_HEADING_4 ->
                        editableText.setSpan(AztecHeadingSpan(AztecHeadingSpan.Heading.H4), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    TextFormat.FORMAT_HEADING_5 ->
                        editableText.setSpan(AztecHeadingSpan(AztecHeadingSpan.Heading.H5), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    TextFormat.FORMAT_HEADING_6 ->
                        editableText.setSpan(AztecHeadingSpan(AztecHeadingSpan.Heading.H6), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else -> {
                    }
                }
            }
        }

        editor.refreshText()
    }

    fun containsHeading(textFormat: TextFormat, selStart: Int, selEnd: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        val list = ArrayList<Int>()

        for (i in lines.indices) {
            var lineStart = 0
            for (j in 0..i - 1) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            if (lineStart <= selStart && selEnd <= lineEnd) {
                list.add(i)
            } else if (selStart <= lineStart && lineEnd <= selEnd) {
                list.add(i)
            }
        }

        if (list.isEmpty()) return false

        for (i in list) {
            if (!containHeadingType(textFormat, i)) {
                return false
            }
        }

        return true
    }

    fun containsHeading(index: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")

        if (index < 0 || index >= lines.size) {
            return false
        }

        var start = 0

        for (i in 0..index - 1) {
            start += lines[i].length + 1
        }

        val end = start + lines[index].length

        if (start >= end) {
            return false
        }

        val spans = editableText.getSpans(start, end, AztecHeadingSpan::class.java)
        return spans.size > 0
    }

    private fun containHeadingType(textFormat: TextFormat, index: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")

        if (index < 0 || index >= lines.size) {
            return false
        }

        var start = 0

        for (i in 0..index - 1) {
            start += lines[i].length + 1
        }

        val end = start + lines[index].length

        if (start >= end) {
            return false
        }

        val spans = editableText.getSpans(start, end, AztecHeadingSpan::class.java)

        for (span in spans) {
            when (textFormat) {
                TextFormat.FORMAT_HEADING_1 ->
                    return span.heading == AztecHeadingSpan.Heading.H1
                TextFormat.FORMAT_HEADING_2 ->
                    return span.heading == AztecHeadingSpan.Heading.H2
                TextFormat.FORMAT_HEADING_3 ->
                    return span.heading == AztecHeadingSpan.Heading.H3
                TextFormat.FORMAT_HEADING_4 ->
                    return span.heading == AztecHeadingSpan.Heading.H4
                TextFormat.FORMAT_HEADING_5 ->
                    return span.heading == AztecHeadingSpan.Heading.H5
                TextFormat.FORMAT_HEADING_6 ->
                    return span.heading == AztecHeadingSpan.Heading.H6
                else -> return false
            }
        }

        return false
    }

    private fun applyComment(comment: AztecCommentSpan.Comment) {
        //check if we add a comment into a block element, at the end of the line, but not at the end of last line
        var applyingOnTheEndOfBlockLine = false
        editableText.getSpans(selectionStart, selectionEnd, AztecBlockSpan::class.java).forEach {
            if (editableText.getSpanEnd(it) > selectionEnd && editableText[selectionEnd] == '\n') {
                applyingOnTheEndOfBlockLine = true
                return@forEach
            }
        }

        val commentStartIndex = selectionStart + 1
        val commentEndIndex = selectionStart + comment.html.length + 1

        editor.disableTextChangedListener()
        editableText.replace(selectionStart, selectionEnd, "\n" + comment.html + if (applyingOnTheEndOfBlockLine) "" else "\n")

        editor.removeBlockStylesFromRange(commentStartIndex, commentEndIndex + 1, true)
        editor.removeHeadingStylesFromRange(commentStartIndex, commentEndIndex + 1)
        editor.removeInlineStylesFromRange(commentStartIndex, commentEndIndex + 1)

        val span = AztecCommentSpan(
                editor.context,
                when (comment) {
                    AztecCommentSpan.Comment.MORE -> ContextCompat.getDrawable(editor.context, R.drawable.img_more)
                    AztecCommentSpan.Comment.PAGE -> ContextCompat.getDrawable(editor.context, R.drawable.img_page)
                }
        )

        editableText.setSpan(
                span,
                commentStartIndex,
                commentEndIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        editor.setSelection(commentEndIndex + 1)
    }


}