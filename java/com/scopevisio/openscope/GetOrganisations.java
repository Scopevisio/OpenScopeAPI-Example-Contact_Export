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

import org.json.JSONArray;

import com.scopevisio.openscope.util.SOAPUtil;
import com.scopevisio.openscope.util.URLPost;
import com.scopevisio.openscope.util.URLPost.PostResult;

/**
 * 
 * @author mfg8876
 *
 */
public class GetOrganisations {
    
    static String getOrganisations(String args[]) throws Exception {
        boolean verbose = System.getProperty("com.scopevisio.openscope.verbose", "").equalsIgnoreCase("verbose");
        if (verbose) {
            System.err.println("Retrieving list of organisations");
            System.err.println("================================");
        }
        String path = "/api/soap/accounting/accounting.GetOrganisations";
        String url = args[0].replaceAll("/+$", "") + path;
        String customer = args[1];
        String user = args[2];
        String pass = args[3];

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

        if (verbose)
            System.err.println(SOAPUtil.indent(SOAPUtil.toString(request)));

        // post SOAP
        PostResult result = new URLPost().postSoap(url, request);
        if (result.getResponseCode() != 200)
        throw new Exception("Unexpected response, HTTP Status Code: " + result.getResponseCode()
                    + ", Reason-Phrase: " + result.getReply());
        String reply = result.getReply();
        if (verbose)
            System.err.println("responseCode: " + result.getResponseCode() + ", reply: " + reply);
        
        return reply;
    }

    static String getFirstOrganisation(String reply) throws Exception {
        JSONArray array = new JSONArray(reply);
        if (array.length() == 0 || array.optString(0).isEmpty())
            throw new Exception("No organisation is available");
        if (array.length() == 1)
            return array.getString(0);
        //Pick the first non-demo organisation
        for(int idx = 0, len = array.length(); idx < len; idx++) {
            String org = array.optString(idx);
            if (!"Scopevisio Demo AG".equals(org))
                return org;
        }
        return array.getString(0);
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Die Argumentenliste muss von genau 4 Elementen bestehen.");
            System.err.println("Jedes Element, das Leerraum enthält, muss durch Anführungszeichen eingeschlossen sein.");
            System.err.println("[Webdienst-URL] [Kunde] [Benutzer] [Kennwort]");
            System.err.println("Webdienst-URL besteht von den folgenden URL-Elemente:");
            System.err.println("scheme host [:port]");
            System.err.println("Port ist ein optionales Element");
            System.err.println("Kunde entspricht der veröffentlichen Kundennummer bei Scopevisio");
            System.err.println("Beispiel zur Argumentenlist");
            System.err.println("https://appload.scopevisio.com 2000000 user@example.com password");
            return;
        }
        try {
            System.out.println(getOrganisations(args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}