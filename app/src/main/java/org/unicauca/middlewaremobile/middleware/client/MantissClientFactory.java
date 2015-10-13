package org.unicauca.middlewaremobile.middleware.client;

import android.util.Log;

import org.unicauca.middlewaremobile.middleware.handler.MantissConnectionHandler;
import org.unicauca.middlewaremobile.middleware.handler.MantissControlHandler;
import org.unicauca.middlewaremobile.middleware.handler.MantissEventHandler;


public class MantissClientFactory {
	
	private static final String TAG = MantissClientFactory.class.getSimpleName();
	
	public static MantissClient getInstance(String webSocketEndpoint, MantissConnectionHandler connectionHandler, MantissEventHandler eventHandler, MantissControlHandler controlHandler){
		MantissClient client = null;
		client = new MantissClient(webSocketEndpoint, connectionHandler, eventHandler, controlHandler);
		
		Log.v(TAG, "created MantissClient instance");
		return client;
	}
	
	public static MantissClient getInstance(String webSocketEndpoint, MantissConnectionHandler connectionHandler) {
		MantissClient client = null;
		client = new MantissClient(webSocketEndpoint, connectionHandler, null, null);
		
		Log.v(TAG, "created MantissClient instance");
		return client;
	}
	
	
	public static MantissClient getInstance(String webSocketEndpoint) {
		MantissClient client = null;
		client = new MantissClient(webSocketEndpoint, null, null, null);
		
		Log.v(TAG, "created MantissClient instance");
		return client;
	}	
	
}
