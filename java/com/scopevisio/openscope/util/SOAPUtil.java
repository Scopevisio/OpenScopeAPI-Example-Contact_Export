/**
Copyright (c) 2015, Scopevisio AG
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or other
materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may
be used to endorse or promote products derived from this software without specific
prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 */
package com.scopevisio.openscope.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import com.scopevisio.openscope.util.URLPost.PostResult;


/**
 * various soap utility methods
 * 
 */
public class SOAPUtil {
    private static final Logger log = Logger.getLogger(SOAPUtil.class.getName());
    public static String toString(SOAPMessage message) {
        String r = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try {
                message.writeTo(bout);
            } finally {
                bout.close();
            }
            r = bout.toString("UTF-8");
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to convert message to string", e);
        }
        return r;
    }
    
    public static String saveDocumentToString(Node doc) {
        return saveDocumentToString(doc, false);
    }
    
    /**
     * Writes document to a string
     * @param doc
     * @param indent whether to indent the document.
     * @return the document as a string representation.
     */
    public static String saveDocumentToString(Node doc, boolean indent) {
        StringWriter writer = new StringWriter();
        try {
            Source source = new DOMSource(doc);
            Result result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            if (indent)
                tf.setAttribute("indent-number", new Integer(4));
            Transformer xformer = tf.newTransformer();
            if (indent) 
               xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, result);
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to write xml to string", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                /* ignore */
            }
        }
        return writer.toString();
    }

    
    public static Element[] getChildrenByTagName(Node element, String name) {
        ArrayList<Element> out = new ArrayList<Element>();
        NodeList nl = element instanceof Document ? ((Document)element).getChildNodes() : 
            ((Element)element).getChildNodes();
        for (int i = 0, n = nl.getLength(); i < n; ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            Element child = (Element)node;
            if (name.equals(child.getNodeName())) {
                out.add((Element)node);
            }
        }
        return out.toArray(new Element[0]);
    }
    
    public static String getText(Element element, String textElement) {
        String s = null;
        try {
            Element[] childrenByTagName = getChildrenByTagName(element, textElement);
            if (childrenByTagName.length > 0) {
                if (childrenByTagName[0].getTextContent().length() > 0)
                        s = childrenByTagName[0].getTextContent().trim();
            } else {
                log.log(Level.WARNING, "failed to get text from nested text element \"" + textElement + "\" (element not found)");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to get text from nested text element \"" + textElement + "\"", e);
        }
        return s;
    }

    public static String getText(Element element, String textElement, String defaultValue) {
        String s = null;
        try {
            Element[] childrenByTagName = getChildrenByTagName(element, textElement);
            if (childrenByTagName.length > 0) {
                if (childrenByTagName[0].getTextContent().trim().length() > 0){
                    s = childrenByTagName[0].getTextContent().trim();
                    }
                else {
                    return defaultValue;
            } 
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to get text from nested text element \"" + textElement + "\"", e);
        }
        return s;
    }
    
    public static String getAttribute(Element element, String attributeName) {
        String s = null;
        if (element.hasAttribute(attributeName)) {
            try {
                s = element.getAttribute(attributeName);
            } catch (Exception e) {
                 log.log(Level.SEVERE, "failed to get attribute value for \"" + attributeName + "\"", e);
            }
        }
        return s;
    }
    
    public static Boolean getBoolean(Element element, String textElement, Boolean defaultValue) {
        String s = "";
        try {
            Element[] childrenByTagName = getChildrenByTagName(element, textElement);
            if (childrenByTagName.length > 0) {
                s = childrenByTagName[0].getTextContent().trim();
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to get text from nested text element \"" + textElement + "\"", e);
        }
        return Boolean.valueOf(s);
    }
    
    public static BigDecimal getBigDecimal(Element element, String textElement) {
        String s = getText(element, textElement);
        BigDecimal result = null;
        if (s != null) {
            result = new BigDecimal(s.replace(".", "").replace(',', '.'));
        }
        return result;
    }

    public static BigInteger getBigInteger(Element element, String textElement) {
        String s = getText(element, textElement);
        BigInteger result = null;
        if (s != null) {
            try {
                result = new BigInteger(s);
            } catch(Exception e) {
                log.log(Level.WARNING, "failed to convert field \"" + textElement + "\" to big-int", e);
            }
        }
        return result;
    }
    
    public static Date getDate(Element element, String textElement) throws ParseException {
        String s = getText(element, textElement);
        Date result = null;
        if (s != null) {
            try {
                result = new SimpleDateFormat("dd.MM.yyyy").parse(s);
            } catch (Exception e) {
//                log.log(Level.SEVERE, "field dump for followup severe: " + saveDocumentToString(element, true));
                log.log(Level.SEVERE, "failed to convert field content \"" + s+ "\" to date", e);
            }
        }
        return result;
    }
    
    
    public static String getTextFromAttributes(Element element, String attributeName) {
        return getAttribute(element, attributeName);
    }
    
    public static BigDecimal getBigDecimalFromAttributes(Element element, String attributeName) {
        String s = getAttribute(element, attributeName);
        BigDecimal result = null;
        if (s != null) {
            try {
                result = new BigDecimal(s);
            } catch (Exception e) {
//                log.log(Level.SEVERE, "field dump for followup severe: " + saveDocumentToString(element, true));
                log.log(Level.SEVERE, "failed to convert \"" + s + "\" to BigDecimal", e);
            }
        }
        return result;
    }
    
    public static BigInteger getBigIntegerFromAttributes(Element element, String attributeName) {
        String s = getTextFromAttributes(element, attributeName);
        BigInteger result = null;
        if (s != null) {
            try {
                result = new BigInteger(s);
            } catch (Exception e) {
//                log.log(Level.SEVERE, "field dump for followup severe: " + saveDocumentToString(element, true));
                log.log(Level.SEVERE, "failed to convert \"" + s + "\" to BigInteger", e);
            }
        }
        return result;
    }
    
    public static Date getDateFromAttributes(Element element, String attributeName) throws ParseException {
        String s = getAttribute(element, attributeName);
        Date result = null;
        if (s != null && !s.isEmpty()) {
            try {
                result = new SimpleDateFormat("dd.MM.yyyy").parse(s);
            } catch (Exception e) {
                log.log(Level.SEVERE, "field dump for followup severe: " + saveDocumentToString(element, true));
                log.log(Level.SEVERE, "failed to convert field content \"" + s+ "\" to date", e);
            }
        }
        return result;
    }
    
    /*
     * @Deprecated replaced by {@link #createRequestWithNamespace()}
     */
    @Deprecated
    public static SOAPMessage createRequest(
            String customer,
            String user,
            String pass,
            String language,
            String organisation) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage req = mf.createMessage();
        SOAPBody body = req.getSOAPBody();
        SOAPElement authnElement = body.addChildElement("authn");
        authnElement.addChildElement("customer").setTextContent(customer);
        authnElement.addChildElement("user").setTextContent(user);
        authnElement.addChildElement("pass").setTextContent(pass);
        authnElement.addChildElement("language").setTextContent(language);
        authnElement.addChildElement("organisation").setTextContent(organisation);
        return req;
    }
    
    
    public static SOAPMessage createRequestWithNamespace(
            String customer,
            String user,
            String pass,
            String organisation) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage req = mf.createMessage();
        SOAPBody body = req.getSOAPBody();
        SOAPElement requestElement = body.addChildElement("req", "ns1", "http://www.scopevisio.com/");
        SOAPElement authnElement = requestElement.addChildElement("authn");
        authnElement.addChildElement("customer").setTextContent(customer);
        authnElement.addChildElement("user").setTextContent(user);
        authnElement.addChildElement("pass").setTextContent(pass);
        authnElement.addChildElement("organisation").setTextContent(organisation);
        return req;
    }
    
    public static SOAPMessage createRequestWithNamespace(
            String customer,
            String user,
            String pass,
            String language,
            String organisation) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage req = mf.createMessage();
        SOAPBody body = req.getSOAPBody();
        SOAPElement requestElement = body.addChildElement("req", "ns1", "http://www.scopevisio.com/");
        SOAPElement authnElement = requestElement.addChildElement("authn");
        authnElement.addChildElement("customer").setTextContent(customer);
        authnElement.addChildElement("user").setTextContent(user);
        authnElement.addChildElement("pass").setTextContent(pass);
        authnElement.addChildElement("language").setTextContent(language);
        authnElement.addChildElement("organisation").setTextContent(organisation);
        return req;
    }
    
    public static SOAPMessage createCRequestWithNamespace(
            String sessionid,
            String transactionid) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage req = mf.createMessage();
        SOAPBody body = req.getSOAPBody();
        SOAPElement requestElement = body.addChildElement("req", "ns1", "http://www.scopevisio.com/");
        SOAPElement authnElement = requestElement.addChildElement("credentials");
        authnElement.addChildElement("sid").setTextContent(sessionid);
        authnElement.addChildElement("tid").setTextContent(transactionid);
        return req;
    }
    
    public static void addParameters(
            SOAPMessage message,
            Map<String,Object> parameters) throws Exception {
        SOAPBody body = message.getSOAPBody();
        SOAPElement argsElement = body.addChildElement("args");
        TreeSet<String> keys = new TreeSet<String>(parameters.keySet());
        for (String key : keys) {
            Object value = parameters.get(key);
            argsElement.addChildElement(key).setTextContent(value == null ? "" : value.toString());
        }
    }
    
    /**
     * Parses a reply soap message.
     * @param rawResponse the raw response that was received
     * @return the parsed soap message.
     * @throws Exception
     */
    public static SOAPMessage createReply(String rawResponse) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        ByteArrayInputStream bin = new ByteArrayInputStream(rawResponse.getBytes("UTF-8"));
        SOAPMessage reply = null;
        try {
            MimeHeaders headers = new MimeHeaders();
            System.err.println(rawResponse);
            reply = mf.createMessage(headers, bin);
        }
        finally {
            bin.close();
        }
        return reply;
    }
    
    public static void filter(Reader reader, Writer writer, XMLFilterImpl filter) throws Exception {
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        XMLReader parent = XMLReaderFactory.createXMLReader();
        filter.setParent(parent);
        SAXSource source = new SAXSource(filter, new InputSource(reader));
        StreamResult result = new StreamResult(writer);
        xformer.transform(source, result);
    }
    
    public static String filter(String input, XMLFilterImpl filter) throws Exception {
        StringWriter writer = new StringWriter();
        try {
            StringReader reader = new StringReader(input);
            try {
                filter(reader, writer, filter);
            } finally {
                reader.close();
            }
            
        } finally {
            writer.close();
        }
        return writer.toString();
    }
    
    public static String indent(String xml) throws Exception {
        return filter(xml, new XMLIndentFilter());
    }

    public static ArrayList<String> loadFile(String filename, String encoding) throws Exception {
        ArrayList<String> l = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encoding));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                l.add(line);
            }
        } finally {
            reader.close();
        }
        return l;
        
    }
    
    public static ArrayList<ArrayList<String>> splitSegments(ArrayList<String> lines) {
        final int DOCINDEX = 2;
        ArrayList<ArrayList<String>> segments = new ArrayList<ArrayList<String>>();
        ArrayList<String> segment = null;
        String lastDocument = null;
        for (String line : lines) {
            String[] fields = line.split(";");
            String thisDocument = DOCINDEX < fields.length ? fields[DOCINDEX] : "nan";
            if (lastDocument != null && !thisDocument.equals(lastDocument) && segment != null && segment.size() > 500) {
                segment = null;
            }
            if(segment == null) {
                segment = new ArrayList<String>();
                segments.add(segment);
            }
            segment.add(line);
            lastDocument = thisDocument;
        }
        return segments;
    }
    
    public static SOAPBody getResultBody(PostResult result) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage req = mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(result.getReply().getBytes("UTF-8")));
        SOAPBody body = req.getSOAPBody();
        return body;
    }
    

}
