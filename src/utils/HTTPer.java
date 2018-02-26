package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPer {


	public static class Builder {
		private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
		private String url;
		private String method;

		public Builder() {
		}

		public Builder setMethod(String method) {
			this.method = method;
			return this;
		}

		public Builder addURLParameter(String key, String value) {
			this.parameters.put(key, value);
			return this;
		}

		public Builder setRootURL(String url) {
			this.url = url;
			return this;
		}

		public HTTPer build() throws IOException {
			StringBuilder urlBuilder = new StringBuilder();

			urlBuilder.append(this.url);

			if (this.parameters.size() > 0) {
				urlBuilder.append('?');
			}

			boolean firstParameter = true;
			for(Map.Entry<String, String> parameter : parameters.entrySet()) {
				if (firstParameter) {
					firstParameter = false;
				} else {
					urlBuilder.append('&');
				}
				urlBuilder.append(URLEncoder.encode(parameter.getKey(), "utf-8"));
				urlBuilder.append('=');
				urlBuilder.append(URLEncoder.encode(parameter.getValue(), "utf-8"));
			}
			return new HTTPer(urlBuilder.toString(), this.method.toUpperCase());

		}

	}

	private HttpURLConnection connection;

	private HTTPer(String url, String method) throws IOException {
		URL url1 = new URL(url);
		connection = (HttpURLConnection) url1.openConnection();
		connection.setRequestMethod(method);


		if (method.equalsIgnoreCase("POST")) {
			connection.setDoOutput(true);
		}
	}

	public String get() {
		try {
			String response = readInputStream(this.connection.getInputStream());
			int responseCode = this.connection.getResponseCode();

			if (!String.valueOf(responseCode).startsWith("2")) {
				throw new IOException("Server has responded with: " + responseCode);
			}
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			disconnect();
		}
	}

	public void disconnect() {
		this.connection.disconnect();
	}

	@Override
	public String toString() {
		return "Active connection: " + this.connection.getURL().toString();
	}

	private String readInputStream(InputStream inputStream) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			char buffer[] = new char[4096];

			InputStreamReader reader = new InputStreamReader(inputStream);
			int bytesRead = 0;
			while (bytesRead != -1){
				bytesRead = reader.read(buffer);
				for (int i = 0; i < bytesRead; i++) {
					stringBuilder.append(buffer[i]);
				}
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}
