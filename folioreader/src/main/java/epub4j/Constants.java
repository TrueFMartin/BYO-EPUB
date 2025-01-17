package epub4j;

import epub4j.util.GVersion;

public interface Constants {

  String CHARACTER_ENCODING = "UTF-8";
  String DOCTYPE_XHTML = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">";
  String NAMESPACE_XHTML = "http://www.w3.org/1999/xhtml";
  String EPUB4J_GENERATOR_NAME = "EPUB4J v"
      + GVersion.VERSION.replace("-SNAPSHOT", "") + "."
      + GVersion.GIT_REVISION;
  char FRAGMENT_SEPARATOR_CHAR = '#';
  String DEFAULT_TOC_ID = "toc";
}
