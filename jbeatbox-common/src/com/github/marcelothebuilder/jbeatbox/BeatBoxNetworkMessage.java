package com.github.marcelothebuilder.jbeatbox;

import java.io.Serializable;

public class BeatBoxNetworkMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sender;
	private String message;
	private Boolean[] pattern;

	public BeatBoxNetworkMessage(String sender, String message, Boolean[] pattern) {
		assert(sender != null);
		assert(message != null);
		assert(pattern != null);
		this.sender = sender;
		this.message = message;
		this.pattern = pattern;
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public Boolean[] getPattern() {
		return pattern;
	}
}