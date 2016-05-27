package com.github.marcelothebuilder.jbeatbox.server;

import com.github.marcelothebuilder.jbeatbox.Messages;
import com.github.marcelothebuilder.jbeatbox.server.client.BeatBoxClient;
import com.github.marcelothebuilder.jbeatbox.server.client.BeatBoxClientTask;

class BeatBoxServerTask implements Runnable, BeatBoxServerListener {

	BeatBoxServer server;

	BeatBoxServerTask(BeatBoxServer server) {
		this.server = server;

	}

	@Override
	public void run() {
		server.addServerListener(this);
		while (true) {
			server.iterate();
		}
	}

	@Override
	public void clientConnected(BeatBoxClient client) {
		System.out.println(Messages.getString("BeatBoxServerTask.0")); //$NON-NLS-1$
		BeatBoxClientTask clientTask = new BeatBoxClientTask(client);
		Thread clientThread = new Thread(clientTask);
		clientThread.start();
	}

	@Override
	public void clientDisconnected(BeatBoxClient client) {
		System.out.println(Messages.getString("BeatBoxServerTask.1")); //$NON-NLS-1$
	}
}