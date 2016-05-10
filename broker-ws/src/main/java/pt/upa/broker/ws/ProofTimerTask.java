package pt.upa.broker.ws;

import java.util.TimerTask;

public class ProofTimerTask extends TimerTask {

	BrokerPortType _bp;
	
	public ProofTimerTask(BrokerPortType bp){ _bp = bp; }

	@Override
	public void run() {
		_bp.imAlive();
	}

}