package service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Part;

public class ImageUploadValidator {
	private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));
	private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));
	private static final long BYTES_PER_MB = 1024L * 1024L;

	private final long maxSizeBytes;

	public ImageUploadValidator() {
		this.maxSizeBytes = Math.max(1, AzureComputerVisionClient.getIntEnv("IMAGE_MAX_SIZE_MB", 5)) * BYTES_PER_MB;
	}

	public ImageValidationResult validate(Part part, byte[] bytes) {
		if (part.getSize() <= 0 || bytes.length == 0) {
			return ImageValidationResult.invalid("Arquivo vazio.");
		}

		if (bytes.length > maxSizeBytes) {
			return ImageValidationResult.invalid("Imagem acima do tamanho máximo permitido.");
		}

		String contentType = part.getContentType() == null ? "" : part.getContentType().toLowerCase();
		if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
			return ImageValidationResult.invalid("Tipo MIME inválido.");
		}

		String extension = getExtension(part);
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			return ImageValidationResult.invalid("Extensão de arquivo inválida.");
		}

		if (!matchesMagicBytes(bytes, contentType)) {
			return ImageValidationResult.invalid("Conteúdo do arquivo não corresponde a uma imagem suportada.");
		}

		return ImageValidationResult.valid(extension.equals("jpeg") ? "jpg" : extension);
	}

	private String getExtension(Part part) {
		String submittedFileName = part.getSubmittedFileName();
		if (submittedFileName == null || !submittedFileName.contains(".")) {
			return "";
		}

		return submittedFileName.substring(submittedFileName.lastIndexOf('.') + 1).toLowerCase();
	}

	private boolean matchesMagicBytes(byte[] bytes, String contentType) {
		if ("image/jpeg".equals(contentType)) {
			return bytes.length >= 3
					&& (bytes[0] & 0xFF) == 0xFF
					&& (bytes[1] & 0xFF) == 0xD8
					&& (bytes[2] & 0xFF) == 0xFF;
		}

		if ("image/png".equals(contentType)) {
			return bytes.length >= 8
					&& (bytes[0] & 0xFF) == 0x89
					&& bytes[1] == 0x50
					&& bytes[2] == 0x4E
					&& bytes[3] == 0x47
					&& bytes[4] == 0x0D
					&& bytes[5] == 0x0A
					&& bytes[6] == 0x1A
					&& bytes[7] == 0x0A;
		}

		if ("image/webp".equals(contentType)) {
			return bytes.length >= 12
					&& bytes[0] == 0x52
					&& bytes[1] == 0x49
					&& bytes[2] == 0x46
					&& bytes[3] == 0x46
					&& bytes[8] == 0x57
					&& bytes[9] == 0x45
					&& bytes[10] == 0x42
					&& bytes[11] == 0x50;
		}

		return false;
	}
}
