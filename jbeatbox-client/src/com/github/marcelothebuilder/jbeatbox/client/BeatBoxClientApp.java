package com.github.marcelothebuilder.jbeatbox.client;

import com.github.marcelothebuilder.jbeatbox.client.gui.BeatBoxGui;
import com.github.marcelothebuilder.jbeatbox.client.network.NetworkClient;
import com.github.marcelothebuilder.jbeatbox.client.network.NetworkClientTask;

class BeatBoxClientApp {
	public static void main(String[] args) {
		BeatBoxGui gui = new BeatBoxGui();
		gui.showApp();

		NetworkClient net = new NetworkClient(5001);
		net.addMessageListener(gui);

		// net.connect();
		NetworkClientTask task = new NetworkClientTask(net);
		Thread taskThread = new Thread(task);
		taskThread.start();

		gui.addNetClient(net);
	}
}