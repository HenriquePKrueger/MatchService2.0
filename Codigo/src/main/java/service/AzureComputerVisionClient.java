package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.cdimascio.dotenv.Dotenv;

public class AzureComputerVisionClient {
	private static final int DEFAULT_TIMEOUT_MS = 10000;
	private static final int READ_MAX_ATTEMPTS = 6;
	private static final long READ_POLL_INTERVAL_MS = 1000L;
	private final String endpoint;
	private final String key;
	private final int timeoutMs;

	public AzureComputerVisionClient() {
		this.endpoint = getEnv("AZURE_COMPUTER_VISION_ENDPOINT", null);
		this.key = getEnv("AZURE_COMPUTER_VISION_KEY", null);
		this.timeoutMs = getIntEnv("IMAGE_MODERATION_TIMEOUT_MS", DEFAULT_TIMEOUT_MS);
	}

	public String analyzeImage(byte[] imageBytes) throws IOException {
		if (isBlank(endpoint) || isBlank(key)) {
			throw new IOException("Azure Computer Vision endpoint/key not configured.");
		}

		String baseEndpoint = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
		URL url = new URL(baseEndpoint + "/vision/v3.2/analyze?overload=stream&visualFeatures=Adult,Tags,Description&language=pt");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setConnectTimeout(timeoutMs);
		connection.setReadTimeout(timeoutMs);
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", key);
		connection.setRequestProperty("Content-Type", "application/octet-stream");

		try (OutputStream outputStream = connection.getOutputStream()) {
			outputStream.write(imageBytes);
		}

		int statusCode = connection.getResponseCode();
		String body = readBody(statusCode >= 200 && statusCode < 300 ? connection.getInputStream() : connection.getErrorStream());

		if (statusCode != 200) {
			throw new IOException("Azure Computer Vision returned HTTP " + statusCode + ": " + body);
		}

		return body;
	}

	public String readText(byte[] imageBytes) throws IOException {
		if (isBlank(endpoint) || isBlank(key)) {
			throw new IOException("Azure Computer Vision endpoint/key not configured.");
		}

		String baseEndpoint = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
		URL url = new URL(baseEndpoint + "/vision/v3.2/read/analyze?language=pt");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setConnectTimeout(timeoutMs);
		connection.setReadTimeout(timeoutMs);
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", key);
		connection.setRequestProperty("Content-Type", "application/octet-stream");

		try (OutputStream outputStream = connection.getOutputStream()) {
			outputStream.write(imageBytes);
		}

		int statusCode = connection.getResponseCode();
		String body = readBody(statusCode >= 200 && statusCode < 300 ? connection.getInputStream() : connection.getErrorStream());

		if (statusCode != 202) {
			throw new IOException("Azure Computer Vision Read returned HTTP " + statusCode + ": " + body);
		}

		String operationLocation = connection.getHeaderField("Operation-Location");
		if (isBlank(operationLocation)) {
			throw new IOException("Azure Computer Vision Read did not return Operation-Location.");
		}

		return pollReadResult(operationLocation);
	}

	private String pollReadResult(String operationLocation) throws IOException {
		for (int attempt = 0; attempt < READ_MAX_ATTEMPTS; attempt++) {
			if (attempt > 0) {
				waitBeforeReadRetry();
			}

			HttpURLConnection connection = (HttpURLConnection) new URL(operationLocation).openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(timeoutMs);
			connection.setReadTimeout(timeoutMs);
			connection.setRequestProperty("Ocp-Apim-Subscription-Key", key);

			int statusCode = connection.getResponseCode();
			String body = readBody(statusCode >= 200 && statusCode < 300 ? connection.getInputStream() : connection.getErrorStream());
			if (statusCode != 200) {
				throw new IOException("Azure Computer Vision Read result returned HTTP " + statusCode + ": " + body);
			}

			JsonObject root = JsonParser.parseString(body).getAsJsonObject();
			String status = getString(root, "status");
			if ("succeeded".equalsIgnoreCase(status)) {
				return extractReadText(root);
			}
			if ("failed".equalsIgnoreCase(status)) {
				throw new IOException("Azure Computer Vision Read failed.");
			}
		}

		throw new IOException("Azure Computer Vision Read timed out.");
	}

	private void waitBeforeReadRetry() throws IOException {
		try {
			Thread.sleep(READ_POLL_INTERVAL_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Azure Computer Vision Read interrupted.", e);
		}
	}

	private String extractReadText(JsonObject root) {
		JsonObject analyzeResult = getObject(root, "analyzeResult");
		JsonElement readResultsElement = analyzeResult.get("readResults");
		if (readResultsElement == null || !readResultsElement.isJsonArray()) {
			return "";
		}

		StringBuilder text = new StringBuilder();
		for (JsonElement pageElement : readResultsElement.getAsJsonArray()) {
			JsonObject page = pageElement.isJsonObject() ? pageElement.getAsJsonObject() : new JsonObject();
			JsonElement linesElement = page.get("lines");
			if (linesElement == null || !linesElement.isJsonArray()) {
				continue;
			}

			JsonArray lines = linesElement.getAsJsonArray();
			for (JsonElement lineElement : lines) {
				JsonObject line = lineElement.isJsonObject() ? lineElement.getAsJsonObject() : new JsonObject();
				String lineText = getString(line, "text");
				if (!isBlank(lineText)) {
					text.append(' ').append(lineText);
				}
			}
		}

		return text.toString().trim();
	}

	static String getEnv(String name, String defaultValue) {
		String value = System.getenv(name);
		if (!isBlank(value)) {
			return value;
		}

		try {
			Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
			value = dotenv.get(name);
			return isBlank(value) ? defaultValue : value;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	static boolean getBooleanEnv(String name, boolean defaultValue) {
		String value = getEnv(name, null);
		return isBlank(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	static double getDoubleEnv(String name, double defaultValue) {
		String value = getEnv(name, null);
		if (isBlank(value)) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	static int getIntEnv(String name, int defaultValue) {
		String value = getEnv(name, null);
		if (isBlank(value)) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private static String readBody(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return "";
		}

		StringBuilder body = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
		}
		return body.toString();
	}

	private static JsonObject getObject(JsonObject root, String memberName) {
		JsonElement element = root.get(memberName);
		return element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
	}

	private static String getString(JsonObject object, String memberName) {
		JsonElement element = object.get(memberName);
		return element != null && !element.isJsonNull() ? element.getAsString() : "";
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
