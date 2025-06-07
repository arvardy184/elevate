package com.application.elevate.service

import android.content.Context
import android.content.ContentValues
import android.graphics.Color // Added for color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.application.elevate.model.CVReviewData
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.font.PDFont
// Import PDType0Font for potentially better font support if needed, though sticking to Type1 for simplicity now
// import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PDFGenerator @Inject constructor() {

  // --- Constants for Styling ---
  private val MARGIN = 50f
  private val PAGE_TOP_Y = PDRectangle.A4.height - MARGIN
  private val PAGE_BOTTOM_Y_THRESHOLD = MARGIN + 50f // Threshold to trigger a new page
  private val LINE_HEIGHT_SMALL = 14f
  private val LINE_HEIGHT_MEDIUM = 18f
  private val LINE_HEIGHT_LARGE = 22f

  private val FONT_SIZE_TITLE = 22f
  private val FONT_SIZE_SUBTITLE = 12f
  private val FONT_SIZE_HEADER_MAIN = 18f
  private val FONT_SIZE_HEADER_SECTION = 15f
  private val FONT_SIZE_BODY = 11f
  private val FONT_SIZE_LIST_ITEM = 11f

  private val SPACE_AFTER_TITLE = 30f
  private val SPACE_AFTER_SUBTITLE_BLOCK = 25f
  private val SPACE_BEFORE_SECTION = 25f
  private val SPACE_AFTER_SECTION_HEADER = 15f
  private val SPACE_BETWEEN_LIST_ITEMS = 5f
  private val SPACE_AFTER_LIST_BLOCK = 20f
  private val INDENT_LIST_ITEM = 20f

  private lateinit var titleFont: PDFont
  private lateinit var headerFont: PDFont
  private lateinit var bodyFont: PDFont
  private lateinit var bodyBoldFont: PDFont // Added for emphasis

  // Helper class to manage current Y position and page creation
  private class PageManager(
    val document: PDDocument,
    var currentPage: PDPage,
    var contentStream: PDPageContentStream,
    var currentY: Float,
    val pageHeight: Float,
    val topMargin: Float,
    val bottomMarginThreshold: Float
  ) {
    fun ensureSpace(requiredSpace: Float): Boolean {
      if (currentY - requiredSpace < bottomMarginThreshold) {
        contentStream.close()
        currentPage = PDPage(PDRectangle.A4)
        document.addPage(currentPage)
        contentStream = PDPageContentStream(document, currentPage)
        currentY = pageHeight - topMargin
        return true // New page created
      }
      return false // No new page needed
    }

    fun moveY(amount: Float) {
      currentY -= amount
    }

    fun setY(newY: Float) {
      currentY = newY
    }
  }


  private fun initializeFonts() {
    titleFont = try { PDType1Font.HELVETICA_BOLD } catch (e: Exception) { PDType1Font.HELVETICA }
    headerFont = try { PDType1Font.HELVETICA_BOLD } catch (e: Exception) { PDType1Font.HELVETICA }
    bodyFont = PDType1Font.HELVETICA
    bodyBoldFont = try { PDType1Font.HELVETICA_BOLD } catch (e: Exception) { PDType1Font.HELVETICA }
  }

  suspend fun generateCVReviewPDF(
    context: Context,
    cvData: CVReviewData,
    baseFileName: String = "cv_review_report"
  ): Result<File> = withContext(Dispatchers.IO) {
    try {
      Log.d("PDFGenerator", "Starting PDF generation for CV: ${cvData.fileName}")
      PDFBoxResourceLoader.init(context.applicationContext) // Use application context
      initializeFonts()

      val document = PDDocument()
      var page = PDPage(PDRectangle.A4)
      document.addPage(page)

      val pageManager = PageManager(
        document = document,
        currentPage = page,
        contentStream = PDPageContentStream(document, page),
        currentY = PAGE_TOP_Y,
        pageHeight = PDRectangle.A4.height,
        topMargin = MARGIN,
        bottomMarginThreshold = PAGE_BOTTOM_Y_THRESHOLD
      )

      val pageWidth = PDRectangle.A4.width - 2 * MARGIN

      // --- PDF Content Generation ---

      // Title
      addText(pageManager, "CV Review Report", titleFont, FONT_SIZE_TITLE, MARGIN, pageManager.currentY, Color.BLACK)
      pageManager.moveY(FONT_SIZE_TITLE + SPACE_AFTER_TITLE)

      // Horizontal Line after Title
      addHorizontalLine(pageManager, MARGIN, pageWidth)
      pageManager.moveY(10f) // Space after line

      // Basic Info
      val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
      val generatedDate = dateFormat.format(Date())
      addText(pageManager, "CV File: ${cvData.fileName}", bodyFont, FONT_SIZE_SUBTITLE, MARGIN, pageManager.currentY, Color.DKGRAY)
      pageManager.moveY(LINE_HEIGHT_MEDIUM)
      addText(pageManager, "Career Field: ${cvData.careerField}", bodyFont, FONT_SIZE_SUBTITLE, MARGIN, pageManager.currentY, Color.DKGRAY)
      pageManager.moveY(LINE_HEIGHT_MEDIUM)
      addText(pageManager, "Report Generated: $generatedDate", bodyFont, FONT_SIZE_SUBTITLE, MARGIN, pageManager.currentY, Color.DKGRAY)
      pageManager.moveY(SPACE_AFTER_SUBTITLE_BLOCK)

      // Overall Score Section
      pageManager.ensureSpace(FONT_SIZE_HEADER_MAIN + SPACE_AFTER_SECTION_HEADER + 10f) // Estimate space
      addSectionHeader(pageManager, "Overall Score: ${cvData.scores.overallScore}%", headerFont, FONT_SIZE_HEADER_MAIN, MARGIN, Color.BLACK)
      pageManager.moveY(SPACE_AFTER_SECTION_HEADER + 10f) // Extra space for emphasis

      // Horizontal Line
      addHorizontalLine(pageManager, MARGIN, pageWidth)
      pageManager.moveY(SPACE_BEFORE_SECTION)


      // Detailed Scores
      pageManager.ensureSpace(FONT_SIZE_HEADER_SECTION + SPACE_AFTER_SECTION_HEADER + (cvData.scores.javaClass.declaredFields.size * LINE_HEIGHT_MEDIUM))
      addSectionHeader(pageManager, "Detailed Scoring Breakdown", headerFont, FONT_SIZE_HEADER_SECTION, MARGIN)
      pageManager.moveY(SPACE_AFTER_SECTION_HEADER)

      val scores = listOf(
        "Relevancy Rate" to "${cvData.scores.relevancyRate}%",
        "Targeted Job Rate" to "${cvData.scores.targetedJobRate}%",
        "Relevant Skills Match" to "${cvData.scores.relevantSkill}%",
        "Work Experience Alignment" to "${cvData.scores.workExperience}%",
        "Document Consistency" to "${cvData.scores.consistency}%",
        "Writing Quality & Clarity" to "${cvData.scores.writingQuality}%"
      )
      scores.forEach { (label, scoreValue) ->
        pageManager.ensureSpace(LINE_HEIGHT_MEDIUM)
        addListItem(pageManager, "$label: $scoreValue", bodyFont, FONT_SIZE_LIST_ITEM, MARGIN, INDENT_LIST_ITEM, pageWidth, LINE_HEIGHT_MEDIUM, isBoldKey = true)
        pageManager.moveY(SPACE_BETWEEN_LIST_ITEMS)
      }
      pageManager.moveY(SPACE_AFTER_LIST_BLOCK - SPACE_BETWEEN_LIST_ITEMS)


      // AI Analysis Summary
      addSectionWithWrappedText(
        pageManager, "AI Analysis Summary", cvData.aiAnalysis.summary,
        pageWidth, MARGIN, INDENT_LIST_ITEM
      )

      // Career Field Fit


      addSectionWithWrappedText(
        pageManager, "Career Field Fit Assessment", cvData.aiAnalysis.careerFieldFit,
        pageWidth, MARGIN, INDENT_LIST_ITEM
      )

      // Strengths
      addListSection(
        pageManager, "Identified Strengths", cvData.aiAnalysis.strengths,
        pageWidth, MARGIN, INDENT_LIST_ITEM
      )

      // Weaknesses / Areas for Improvement
      addListSection(
        pageManager, "Areas for Improvement", cvData.aiAnalysis.weaknesses,
        pageWidth, MARGIN, INDENT_LIST_ITEM
      )

      // Recommendations
      if (cvData.suggestions.isNotEmpty()) {
        addListSection(
          pageManager, "Personalized Recommendations", cvData.suggestions,
          pageWidth, MARGIN, INDENT_LIST_ITEM, isNumbered = true
        )
      }

      pageManager.contentStream.close()

      // Save PDF
      val timestamp = System.currentTimeMillis() // Generate timestamp once
      val uniqueFileName = "${baseFileName}_${timestamp}.pdf"

      val pdfFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveToMediaStore(context, document, uniqueFileName)
      } else {
        saveToExternalStorage(document, uniqueFileName)
      }

      document.close()

      if (pdfFile != null) {
        Log.d("PDFGenerator", "PDF generated successfully: ${pdfFile.absolutePath}")
        Result.success(pdfFile)
      } else {
        Log.e("PDFGenerator", "Failed to save PDF file.")
        Result.failure(IOException("Failed to save PDF file."))
      }

    } catch (e: Exception) {
      Log.e("PDFGenerator", "Failed to generate PDF", e)
      Result.failure(e)
    }
  }

  private fun addText(
    pm: PageManager,
    text: String,
    font: PDFont,
    fontSize: Float,
    x: Float,
    y: Float,
    color: Int = Color.BLACK,
    isCentered: Boolean = false
  ) {
    pm.contentStream.beginText()
    pm.contentStream.setFont(font, fontSize)
    pm.contentStream.setNonStrokingColor(Color.red(color), Color.green(color), Color.blue(color)) // Set text color

    var actualX = x
    if (isCentered) {
      val textWidth = font.getStringWidth(text) / 1000 * fontSize
      val pageWidth = PDRectangle.A4.width - 2 * MARGIN
      actualX = (pageWidth - textWidth) / 2 + MARGIN
    }

    pm.contentStream.newLineAtOffset(actualX, y)
    pm.contentStream.showText(text)
    pm.contentStream.endText()
  }

  private fun addSectionHeader(
    pm: PageManager,
    text: String,
    font: PDFont,
    fontSize: Float,
    xOffset: Float,
    color: Int = Color.BLACK // Dark Blueish Gray: 0x4A4A4A
  ) {
    pm.ensureSpace(fontSize + SPACE_AFTER_SECTION_HEADER)
    addText(pm, text, font, fontSize, xOffset, pm.currentY, color)
    // pm.moveY(fontSize + SPACE_AFTER_SECTION_HEADER) // Movement handled by caller or specific logic
  }

  private fun addHorizontalLine(pm: PageManager, xOffset: Float, width: Float, yOffset: Float = 0f, thickness: Float = 0.5f, color: Int = Color.LTGRAY) {
    pm.ensureSpace(thickness + 5f) // Ensure space for the line and a little padding
    val yPos = pm.currentY - yOffset
    pm.contentStream.setStrokingColor(Color.red(color), Color.green(color), Color.blue(color))
    pm.contentStream.setLineWidth(thickness)
    pm.contentStream.moveTo(xOffset, yPos)
    pm.contentStream.lineTo(xOffset + width, yPos)
    pm.contentStream.stroke()
    // pm.moveY(thickness + 5f) // Movement handled by caller
  }


  private fun wrapText(text: String, maxWidth: Float, font: PDFont, fontSize: Float): List<String> {
    val lines = mutableListOf<String>()
    if (text.isBlank()) return lines

    var remainingText = text
    while (remainingText.isNotEmpty()) {
      var splitIndex = remainingText.length
      var lineFits = false
      while (splitIndex > 0 && !lineFits) {
        val currentLineAttempt = remainingText.substring(0, splitIndex)
        val width = font.getStringWidth(currentLineAttempt) / 1000 * fontSize
        if (width <= maxWidth) {
          lineFits = true
          // Try to break at the last space for prettier wrapping
          val lastSpace = currentLineAttempt.lastIndexOf(' ')
          if (lastSpace != -1 && splitIndex != remainingText.length && !currentLineAttempt.endsWith(" ")) {
            // Check if the next char is not a space, to avoid breaking mid-word if the whole word fits
            if (remainingText.length > splitIndex && remainingText[splitIndex] != ' ') {
              val nextWordPart = remainingText.substring(splitIndex).takeWhile { it != ' ' }
              val lineWithNextWordPart = currentLineAttempt + nextWordPart
              if (font.getStringWidth(lineWithNextWordPart) / 1000 * fontSize <= maxWidth) {
                // If the whole word fits, don't break at space yet
              } else if (font.getStringWidth(remainingText.substring(0, lastSpace)) / 1000 * fontSize > 0) { // ensure not an empty line
                splitIndex = lastSpace + 1 // Break after space
              }
            }
          }
          lines.add(remainingText.substring(0, splitIndex).trimEnd())
          remainingText = remainingText.substring(splitIndex).trimStart()
        } else {
          splitIndex--
        }
      }
      if (!lineFits && remainingText.isNotEmpty()) { // Word is longer than maxWidth
        lines.add(remainingText) // Add the word as is (it will overflow)
        remainingText = ""
      }
    }
    return lines.filter { it.isNotEmpty() }
  }


  private fun addWrappedText(
    pm: PageManager,
    text: String,
    font: PDFont,
    fontSize: Float,
    xOffset: Float,
    maxWidth: Float,
    lineHeight: Float
  ) {
    val lines = wrapText(text, maxWidth, font, fontSize)
    lines.forEach { line ->
      pm.ensureSpace(lineHeight)
      addText(pm, line, font, fontSize, xOffset, pm.currentY)
      pm.moveY(lineHeight)
    }
  }

  private fun addListItem(
    pm: PageManager,
    text: String,
    font: PDFont,
    fontSize: Float,
    xMargin: Float,
    xIndent: Float,
    pageWidth: Float,
    lineHeight: Float,
    bullet: String = "•",
    isBoldKey: Boolean = false // For "Key: Value" format
  ) {
    val fullXOffset = xMargin + xIndent
    val textMaxWidth = pageWidth - xIndent - (font.getStringWidth("$bullet ") / 1000 * fontSize)

    var keyPart = ""
    var valuePart = text
    val separatorIndex = text.indexOf(": ")
    if (isBoldKey && separatorIndex != -1) {
      keyPart = text.substring(0, separatorIndex + 1) // Include colon
      valuePart = text.substring(separatorIndex + 1).trimStart()
    }

    val lines = if (isBoldKey && keyPart.isNotEmpty()) {
      // Wrap only the value part if key is present
      val keyWidth = bodyBoldFont.getStringWidth(keyPart) / 1000 * fontSize
      wrapText(valuePart, textMaxWidth - keyWidth, font, fontSize).mapIndexed { index, line ->
        if (index == 0) "$bullet $keyPart $line" else "  ${" ".repeat(keyPart.length)} $line" // Indent subsequent lines of value
      }
    } else {
      wrapText(text, textMaxWidth, font, fontSize).map { "$bullet $it" }
    }


    lines.forEachIndexed { index, line ->
      pm.ensureSpace(lineHeight)
      if (isBoldKey && keyPart.isNotEmpty() && index == 0) {
        // Draw key part bold, value part normal for the first line
        val bulletAndKey = "$bullet $keyPart"
        addText(pm, bulletAndKey, bodyBoldFont, fontSize, fullXOffset, pm.currentY)
        val valueXOffset = fullXOffset + (bodyBoldFont.getStringWidth(bulletAndKey) / 1000 * fontSize)
        val firstValueLine = line.substring(bulletAndKey.length).trimStart()
        addText(pm, firstValueLine, font, fontSize, valueXOffset, pm.currentY)

      } else {
        addText(pm, line, font, fontSize, fullXOffset, pm.currentY)
      }
      pm.moveY(lineHeight)
    }
  }

  private fun addSectionWithWrappedText(
    pm: PageManager,
    sectionTitle: String,
    textContent: String,
    pageWidth: Float,
    margin: Float,
    indent: Float
  ) {
    pm.ensureSpace(FONT_SIZE_HEADER_SECTION + SPACE_AFTER_SECTION_HEADER + SPACE_BEFORE_SECTION + (2 * LINE_HEIGHT_MEDIUM)) // Estimate
    addHorizontalLine(pm, margin, pageWidth)
    pm.moveY(SPACE_BEFORE_SECTION)

    addSectionHeader(pm, sectionTitle, headerFont, FONT_SIZE_HEADER_SECTION, margin)
    pm.moveY(SPACE_AFTER_SECTION_HEADER)

    addWrappedText(
      pm, textContent, bodyFont, FONT_SIZE_BODY,
      margin + indent, pageWidth - indent, LINE_HEIGHT_MEDIUM
    )
    pm.moveY(SPACE_AFTER_LIST_BLOCK) // Use list block spacing for consistency
  }

  private fun addListSection(
    pm: PageManager,
    sectionTitle: String,
    items: List<String>,
    pageWidth: Float,
    margin: Float,
    indent: Float,
    isNumbered: Boolean = false
  ) {
    if (items.isEmpty()) return

    pm.ensureSpace(FONT_SIZE_HEADER_SECTION + SPACE_AFTER_SECTION_HEADER + SPACE_BEFORE_SECTION + (items.size * LINE_HEIGHT_MEDIUM)) // Estimate
    addHorizontalLine(pm, margin, pageWidth)
    pm.moveY(SPACE_BEFORE_SECTION)

    addSectionHeader(pm, sectionTitle, headerFont, FONT_SIZE_HEADER_SECTION, margin)
    pm.moveY(SPACE_AFTER_SECTION_HEADER)

    items.forEachIndexed { index, item ->
      val bullet = if (isNumbered) "${index + 1}." else "•"
      addListItem(
        pm, item, bodyFont, FONT_SIZE_LIST_ITEM,
        margin, indent, pageWidth, LINE_HEIGHT_MEDIUM, bullet = bullet
      )
      pm.moveY(SPACE_BETWEEN_LIST_ITEMS)
    }
    pm.moveY(SPACE_AFTER_LIST_BLOCK - SPACE_BETWEEN_LIST_ITEMS)
  }


  @RequiresApi(Build.VERSION_CODES.Q)
  private fun saveToMediaStore(context: Context, document: PDDocument, uniqueFileName: String): File? {
    // uniqueFileName already includes timestamp and .pdf extension
    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueFileName)
      put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
      put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    var file: File? = null
    try {
      val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
      uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
          document.save(outputStream)
        }
        // Try to get a File object from the URI for returning, though this can be tricky
        // and not always guaranteed to be a direct file path.
        // For consistency, we'll construct the path as it would be in Downloads.
        // This is mainly for the return type, the actual saving is via the URI.
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        file = File(downloadsDir, uniqueFileName)
        if (!file!!.exists()) { // Fallback if the constructed path doesn't immediately reflect the saved file
          Log.w("PDFGenerator", "File object from constructed path doesn't exist immediately after MediaStore save. URI: $uri")
          // The file is saved, but returning a File object that accurately points to it can be complex.
          // For the purpose of this function, if URI is not null, we assume success.
          // The caller should ideally work with URIs on Q+ if possible.
        }
      }
    } catch (e: Exception) {
      Log.e("PDFGenerator", "Failed to save via MediaStore", e)
      return null // Explicitly return null on failure
    }
    return file // This might be null if URI was null, or point to an assumed location
  }

  private fun saveToExternalStorage(document: PDDocument, uniqueFileName: String): File? {
    // uniqueFileName already includes timestamp and .pdf extension
    return try {
      val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      if (!downloadsDir.exists()) {
        downloadsDir.mkdirs()
      }
      val pdfFile = File(downloadsDir, uniqueFileName)
      FileOutputStream(pdfFile).use { outputStream ->
        document.save(outputStream)
      }
      pdfFile
    } catch (e: Exception) {
      Log.e("PDFGenerator", "Failed to save to external storage", e)
      null
    }
  }
}
