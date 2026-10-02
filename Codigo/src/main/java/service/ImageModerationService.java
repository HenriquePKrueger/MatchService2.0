package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.ImageModerationResult;

public class ImageModerationService {
	private static final double DEFAULT_THRESHOLD = 0.70;
	private final AzureComputerVisionClient azureClient;
	private final SensitiveDocumentDetector sensitiveDocumentDetector;
	private final boolean enabled;
	private final double adultThreshold;
	private final double racyThreshold;
	private final double goreThreshold;

	public ImageModerationService() {
		this(new AzureComputerVisionClient());
	}

	public ImageModerationService(AzureComputerVisionClient azureClient) {
		this.azureClient = azureClient;
		this.sensitiveDocumentDetector = new SensitiveDocumentDetector();
		this.enabled = AzureComputerVisionClient.getBooleanEnv("IMAGE_MODERATION_ENABLED", true);
		this.adultThreshold = AzureComputerVisionClient.getDoubleEnv("IMAGE_MODERATION_ADULT_THRESHOLD", DEFAULT_THRESHOLD);
		this.racyThreshold = AzureComputerVisionClient.getDoubleEnv("IMAGE_MODERATION_RACY_THRESHOLD", DEFAULT_THRESHOLD);
		this.goreThreshold = AzureComputerVisionClient.getDoubleEnv("IMAGE_MODERATION_GORE_THRESHOLD", DEFAULT_THRESHOLD);
	}

	public ImageModerationResult analyze(byte[] imageBytes) throws IOException {
		ImageModerationResult result = new ImageModerationResult();

		if (!enabled) {
			result.setApproved(true);
			result.setReason("Moderação de imagem desativada.");
			return result;
		}

		String rawResponse = azureClient.analyzeImage(imageBytes);
		result.setRawResponse(rawResponse);

		try {
			JsonObject root = JsonParser.parseString(rawResponse).getAsJsonObject();
			JsonObject adult = getObject(root, "adult");

			result.setAdultContent(getBoolean(adult, "isAdultContent"));
			result.setRacyContent(getBoolean(adult, "isRacyContent"));
			result.setGoryContent(getBoolean(adult, "isGoryContent"));
			result.setAdultScore(getDouble(adult, "adultScore"));
			result.setRacyScore(getDouble(adult, "racyScore"));
			result.setGoreScore(getDouble(adult, "goreScore"));
			result.setTags(readTags(root));
			result.setDescription(readDescription(root));

			String ocrText = azureClient.readText(imageBytes);
			SensitiveDocumentDetector.DetectionResult sensitiveDocument = sensitiveDocumentDetector.detect(
					result.getTags(),
					result.getDescription(),
					ocrText);
			if (sensitiveDocument.isSensitive()) {
				result.setApproved(false);
				result.setReason(sensitiveDocument.getReason());
				return result;
			}

			boolean rejected = result.isAdultContent()
					|| result.isRacyContent()
					|| result.isGoryContent()
					|| result.getAdultScore() >= adultThreshold
					|| result.getRacyScore() >= racyThreshold
					|| result.getGoreScore() >= goreThreshold;

			result.setApproved(!rejected);
			result.setReason(rejected ? buildRejectionReason(result) : "Imagem aprovada pela moderação.");
			return result;
		} catch (RuntimeException e) {
			throw new IOException("Invalid Azure Computer Vision JSON response.", e);
		}
	}

	private String buildRejectionReason(ImageModerationResult result) {
		List<String> reasons = new ArrayList<>();

		if (result.isAdultContent() || result.getAdultScore() >= adultThreshold) {
			reasons.add("conteúdo adulto");
		}
		if (result.isRacyContent() || result.getRacyScore() >= racyThreshold) {
			reasons.add("conteúdo sugestivo");
		}
		if (result.isGoryContent() || result.getGoreScore() >= goreThreshold) {
			reasons.add("conteúdo violento");
		}

		return "Imagem recusada por " + String.join(", ", reasons) + ".";
	}

	private JsonObject getObject(JsonObject root, String memberName) {
		JsonElement element = root.get(memberName);
		return element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
	}

	private boolean getBoolean(JsonObject object, String memberName) {
		JsonElement element = object.get(memberName);
		return element != null && !element.isJsonNull() && element.getAsBoolean();
	}

	private double getDouble(JsonObject object, String memberName) {
		JsonElement element = object.get(memberName);
		return element != null && !element.isJsonNull() ? element.getAsDouble() : 0.0;
	}

	private List<String> readTags(JsonObject root) {
		List<String> tags = new ArrayList<>();
		JsonElement tagsElement = root.get("tags");

		if (tagsElement != null && tagsElement.isJsonArray()) {
			JsonArray tagsArray = tagsElement.getAsJsonArray();
			for (JsonElement tagElement : tagsArray) {
				if (tagElement.isJsonObject()) {
					JsonElement name = tagElement.getAsJsonObject().get("name");
					if (name != null && !name.isJsonNull()) {
						tags.add(name.getAsString());
					}
				}
			}
		}

		return tags;
	}

	private String readDescription(JsonObject root) {
		JsonObject description = getObject(root, "description");
		JsonElement captionsElement = description.get("captions");

		if (captionsElement != null && captionsElement.isJsonArray() && captionsElement.getAsJsonArray().size() > 0) {
			JsonObject firstCaption = captionsElement.getAsJsonArray().get(0).getAsJsonObject();
			JsonElement text = firstCaption.get("text");
			return text != null && !text.isJsonNull() ? text.getAsString() : "";
		}

		return "";
	}
}
