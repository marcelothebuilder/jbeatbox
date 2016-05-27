package com.github.marcelothebuilder.jbeatbox.client.gui;

import com.github.marcelothebuilder.jbeatbox.BeatBoxNetworkMessage;

public class BeatBoxMessageListItem {
	private BeatBoxNetworkMessage beat;

	public BeatBoxMessageListItem(BeatBoxNetworkMessage beat) {
		assert(beat != null);
		this.beat = beat;
	}

	public String getDisplayName() {
		return beat.getSender() + " : " + beat.getMessage();
	}

	public BeatBoxNetworkMessage getBeat() {
		return beat;
	}

	public String toString() {
		return getDisplayName();
	}

}