package service;

public class ImageValidationResult {
	private final boolean valid;
	private final String reason;
	private final String extension;

	private ImageValidationResult(boolean valid, String reason, String extension) {
		this.valid = valid;
		this.reason = reason;
		this.extension = extension;
	}

	public static ImageValidationResult valid(String extension) {
		return new ImageValidationResult(true, null, extension);
	}

	public static ImageValidationResult invalid(String reason) {
		return new ImageValidationResult(false, reason, null);
	}

	public boolean isValid() {
		return valid;
	}

	public String getReason() {
		return reason;
	}

	public String getExtension() {
		return extension;
	}
}
