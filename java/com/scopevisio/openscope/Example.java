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

import java.util.StringTokenizer;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import com.scopevisio.openscope.Utils.PostResult;

/**
 * This class demonstrates how to obtain a semicolon-separated list of contacts that were created during the 
 * last 100 days. Refer to README.md on specific information on the single processing steps. 
 */
public class Example {

	public static final String DEFAULT_URL = System.getProperty("com.scopevisio.openscope.webservice.url",
			"https://appload.scopevisio.com");

	public static void main(String[] args) throws Exception {

		// parse the command line
		if (args.length != 3) {
			System.err.println("USAGE: java com.scopevisio.openscope.Example [customerid] [username] [password]");
			System.err.println();
			System.err.println(" [customerid] - Public scopevisio customer ID, e.g., 2000000.");
			System.err.println(" [username]   - Login username, e.g., user@example.com");
			System.err.println(" [password]   - Login password, e.g., 'password'");
			return;
		}

		/*
		 * Step 1: Call API method accounting.GetOrganisations to obtain one of your organizations. 
		 */
		String organisation = null;
		{
			Utils.verbose("Retrieving list of organisations");
			Utils.verbose("================================");

			// gather mandatory data
			String path = "/api/soap/accounting/accounting.GetOrganisations";
			String url = DEFAULT_URL.replaceAll("/+$", "") + path;
			String customer = args[0];
			String user = args[1];
			String pass = args[2];

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

			Utils.verbose(Utils.soapMessageToString(request));

			// post SOAP
			PostResult result = Utils.postSoap(url, request);
			if (result.getResponseCode() != 200)
				throw new Exception("Unexpected response, HTTP Status Code: " + result.getResponseCode()
						+ ", Reason-Phrase: " + result.getReply());

			// extract reply
			String reply = result.getReply();
			Utils.verbose("responseCode: " + result.getResponseCode() + ", reply: " + reply);

			// Here you should later probably insert a real JSON parser
			reply = reply.replaceAll("[\\[\\]]", "");
			StringTokenizer stringTokenizer = new StringTokenizer(reply, ",");
			organisation = stringTokenizer.nextToken().replaceAll("\"", "");

		}

		// print out what we've found
		System.out.println("Found organization: " + organisation);

		/*
		 * Step 2: Call API method Contact.exportExtendedCSV to obtain contacts in CSV-format 
		 */
		String contacts = null;
		{
			// gather mandatory data
			String path = "/api/soap/contact/Contact.exportExtendedCSV";
			String url = DEFAULT_URL.replaceAll("/+$", "") + path;
			String customer = args[0];
			String user = args[1];
			String pass = args[2];
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

			Utils.verbose(Utils.soapMessageToString(request));

			// post SOAP
			PostResult result = Utils.postSoap(url, request);
			if (result.getResponseCode() != 200)
				throw new Exception("Unexpected response, HTTP Status Code: " + result.getResponseCode()
						+ ",Reason-Phrase: " + result.getReply());

			// extract reply
			String reply = result.getReply();
			Utils.verbose("responseCode: " + result.getResponseCode() + ", reply: " + reply);

			// extract the CSV-data from the response envelope (later you could do this with a SOAP parser)
			contacts = reply.replaceAll(".*<data>", "").replaceAll("</data>.*", "");

		}

		// print out what we've found
		System.out.println("Found contacts:\n\n" + contacts);
	}
}
