package pt.upa.transporter.ws.cli;

import pt.upa.transporter.ws.*;

import java.util.ArrayList;

public class TransporterClient {

    private static ArrayList<TransporterPortType> _ports = new ArrayList<TransporterPortType>();

	public TransporterClient(ArrayList<TransporterPortType> ports){
        _ports = ports;
    }

    public ArrayList<JobView> requestJob(String origin, String destination, int price) throws BadPriceFault_Exception {
        ArrayList<JobView> proposals = new ArrayList<JobView>();
        JobView jv = null;

        for (TransporterPortType tp : _ports) {
            try {
                jv = tp.requestJob(origin, destination, price);
            } catch (BadLocationFault_Exception e) {
            } finally {
                if (jv != null)
                    proposals.add(jv);
            }
        }
        return proposals.size() == 0 ? null : proposals;
    }

    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception{
        JobView jv = null;

        for(TransporterPortType tp : _ports){
            try{
                jv = tp.decideJob(id,accept);
            }
            catch(BadJobFault_Exception e){
            }
        }

        if(jv == null)
            throw new BadJobFault_Exception("Não existe tal trabalho", new BadJobFault());
        return jv;
    }


    public void changeState(){

    }



}
