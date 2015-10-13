package org.unicauca.middlewaremobile.middleware.handler;

public interface MantissConnectionHandler {
	
	void onClose(int code, String reason);
	void onOpen();
}
