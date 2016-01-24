package org.amnesty.aidoc.transform;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFText2HTML;


public class PdfToHTMLContentTransformer extends AbstractContentTransformer2 {

	@Override
	protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {

		PDDocument pdf = null;
		InputStream is = null;
		try {
			is = reader.getContentInputStream();

			pdf = PDDocument.load(is);
			PDFText2HTML textToHTML = new PDFText2HTML("UTF-8");
			Writer stringWriter = new StringWriter();
			textToHTML.writeText(pdf, stringWriter);
			writer.putContent(stringWriter.toString());
		} finally {
			if (pdf != null)
				try {
					pdf.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			if (is != null)
				try {
					is.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
		}

	}

	public boolean isTransformable(String sourceMimetype,
			String targetMimetype, TransformationOptions options) {
		return ((("application/pdf".equals(sourceMimetype))) && (("text/html"
				.equals(targetMimetype))));
	}

}
