package com.github.marcelothebuilder.jbeatbox.client;

public class BeatBoxTrackBeat {
	private int _key;
	private int _beat;

	public BeatBoxTrackBeat(int key, int beat) {
		_key = key;
		_beat = beat;
	}

	public int getKey() {
		return _key;
	}

	public int getBeat() {
		return _beat;
	}
}