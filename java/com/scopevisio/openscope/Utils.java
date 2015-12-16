package com.scopevisio.openscope;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.xml.soap.SOAPMessage;

public class Utils {

	public static void verbose(String message) {
		if (System.getProperty("com.scopevisio.openscope.verbose", "").equalsIgnoreCase("verbose"))
			System.err.println(message);
	}

	public static String soapMessageToString(SOAPMessage message) {
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
			e.printStackTrace();
		}
		return r;
	}

	/**
	 * Helper class, encapsulates url-post results.
	 */
	protected static class PostResult {
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

	public PostResult postSoap(String url, SOAPMessage soapMessage) throws Exception {
		String[] headers = new String[] { "SOAPAction", "", "Cache-Control", "no-cache", "Content-Type",
				"text/xml; charset=utf-8" };
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
			} else {
				return new PostResult(null, 500);
			}
		}

		// Get the response
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(responseInputStream, baos, 8192);
		String s = baos.toString();

		PostResult postResult = new PostResult(s, HttpURLConnection.HTTP_OK);
		System.err.println("post-soap to \"" + url + "\" completed with status:=" + postResult.responseCode);
		return postResult;
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
			} catch (IOException e) {
				/* empty */}
			try {
				output.close();
			} catch (IOException e) {
				/* empty */}
		}
	}

	@SuppressWarnings("unchecked")
	public static <E> E[] prepend(E value, E[] values) {
		E[] array = (E[]) Array.newInstance(value.getClass(), values.length + 1);
		array[0] = value;
		System.arraycopy(values, 0, array, 1, values.length);
		return array;
	}

}
