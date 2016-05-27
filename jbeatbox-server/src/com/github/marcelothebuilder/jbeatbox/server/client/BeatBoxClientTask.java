package com.github.marcelothebuilder.jbeatbox.server.client;

public class BeatBoxClientTask implements Runnable {
	private BeatBoxClient client;

	public BeatBoxClientTask(BeatBoxClient client) {
		this.client = client;
	}

	public void run() {
		while (client.active()) {
			client.iterate();
		}
	}

}