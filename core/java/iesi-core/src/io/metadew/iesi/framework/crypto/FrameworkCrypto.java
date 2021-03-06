package io.metadew.iesi.framework.crypto;

import java.util.ArrayList;

import io.metadew.iesi.framework.crypto.tools.CryptoTools;
import io.metadew.iesi.framework.crypto.algo.AESGCMEncrypt;
import io.metadew.iesi.framework.execution.FrameworkExecution;

public class FrameworkCrypto {

	private AESGCMEncrypt aesGcmEncrypt;

	public FrameworkCrypto() {
		this.setAesGcmEncrypt(new AESGCMEncrypt(CryptoTools
				.formatKeyString(CryptoTools.generateMD5Hash("c7c1e473-9115-4a6b-abff-6abee67a391e"), 16)));
	}

	// Methods
	public String encrypt(String input) {
		String output = "";
		try {
			output = "ENC(" + this.getAesGcmEncrypt().encrypt(input) + ")";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}

	public String decrypt(String input) {
		String output = "";
		if (input.trim().equals(""))
			return output;

		if (input.substring(0, 4).equals("ENC(")) {
			if (!input.substring(input.length() - 1).equals(")")) {
				throw new RuntimeException("Encrypted password not set correctly");
			}

			try {
				output = this.getAesGcmEncrypt().decrypt(input.substring(4, input.length() - 1));
			} catch (Exception e) {
				throw new RuntimeException("Encrypted password cannot be decrypted on this host", e);
			}
		} else {
			throw new RuntimeException("Encrypted password not set correctly");
		}

		return output;
	}

	public String resolve(FrameworkExecution frameworkExecution, String input) {
		int openPos;
		int closePos;
		String variable_char = "ENC(";
		String variable_char_close = ")";
		String midBit;
		String replaceValue;
		String temp = input;
		while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
			openPos = temp.indexOf(variable_char);
			closePos = temp.indexOf(variable_char_close, openPos + 1);
			midBit = temp.substring(openPos + 4, closePos);

			// Replace
			replaceValue = this.decrypt(variable_char + midBit + variable_char_close);
			if (replaceValue != null) {
				input = input.replace(variable_char + midBit + variable_char_close, replaceValue);
				frameworkExecution.getFrameworkLog().getEncryptionRedactionList().add(replaceValue);
			}
			temp = temp.substring(closePos + 1, temp.length());

		}
		return input;
	}

	public String redact(String input) {
		// Catch null pointer exceptions
		if (input == null)
			input = "";

		// Redact the input value
		int openPos;
		int closePos;
		String variable_char_open = "ENC(";
		String variable_char_close = ")";
		String midBit;
		String replaceValue;
		String temp = input;
		while (temp.indexOf(variable_char_open) > 0 || temp.startsWith(variable_char_open)) {
			openPos = temp.indexOf(variable_char_open);
			closePos = temp.indexOf(variable_char_close, openPos + 1);
			midBit = temp.substring(openPos + variable_char_open.length(), closePos);

			// Replace
			replaceValue = "*******";
			if (replaceValue != null) {
				// Use replace instead of replaceAll to avoid regex replace issues with special
				// characters
				input = input.replace(variable_char_open + midBit + variable_char_close, replaceValue);
			}
			temp = temp.substring(closePos + 1, temp.length());
		}
		return input;
	}

	public String redact(String input, ArrayList<String> redactionList) {
		for (String curVal : redactionList) {
			input = input.replace(curVal, "*******");
		}
		return input;
	}

	// Getters and Setters
	public AESGCMEncrypt getAesGcmEncrypt() {
		return aesGcmEncrypt;
	}

	public void setAesGcmEncrypt(AESGCMEncrypt aesGcmEncrypt) {
		this.aesGcmEncrypt = aesGcmEncrypt;
	}

}