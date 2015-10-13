package org.unicauca.middlewaremobile.middleware.handler;

import org.unicauca.middlewaremobile.middleware.message.MantissMessage;

public interface MantissEventHandler {
	
	void onEvent(String topicUri, MantissMessage message);
	
	void onMove(String topicUri, MantissMessage message);
	void onPress(String topicUri, MantissMessage message);
	void onLongPress(String topicUri, MantissMessage message);
	void onTap(String topicUri, MantissMessage message);
	void onDoubleTap(String topicUri, MantissMessage message);
	void onSwipe(String topicUri, MantissMessage message);
	void onCustom(String topicUri, MantissMessage message);
	void onDrag(String topicUri, MantissMessage message);
	
}
