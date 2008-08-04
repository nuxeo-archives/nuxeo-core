/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */
package org.nuxeo.ecm.core.jackrabbit.extractor;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


// same as org.apache.jackrabbit.extractor.XMLTextExtractor ; it just  call AbstractTextExtractor constructor with 
// xhtml contentTypes
public class XhtmlTextExtractor extends AbstractTextExtractor {

   /**
    * Creates a new <code>XHLTextExtractor</code> instance.
    */
   public XhtmlTextExtractor() {
       super(new String[]{"text/xhtml", "application/xhtml"});
       System.out.println("=========================");
       System.out.println("registerd extracor for xhtml");
       System.out.println("=========================");
   }

// copied from org.apache.jackrabbit.extractor.XMLTextExtractor
   public Reader extractText(InputStream stream, String type, String encoding)
           throws IOException {
       try {
           CharArrayWriter writer = new CharArrayWriter();
           ExtractorHandler handler = new ExtractorHandler(writer);

           // TODO: Use a pull parser to avoid the memory overhead
           SAXParserFactory factory = SAXParserFactory.newInstance();
           SAXParser parser = factory.newSAXParser();
           XMLReader reader = parser.getXMLReader();
           reader.setContentHandler(handler);
           reader.setErrorHandler(handler);

           // It is unspecified whether the XML parser closes the stream when
           // done parsing. To ensure that the stream gets closed just once,
           // we prevent the parser from closing it by catching the close()
           // call and explicitly close the stream in a finally block.
           InputSource source = new InputSource(new FilterInputStream(stream) {
               public void close() {
               }
           });
           if (encoding != null) {
               source.setEncoding(encoding);
           }
           reader.parse(source);

           return new CharArrayReader(writer.toCharArray());
       } catch (ParserConfigurationException e) {
           return new StringReader("");
       } catch (SAXException e) {
           return new StringReader("");
       } finally {
           stream.close();
       }
   }

}

