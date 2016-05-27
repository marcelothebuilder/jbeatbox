package com.github.marcelothebuilder.jbeatbox.client.network;

public class NetworkClientTask implements Runnable {
	private NetworkClient net;

	public NetworkClientTask(NetworkClient net) {
		this.net = net;
	}

	public void run() {
		net.connect();
		while (true)
			net.iterate();
	}
}