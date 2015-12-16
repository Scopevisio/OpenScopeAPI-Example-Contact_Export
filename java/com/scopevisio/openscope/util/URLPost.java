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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.soap.SOAPMessage;

public class URLPost {

    private static final Logger log = Logger.getLogger(URLPost.class.getName());
    
    /**
     * Helper class, encapsulates url-post results.
     */
    public static class PostResult {
        private String reply;
        private int responseCode;
        public PostResult(String reply, int responseCode) {
            this.reply = reply;
            this.responseCode = responseCode;
        }
        public String getReply() {
            return reply;
        }
        public int getResponseCode() {
            return responseCode;
        }
    }

    public URLPost() {
    }

    /**
     * Utility method to post a soap message using a standard set of headers.
     * @param url the url to post to
     * @param soapMessage message to post
     * @return the reply of the server.
     * @throws Exception in error case
     */
    public PostResult postSoap(String url, SOAPMessage soapMessage) throws Exception {
        String[] headers = new String[]{
                "SOAPAction", "",
                "Cache-Control", "no-cache",
                "Content-Type", "text/xml; charset=utf-8"
        };
        return postSoap(url, soapMessage, headers);
    }
        
    /**
     * Utility method to post a soap message. Additional header like e.g. Post-Action
     * can also be passed.
     *
     * @param url         the url to post to
     * @param soapMessage message to post
     * @param headers     used for the request
     * @return the reply of the server.
     * @throws Exception in error case
     */
    public PostResult postSoap(String url, SOAPMessage soapMessage, String... headers) throws Exception {
        PostResult postResult = postSoap0(url, soapMessage, headers);
        if (log.isLoggable(Level.INFO)) {
            log.info("post-soap to \"" + url + "\" completed with status:=" + postResult.responseCode);
        }
        return postResult;
    }

    /**
     * Utility method to post an empty payload using a standard set of headers.
     * @param url the url to post to
     * @return the reply of the server.
     * @throws Exception in error case
     */
    public PostResult post(String url) throws Exception {
        String[] headers = new String[]{
                "Cache-Control", "no-cache",
                "Content-Type", "application/x-www-form-urlencoded"
        };
        return postSoap(url, null, headers);
    }
    
    private PostResult postSoap0(String url, SOAPMessage soapMessage, String... headers) throws Exception {
        // Send data via post, very simple ;-)
        URL urlurl = new URL(url);
        URLConnection conn = urlurl.openConnection();
        if (conn instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
            httpURLConnection.setRequestMethod("POST");
        }
        for (int i = 0; i < headers.length; i += 2) {
            String key = headers[i];
            String value = headers[i + 1];
            conn.addRequestProperty(key, value);
        }

        conn.setDoOutput(true);
        if (soapMessage != null)
            soapMessage.writeTo(conn.getOutputStream());

        InputStream responseInputStream = null;
        try {
            responseInputStream = conn.getInputStream();
        } catch (Exception e) {
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
                String msg = httpURLConnection.getResponseMessage();
                msg = URLDecoder.decode(msg, "UTF-8");
                return new PostResult(msg, httpURLConnection.getResponseCode());
            }
            else {
                return new PostResult(null, 500);
            }
        }
        
        // Get the response
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(responseInputStream, baos);
        String s = baos.toString();
        
        return new PostResult(s, HttpURLConnection.HTTP_OK);
    }


    
    /**
     * Copies bytes from an InputStream to an OutputStream. The streams are closed after copying
     * 
     * @param input the InputStream to read from
     * @param output the OutputStream to write to
     * @return the number of bytes copied
     * @throws IOException in case of an I/O problem
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, 8192);
    }
    
    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        try {
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                count += bytesRead;
            }
            return count;
        } finally {
            try {
                input.close();
            } catch (IOException e) { /* empty */}
            try {
                output.close();
            } catch (IOException e) { /* empty */}
        }
    }
 
}
