package epub4j.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import epub4j.Constants;
import epub4j.domain.MediaType;
import epub4j.domain.MediaTypes;
import epub4j.domain.Resource;
import epub4j.epub.EpubProcessorSupport;
import jazzlib.ZipEntry;
import jazzlib.ZipInputStream;

/**
 * Various resource utility methods
 *
 * @author paul
 */
public class ResourceUtil {

  public static Resource createResource(File file) throws IOException {
    if (file == null) {
      return null;
    }
    MediaType mediaType = MediaTypes.determineMediaType(file.getName());
    byte[] data = IOUtil.toByteArray(new FileInputStream(file));
    Resource result = new Resource(data, mediaType);
    return result;
  }


  /**
   * Creates a resource with as contents a html page with the given title.
   *
   * @param title
   * @param href
   * @return a resource with as contents a html page with the given title.
   */
  public static Resource createResource(String title, String href) {
    String content =
        "<html><head><title>" + title + "</title></head><body><h1>" + title
            + "</h1></body></html>";
    return new Resource(null, content.getBytes(), href, MediaTypes.XHTML,
        Constants.CHARACTER_ENCODING);
  }

  /**
   * Creates a resource out of the given zipEntry and zipInputStream.
   *
   * @param zipEntry
   * @param zipInputStream
   * @return a resource created out of the given zipEntry and zipInputStream.
   * @throws IOException
   */
  public static Resource createResource(ZipEntry zipEntry,
      ZipInputStream zipInputStream) throws IOException {
    return new Resource(zipInputStream, zipEntry.getName());

  }

  public static Resource createResource(ZipEntry zipEntry,
      InputStream zipInputStream) throws IOException {
    return new Resource(zipInputStream, zipEntry.getName());

  }

  /**
   * Converts a given string from given input character encoding to the requested output character encoding.
   *
   * @param inputEncoding
   * @param outputEncoding
   * @param input
   * @return the string from given input character encoding converted to the requested output character encoding.
   * @throws UnsupportedEncodingException
   */
  public static byte[] recode(String inputEncoding, String outputEncoding,
      byte[] input) throws UnsupportedEncodingException {
    return new String(input, inputEncoding).getBytes(outputEncoding);
  }

  /**
   * Gets the contents of the Resource as an InputSource in a null-safe manner.
   *
   */
  public static InputSource getInputSource(Resource resource)
      throws IOException {
    if (resource == null) {
      return null;
    }
    Reader reader = resource.getReader();
    if (reader == null) {
      return null;
    }
    InputSource inputSource = new InputSource(reader);
    return inputSource;
  }


  /**
   * Reads parses the xml therein and returns the result as a Document
   */
  public static Document getAsDocument(Resource resource)
      throws SAXException, IOException, ParserConfigurationException {
    return getAsDocument(resource,
        EpubProcessorSupport.createDocumentBuilder());
  }

  /**
   * Reads the given resources inputstream, parses the xml therein and returns the result as a Document
   *
   * @param resource
   * @param documentBuilder
   * @return the document created from the given resource
   * @throws UnsupportedEncodingException
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public static Document getAsDocument(Resource resource,
      DocumentBuilder documentBuilder)
      throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
    InputSource inputSource = getInputSource(resource);
    if (inputSource == null) {
      return null;
    }
    Document result = documentBuilder.parse(inputSource);
    return result;
  }
}
