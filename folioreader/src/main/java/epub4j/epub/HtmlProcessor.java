package epub4j.epub;

import java.io.OutputStream;

import epub4j.domain.Resource;

public interface HtmlProcessor {

  void processHtmlResource(Resource resource, OutputStream out);
}
