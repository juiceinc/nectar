package com.juiceanalytics.nectar.util

import java.lang.String
import org.apache.commons.fileupload.{FileItemHeadersSupport, FileItemHeaders, FileItemFactory, FileItem}
import java.io._


/**
 * Is a quick and dirty implementation of a memory-only FileItem.
 *
 * @author Jon Buffington
 */
@serializable
case class MemoryFileItem(var fieldName: String, val contentType: String, var formField: Boolean, val fileName: String) extends FileItem with FileItemHeadersSupport {

  private val content: ByteArrayOutputStream = new ByteArrayOutputStream

  def getInputStream: InputStream = new ByteArrayInputStream(content.toByteArray);

  def getOutputStream: OutputStream = content

  def isFormField: Boolean = formField

  def setFormField(state: Boolean) {
    formField = state
  }

  def getFieldName: String = fieldName

  def setFieldName(name: String) {
    fieldName = name
  }

  def delete() {
    content.reset()
  }

  def write(file: File) {
    // TODO ?
  }

  def getString: String = content.toString

  // TODO Add encoding support.
  def getString(encoding: String) = getString

  def get: Array[Byte] = content.toByteArray

  def getSize: Long = content.size

  def isInMemory: Boolean = true

  def getName: String = fileName

  def getContentType: String = contentType

  private var _headers: FileItemHeaders = _

  /**
   * Returns the file item headers.
   * @return The file items headers.
   */
  def getHeaders: FileItemHeaders = _headers

  /**
   * Sets the file item headers.
   * @param pHeaders The file items headers.
   */
  def setHeaders(pHeaders: FileItemHeaders) {
    _headers = pHeaders;
  }

}

class MemoryFileItemFactory extends FileItemFactory {
  def createItem(fieldName: String, contentType: String, isFormField: Boolean, fileName: String): FileItem = MemoryFileItem(fieldName, contentType, isFormField, fileName)
}
