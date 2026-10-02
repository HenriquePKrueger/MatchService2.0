package service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageContextValidator {
	private static final Set<String> SERVICE_CONTEXT_WORDS = new HashSet<>(Arrays.asList(
			"servico", "manutencao", "casa", "limpeza", "construcao", "reparo", "tecnologia",
			"ferramenta", "eletrica", "encanamento", "pintura", "jardim", "instalacao",
			"cozinha", "banheiro", "computador", "ar condicionado", "porta", "janela"));

	public boolean looksServiceRelated(List<String> tags, String description) {
		String haystack = String.join(" ", tags == null ? Arrays.asList() : tags) + " " + (description == null ? "" : description);
		String normalized = normalize(haystack);

		for (String word : SERVICE_CONTEXT_WORDS) {
			if (normalized.contains(normalize(word))) {
				return true;
			}
		}

		return false;
	}

	private String normalize(String value) {
		return value == null ? "" : value.toLowerCase().trim();
	}
}
