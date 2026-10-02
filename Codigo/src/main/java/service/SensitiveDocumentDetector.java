package service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SensitiveDocumentDetector {
	static final String REASON = "Imagem parece conter documento, cartão ou informação pessoal sensível.";
	static final String CREDIT_CARD = "CREDIT_CARD";
	static final String BANK_CARD = "BANK_CARD";
	static final String PERSONAL_DOCUMENT = "PERSONAL_DOCUMENT";
	static final String FINANCIAL_DOCUMENT = "FINANCIAL_DOCUMENT";

	private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("(?<!\\d)(?:\\d[ -]?){13,19}(?!\\d)");
	private static final Pattern EXPIRY_PATTERN = Pattern.compile("(?<!\\d)(0[1-9]|1[0-2])\\s*/\\s*(\\d{2}|\\d{4})(?!\\d)");
	private static final List<String> STRONG_CARD_TERMS = Arrays.asList(
			"credit card",
			"debit card",
			"bank card",
			"cartao de credito",
			"cartao de debito",
			"cartao bancario",
			"mastercard",
			"visa",
			"elo",
			"amex",
			"american express",
			"valid thru",
			"validade",
			"expires",
			"expiry",
			"cvv",
			"cardholder",
			"titular");
	private static final List<String> WEAK_CARD_TERMS = Arrays.asList(
			"card",
			"cartao",
			"bank",
			"banco");
	private static final List<String> PERSONAL_DOCUMENT_TERMS = Arrays.asList(
			"documento",
			"identidade",
			"rg",
			"cpf",
			"cnh",
			"carteira de identidade",
			"carteira de motorista",
			"passaporte",
			"id card",
			"identity",
			"driver license",
			"passport",
			"national id");
	private static final List<String> FINANCIAL_DOCUMENT_TERMS = Arrays.asList(
			"boleto",
			"fatura",
			"invoice",
			"bill",
			"receipt",
			"statement",
			"comprovante");

	DetectionResult detect(List<String> tags, String description, String ocrText) {
		String normalizedTagsDescription = normalize(join(tags, description));
		String normalizedOcrText = normalize(ocrText);
		String content = normalize(normalizedTagsDescription + " " + normalizedOcrText);

		if (containsAny(content, PERSONAL_DOCUMENT_TERMS)) {
			return DetectionResult.sensitive(PERSONAL_DOCUMENT);
		}

		if (containsAny(content, FINANCIAL_DOCUMENT_TERMS)) {
			return DetectionResult.sensitive(FINANCIAL_DOCUMENT);
		}

		boolean hasStrongCardTerm = containsAny(content, STRONG_CARD_TERMS);
		boolean hasWeakCardTerm = containsAny(content, WEAK_CARD_TERMS);
		boolean hasCardNumber = containsCardNumber(content);
		boolean hasExpiry = EXPIRY_PATTERN.matcher(content).find();

		if (hasStrongCardTerm || (hasCardNumber && hasExpiry) || (hasCardNumber && hasWeakCardTerm)) {
			return DetectionResult.sensitive(hasWeakCardTerm && !hasStrongCardTerm ? BANK_CARD : CREDIT_CARD);
		}

		return DetectionResult.safe();
	}

	private String join(List<String> tags, String description) {
		StringBuilder content = new StringBuilder();
		if (tags != null) {
			for (String tag : tags) {
				content.append(' ').append(tag);
			}
		}
		if (description != null) {
			content.append(' ').append(description);
		}
		return content.toString();
	}

	private boolean containsCardNumber(String content) {
		Matcher matcher = CARD_NUMBER_PATTERN.matcher(content);
		while (matcher.find()) {
			String digits = matcher.group().replaceAll("\\D", "");
			if (digits.length() >= 13 && digits.length() <= 19) {
				return true;
			}
		}
		return false;
	}

	private boolean containsAny(String content, List<String> keywords) {
		for (String keyword : keywords) {
			if (containsKeyword(content, normalize(keyword))) {
				return true;
			}
		}
		return false;
	}

	private boolean containsKeyword(String content, String keyword) {
		if (content.isEmpty() || keyword.isEmpty()) {
			return false;
		}

		return Pattern.compile("(^|[^a-z0-9])" + Pattern.quote(keyword) + "([^a-z0-9]|$)")
				.matcher(content)
				.find();
	}

	private String normalize(String value) {
		String lowerCase = value == null ? "" : value.toLowerCase(Locale.ROOT);
		String normalized = Normalizer.normalize(lowerCase, Normalizer.Form.NFD);
		return normalized.replaceAll("\\p{M}", "").replaceAll("\\s+", " ").trim();
	}

	static class DetectionResult {
		private final boolean sensitive;
		private final String reason;
		private final String detectedType;

		private DetectionResult(boolean sensitive, String reason, String detectedType) {
			this.sensitive = sensitive;
			this.reason = reason;
			this.detectedType = detectedType;
		}

		static DetectionResult sensitive(String detectedType) {
			return new DetectionResult(true, REASON, detectedType);
		}

		static DetectionResult safe() {
			return new DetectionResult(false, "", "");
		}

		boolean isSensitive() {
			return sensitive;
		}

		String getReason() {
			return reason;
		}

		String getDetectedType() {
			return detectedType;
		}
	}
}
