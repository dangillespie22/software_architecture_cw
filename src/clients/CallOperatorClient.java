package clients;

import clients.call_operator.CallManager;

public class CallOperatorClient {
	
	public static void main(String[] args) {
		CallManager callManager = new CallManager();
		while (true) {
			callManager.startNewCall();
		}
	}
}
