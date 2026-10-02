package model;

import java.util.ArrayList;
import java.util.List;

public class ImageModerationResult {
	private boolean approved;
	private boolean adultContent;
	private boolean racyContent;
	private boolean goryContent;
	private double adultScore;
	private double racyScore;
	private double goreScore;
	private String reason;
	private String rawResponse;
	private List<String> tags = new ArrayList<>();
	private String description;

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isAdultContent() {
		return adultContent;
	}

	public void setAdultContent(boolean adultContent) {
		this.adultContent = adultContent;
	}

	public boolean isRacyContent() {
		return racyContent;
	}

	public void setRacyContent(boolean racyContent) {
		this.racyContent = racyContent;
	}

	public boolean isGoryContent() {
		return goryContent;
	}

	public void setGoryContent(boolean goryContent) {
		this.goryContent = goryContent;
	}

	public double getAdultScore() {
		return adultScore;
	}

	public void setAdultScore(double adultScore) {
		this.adultScore = adultScore;
	}

	public double getRacyScore() {
		return racyScore;
	}

	public void setRacyScore(double racyScore) {
		this.racyScore = racyScore;
	}

	public double getGoreScore() {
		return goreScore;
	}

	public void setGoreScore(double goreScore) {
		this.goreScore = goreScore;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
