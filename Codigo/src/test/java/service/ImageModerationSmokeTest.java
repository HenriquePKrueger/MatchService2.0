package service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.http.Part;

import model.ImageModerationResult;

public class ImageModerationSmokeTest {
	public static void main(String[] args) throws Exception {
		testValidPngUpload();
		testInvalidMimeUpload();
		testOversizedUpload();
		testAdultResponseIsRejected();
		testSensitiveDocumentResponseIsRejected();
		testCreditCardOcrTextIsRejected();
		testPersonalDocumentOcrTextIsRejected();
		testPlainOcrTextIsApproved();
		testIsolatedCardWordIsApproved();
		testCommonResponseIsApproved();
		testApiFailureIsFailClosed();
		System.out.println("Image moderation smoke tests passed.");
	}

	private static void testValidPngUpload() {
		ImageUploadValidator validator = new ImageUploadValidator();
		byte[] png = validPngBytes();
		ImageValidationResult result = validator.validate(new FakePart("foto.png", "image/png", png), png);
		assertCondition(result.isValid(), "PNG válido deveria ser aceito.");
	}

	private static void testInvalidMimeUpload() {
		ImageUploadValidator validator = new ImageUploadValidator();
		byte[] png = validPngBytes();
		ImageValidationResult result = validator.validate(new FakePart("foto.png", "text/plain", png), png);
		assertCondition(!result.isValid(), "MIME inválido deveria ser rejeitado.");
	}

	private static void testOversizedUpload() {
		ImageUploadValidator validator = new ImageUploadValidator();
		byte[] bytes = new byte[(5 * 1024 * 1024) + 1];
		bytes[0] = (byte) 0x89;
		bytes[1] = 0x50;
		bytes[2] = 0x4E;
		bytes[3] = 0x47;
		bytes[4] = 0x0D;
		bytes[5] = 0x0A;
		bytes[6] = 0x1A;
		bytes[7] = 0x0A;

		ImageValidationResult result = validator.validate(new FakePart("grande.png", "image/png", bytes), bytes);
		assertCondition(!result.isValid(), "Imagem acima do limite deveria ser rejeitada.");
	}

	private static void testAdultResponseIsRejected() throws IOException {
		String json = "{\"adult\":{\"isAdultContent\":true,\"isRacyContent\":false,\"isGoryContent\":false,"
				+ "\"adultScore\":0.91,\"racyScore\":0.10,\"goreScore\":0.05},"
				+ "\"tags\":[{\"name\":\"parede\"}],\"description\":{\"captions\":[{\"text\":\"uma parede\"}]}}";
		ImageModerationService service = new ImageModerationService(new FakeAzureClient(json, null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(!result.isApproved(), "Resposta adult deveria reprovar a imagem.");
		assertCondition(result.isAdultContent(), "Flag adult deveria ser preservada.");
	}

	private static void testSensitiveDocumentResponseIsRejected() throws IOException {
		String json = "{\"adult\":{\"isAdultContent\":false,\"isRacyContent\":false,\"isGoryContent\":false,"
				+ "\"adultScore\":0.01,\"racyScore\":0.01,\"goreScore\":0.01},"
				+ "\"tags\":[{\"name\":\"document\"},{\"name\":\"id card\"},{\"name\":\"identity\"}],"
				+ "\"description\":{\"captions\":[{\"text\":\"a close up of an identity document\"}]}}";
		ImageModerationService service = new ImageModerationService(new FakeAzureClient(json, null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(!result.isApproved(), "Documento pessoal deveria reprovar a imagem.");
		assertCondition(result.getReason().contains("documento"), "Motivo deveria indicar documento sensível.");
	}

	private static void testCreditCardOcrTextIsRejected() throws IOException {
		String json = commonSafeJson();
		ImageModerationService service = new ImageModerationService(
				new FakeAzureClient(json, "5412 3456 7890 1234 09/28 mastercard", null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(!result.isApproved(), "Texto OCR com cartão fictício deveria reprovar a imagem.");
		assertCondition(result.getReason().contains("cartão"), "Motivo deveria indicar cartão sensível.");
	}

	private static void testPersonalDocumentOcrTextIsRejected() throws IOException {
		String json = commonSafeJson();
		ImageModerationService service = new ImageModerationService(
				new FakeAzureClient(json, "RG CPF CNH passaporte identidade", null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(!result.isApproved(), "Texto OCR com documento pessoal deveria reprovar a imagem.");
		assertCondition(result.getReason().contains("documento"), "Motivo deveria indicar documento sensível.");
	}

	private static void testPlainOcrTextIsApproved() throws IOException {
		String json = commonSafeJson();
		ImageModerationService service = new ImageModerationService(
				new FakeAzureClient(json, "parede pia porta ferramenta", null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(result.isApproved(), "Texto comum sem documento/cartão não deveria reprovar a imagem.");
	}

	private static void testIsolatedCardWordIsApproved() throws IOException {
		String json = commonSafeJson();
		ImageModerationService service = new ImageModerationService(
				new FakeAzureClient(json, "cartao de visita em uma mesa", null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(result.isApproved(), "Palavra cartão isolada não deveria reprovar a imagem.");
	}

	private static void testCommonResponseIsApproved() throws IOException {
		String json = commonSafeJson();
		ImageModerationService service = new ImageModerationService(new FakeAzureClient(json, null));
		ImageModerationResult result = service.analyze(new byte[] { 1, 2, 3 });
		assertCondition(result.isApproved(), "Imagem comum não deveria ser bloqueada por documento pessoal.");
	}

	private static void testApiFailureIsFailClosed() {
		ImageModerationService service = new ImageModerationService(new FakeAzureClient(null, new IOException("timeout")));

		try {
			service.analyze(new byte[] { 1, 2, 3 });
			throw new AssertionError("Falha da API deveria propagar IOException para o fluxo fail-closed.");
		} catch (IOException expected) {
			assertCondition(expected.getMessage().contains("timeout"), "Erro original da API deveria ser mantido.");
		}
	}

	private static byte[] validPngBytes() {
		return new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 1, 2, 3 };
	}

	private static String commonSafeJson() {
		return "{\"adult\":{\"isAdultContent\":false,\"isRacyContent\":false,\"isGoryContent\":false,"
				+ "\"adultScore\":0.01,\"racyScore\":0.01,\"goreScore\":0.01},"
				+ "\"tags\":[{\"name\":\"wall\"},{\"name\":\"tool\"},{\"name\":\"door\"}],"
				+ "\"description\":{\"captions\":[{\"text\":\"a wall with a tool near a door\"}]}}";
	}

	private static void assertCondition(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static class FakeAzureClient extends AzureComputerVisionClient {
		private final String response;
		private final String readText;
		private final IOException error;

		private FakeAzureClient(String response, IOException error) {
			this(response, "", error);
		}

		private FakeAzureClient(String response, String readText, IOException error) {
			this.response = response;
			this.readText = readText;
			this.error = error;
		}

		@Override
		public String analyzeImage(byte[] imageBytes) throws IOException {
			if (error != null) {
				throw error;
			}
			return response;
		}

		@Override
		public String readText(byte[] imageBytes) throws IOException {
			if (error != null) {
				throw error;
			}
			return readText;
		}
	}

	private static class FakePart implements Part {
		private final String submittedFileName;
		private final String contentType;
		private final byte[] bytes;

		private FakePart(String submittedFileName, String contentType, byte[] bytes) {
			this.submittedFileName = submittedFileName;
			this.contentType = contentType;
			this.bytes = bytes;
		}

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(bytes);
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public String getName() {
			return "img";
		}

		@Override
		public String getSubmittedFileName() {
			return submittedFileName;
		}

		@Override
		public long getSize() {
			return bytes.length;
		}

		@Override
		public void write(String fileName) {
		}

		@Override
		public void delete() {
		}

		@Override
		public String getHeader(String name) {
			return null;
		}

		@Override
		public Collection<String> getHeaders(String name) {
			return null;
		}

		@Override
		public Collection<String> getHeaderNames() {
			return null;
		}
	}
}
