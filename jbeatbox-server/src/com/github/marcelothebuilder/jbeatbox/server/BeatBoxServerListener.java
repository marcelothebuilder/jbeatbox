package com.github.marcelothebuilder.jbeatbox.server;

import com.github.marcelothebuilder.jbeatbox.server.client.BeatBoxClient;

interface BeatBoxServerListener {
	void clientConnected(BeatBoxClient client);

	void clientDisconnected(BeatBoxClient client);
}