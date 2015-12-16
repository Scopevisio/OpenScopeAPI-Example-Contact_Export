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
package com.scopevisio.openscope;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import com.scopevisio.openscope.util.SOAPUtil;
import com.scopevisio.openscope.util.URLPost;
import com.scopevisio.openscope.util.URLPost.PostResult;

/**
 * 
 * @author mfg8876
 *
 */
public class ContactExportExtendedCsv {
    
    static String getContacts(String[] args) throws Exception {
        boolean verbose = System.getProperty("com.scopevisio.openscope.verbose", "").equalsIgnoreCase("verbose");
        if (verbose) {
            System.err.println("Retrieving contacts");
            System.err.println("===================");
        }
        String path = "/api/soap/contact/Contact.exportExtendedCSV";
        String url = args[0].replaceAll("/+$", "") + path;
        String organisation = args[1];
        String customer = args[2];
        String user = args[3];
        String pass = args[4];
        String language = "de_DE";

        // prepare SOAP
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage request = mf.createMessage();
        SOAPBody body = request.getSOAPBody();
        SOAPElement requestElement = body.addChildElement("req", "ns1", "http://www.scopevisio.com/");
        
        // authorization tag
        SOAPElement authnElement = requestElement.addChildElement("authn");
        authnElement.addChildElement("customer").setTextContent(customer);
        authnElement.addChildElement("user").setTextContent(user);
        authnElement.addChildElement("pass").setTextContent(pass);
        authnElement.addChildElement("language").setTextContent(language);
        authnElement.addChildElement("organisation").setTextContent(organisation);
        SOAPElement req = (SOAPElement) body.getChildElements().next();

        // args/data tag
        SOAPElement configElement = req.addChildElement("args");
        long hundredDaysAgo = System.currentTimeMillis();
        hundredDaysAgo -= 100 * 24 * 60 * 60 * 1000L;
        configElement.addChildElement("createdSinceTimestamp").setTextContent(Long.toString(hundredDaysAgo));
        
        if (verbose)
            System.err.println(SOAPUtil.indent(SOAPUtil.toString(request)));

        // post SOAP
        PostResult result = new URLPost().postSoap(url, request);
        if (result.getResponseCode() != 200)
            throw new Exception("Unexpected response, HTTP Status Code: " + result.getResponseCode()
                    + ",Reason-Phrase: " + result.getReply());
        String reply = result.getReply();
        if (verbose)
            System.err.println("responseCode: " + result.getResponseCode() + ", reply: " + reply);
        
        return reply;
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Die Argumentenliste muss von genau 5 Elementen bestehen.");
            System.err.println("Jedes Element, das Leerraum enthält, muss durch Anführungszeichen eingeschlossen sein.");
            System.err.println("[Webdienst-URL] [Gesselschaft] [Kunde] [Benutzer] [Kennwort]");
            System.err.println("Webdienst-URL besteht von den folgenden URL-Elemente:");
            System.err.println("scheme host [:port]");
            System.err.println("Port ist ein optionales Element");
            System.err.println("Kunde entspricht der veröffentlichen Kundennummer bei Scopevisio");
            System.err.println("Beispiel zur Argumentenlist");
            System.err.println("https://appload.scopevisio.com \"Scopevisio Demo AG\" 2000000 user@example.com password");
            return;
        }
        try {
            System.out.println(getContacts(args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}