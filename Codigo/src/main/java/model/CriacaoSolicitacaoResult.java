package model;

import java.util.ArrayList;
import java.util.List;

public class CriacaoSolicitacaoResult {
	private boolean success;
	private String message;
	private List<String> warnings = new ArrayList<>();

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public void addWarning(String warning) {
		if (warning != null && !warning.trim().isEmpty() && !warnings.contains(warning)) {
			warnings.add(warning);
		}
	}
}
