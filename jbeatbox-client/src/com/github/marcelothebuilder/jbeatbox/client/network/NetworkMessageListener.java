package com.github.marcelothebuilder.jbeatbox.client.network;

import com.github.marcelothebuilder.jbeatbox.BeatBoxNetworkMessage;

public interface NetworkMessageListener {
	void messageReceived(BeatBoxNetworkMessage beat);
}