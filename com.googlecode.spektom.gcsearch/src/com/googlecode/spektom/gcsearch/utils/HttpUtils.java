package com.googlecode.spektom.gcsearch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * Various HTTP client utils
 * 
 * @author Michael
 * 
 */
public class HttpUtils {

	static {
		Protocol.registerProtocol(
				"https",
				new Protocol(
						"https",
						(ProtocolSocketFactory) new EasySSLProtocolSocketFactory(),
						443));
	}

	/**
	 * Returns response from the given URL as a string
	 * 
	 * @throws IOException
	 */
	public static String getString(String url) throws IOException {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(10000);
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
			return readResponse(method);
		} finally {
			method.releaseConnection();
		}
	}

	public static String readResponse(HttpMethod method) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(
				method.getResponseBodyAsStream()));
		try {
			StringBuilder buf = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				if (buf.length() > 0) {
					buf.append('\n');
				}
				buf.append(line);
			}
			return buf.toString();
		} finally {
			r.close();
		}
	}
}
