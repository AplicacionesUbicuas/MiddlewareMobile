package org.unicauca.middlewaremobile.middleware.handler;

import org.unicauca.middlewaremobile.middleware.message.MantissMessage;

public interface MantissControlHandler {
	void onSetup(String topicUri, MantissMessage message);
	void onPing(String topicUri, MantissMessage message);
	void onBye(String topicUri, MantissMessage message);
}
