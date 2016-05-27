package com.github.marcelothebuilder.jbeatbox.server.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.github.marcelothebuilder.jbeatbox.Messages;

public class BeatBoxClient {
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private List<BeatBoxMessageListener> messageListeners = new ArrayList<BeatBoxMessageListener>();
	private boolean active = true;

	public BeatBoxClient(Socket clientSocket) {
		this.socket = clientSocket;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println(Messages.getString("BeatBoxClient.0")); //$NON-NLS-1$
			e.printStackTrace();
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
			return in().readObject();
		} catch (SocketException e) {
			System.out.println(Messages.getString("BeatBoxClient.1")); //$NON-NLS-1$
			this.active = false;
			return null;
		} catch (Exception e) {
			System.out.println(Messages.getString("BeatBoxClient.2")); //$NON-NLS-1$
			e.printStackTrace();

			// invalid class? socket closed? please come here later.
			return null;
		}
	}

	protected Socket getSocket() {
		return socket;
	}

	public Object nextBeat() {
		System.out.println(Messages.getString("BeatBoxClient.3")); //$NON-NLS-1$
		Object beat = readNext();
		return beat;
	}

	public void addMessageListener(BeatBoxMessageListener listener) {
		messageListeners.add(listener);
	}

	public void tellListeners(Object beat) {
		for (BeatBoxMessageListener listener : messageListeners) {
			listener.messageReceived(beat);
		}
	}
	
	public void removeMessageListener(BeatBoxMessageListener listener) {
		messageListeners.remove(listener);
	}

	public void sendBeat(Object beat) {
		try {
			System.out.println(Messages.getString("BeatBoxClient.4") + beat); //$NON-NLS-1$
			out().writeObject(beat);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void iterate() {
		System.out.println(Messages.getString("BeatBoxClient.5")); //$NON-NLS-1$
		Object beat = nextBeat();
		if (beat != null) {
			System.out.println(Messages.getString("BeatBoxClient.6") + beat); //$NON-NLS-1$
			tellListeners(beat);
		}
		
	}

	public boolean active() {
		return this.active;
	}
}