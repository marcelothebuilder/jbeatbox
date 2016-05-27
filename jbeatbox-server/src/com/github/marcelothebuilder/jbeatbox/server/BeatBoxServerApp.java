package com.github.marcelothebuilder.jbeatbox.server;

class BeatBoxServerApp {

	public static void main(String[] args) {
		new BeatBoxServerApp();
	}

	BeatBoxServerApp() {
		BeatBoxServer server = new BeatBoxServer(5001);

		BeatBoxServerTask serverTask = new BeatBoxServerTask(server);
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

}