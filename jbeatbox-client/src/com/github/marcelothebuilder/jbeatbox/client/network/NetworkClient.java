package com.github.marcelothebuilder.jbeatbox.client.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.github.marcelothebuilder.jbeatbox.BeatBoxNetworkMessage;

public class NetworkClient {
	private Socket serverSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private List<NetworkMessageListener> messageListeners = new ArrayList<NetworkMessageListener>();

	public NetworkClient(int serverPort) {
	}

	public void connect() {
		try {
			serverSocket = new Socket("127.0.0.1", 5001); //$NON-NLS-1$
			out = new ObjectOutputStream(serverSocket.getOutputStream());
			in = new ObjectInputStream(serverSocket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected ObjectInputStream in() {
		return in;
	}

	protected ObjectOutputStream out() {
		return out;
	}

	protected Object readNext() {
		try {
			Object obj = in().readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			// invalid class? socket closed? please come here later.
			return null;
		}
	}

	public BeatBoxNetworkMessage receiveNetworkMessage() {
		BeatBoxNetworkMessage beat = (BeatBoxNetworkMessage) readNext();
		return beat;
	}

	public void sendNetworkMessage(BeatBoxNetworkMessage msg) {
		assert(msg != null);
		try {
			out.writeObject(msg);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void addMessageListener(NetworkMessageListener listener) {
		messageListeners.add(listener);
	}

	public void tellListeners(BeatBoxNetworkMessage beat) {
		for (NetworkMessageListener listener : messageListeners) {
			assert(beat != null);
			listener.messageReceived(beat);
		}
	}

	public void iterate() {
		BeatBoxNetworkMessage beat = receiveNetworkMessage();
		assert(beat != null);
		tellListeners(beat);
	}
}