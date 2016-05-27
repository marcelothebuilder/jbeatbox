package com.github.marcelothebuilder.jbeatbox.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.github.marcelothebuilder.jbeatbox.Messages;
import com.github.marcelothebuilder.jbeatbox.server.client.BeatBoxClient;
import com.github.marcelothebuilder.jbeatbox.server.client.BeatBoxMessageListener;

class BeatBoxServer implements BeatBoxMessageListener {
	protected ServerSocket serverSocket;
	private int serverPort;
	private List<BeatBoxClient> clients = new ArrayList<BeatBoxClient>();
	private List<BeatBoxServerListener> serverListeners = new ArrayList<BeatBoxServerListener>();

	BeatBoxServer(int serverPort) {
		this.serverPort = serverPort;
		startServer();
	}

	public void addServerListener(BeatBoxServerListener listener) {
		serverListeners.add(listener);
	}

	public void removeServerListener(BeatBoxServerListener listener) {
		serverListeners.remove(listener);
	}

	protected void tellClientConnected(BeatBoxClient client) {
		for (BeatBoxServerListener listener : serverListeners) {
			listener.clientConnected(client);
		}
	}

	protected void tellClientDisconnected(BeatBoxClient client) {
		for (BeatBoxServerListener listener : serverListeners) {
			listener.clientDisconnected(client);
		}
	}

	protected void startServer() {
		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public BeatBoxClient nextClient() {
		System.out.println(Messages.getString("BeatBoxServer.0")); //$NON-NLS-1$
		BeatBoxClient client = null;
		try {
			Socket clientSocket = serverSocket.accept();
			client = new BeatBoxClient(clientSocket);
			tellClientConnected(client);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return client;
	}

	public void iterate() {
		BeatBoxClient client = nextClient();
		client.addMessageListener(this);
		clients.add(client);
	}

	// we must listen for clients messages
	@Override
	public void messageReceived(Object beat) {
		assert (beat != null);
		// some client sent a message, broadcast it!
		System.out.println(Messages.getString("BeatBoxServer.1")); //$NON-NLS-1$
		for (BeatBoxClient client : clients) {
			client.sendBeat(beat);
		}
	}

}