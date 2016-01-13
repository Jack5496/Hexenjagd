package hexenjagd;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class PotentialWitch {

	private static double accusationBound;
	private static double sentenceBound;
	private static int accusationBound2;
	private static double maxWeight;
	private static int maxEdges;
	private static int howMany;
	private int accused; //Number of People accusing this PotentialWitch 
	private int _accused;
	private boolean sentenced;
	private double fearOfWitches; //
	private double fearOfAccusation;
	private double suggestibility;
	private boolean _sentenced;
	private double fearFactor;
	
	public PotentialWitch(double fearOfWitches, double fearOfAccusation, double suggestibility, double fearFactor){
		
		setFearOfWitches(fearOfWitches);
		setFearOfAccusation(fearOfAccusation);
		this.suggestibility = suggestibility;
		sentenced = false;
		accused = 0;
		this.fearFactor = fearFactor;
	}
	

	private void calcSentence(Network<PotentialWitch> network) {
		
		int max = getMaxAccusations(network);
		if(!isSentenced() && getAccused()> accusationBound2 && ((double)getAccused()/(double)max) > sentenceBound){
			_sentenced = true;
		}
	}



	private static int getMaxAccusations(Network<PotentialWitch> network){
		int maxAccusations = 0;
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(!w.isSentenced() && w.getAccused()>maxAccusations){
				maxAccusations = w.getAccused();
			}
		}
		return maxAccusations;
	}
	private static int getAllAccusations(Network<PotentialWitch> network){
		int nb = 0;
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(!w.isSentenced())
				nb+= w.accused();
		}
		return nb;
	}
	
	private int getNewSentences(Network<PotentialWitch> network) {
		int cnt = 0;
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(w.getSentencedNew())
				cnt++;
		}
		return cnt;
		
		
	}
	
	@ScheduledMethod(start = 1.0, interval = 1.0)
	public void step(){
		
		if(!isSentenced()){
			Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
			Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");

			calcSentence(network);
			System.out.println(getAllAccusations(network));
			calcFear(getNewSentences(network), getAllAccusations(network));
			calcAccusation(network); //accusation jeden Schritt zurücksetzen, aber beeinflusst nächste Runde.
			

		}
		
	}

	public void calcAccusation(Network<PotentialWitch> network){
		
		_accused = 0;
		if(network != null){
			for(Object obj: network.getAdjacent(this)){
				PotentialWitch possAcc = (PotentialWitch) obj;
				RepastEdge<PotentialWitch> edge = network.getEdge(possAcc, this);
				
				if(!possAcc.equals(this) && !possAcc.isSentenced()){
					int max = getMaxAccusations(network);
					//System.out.println(max);
					if(max < 1)
						max = 1;
					
					double alreadyAccused = ((accused/accusationBound)*possAcc.getSuggestibility());
					double accusation;
					//if(alreadyAccused>0)
						accusation = (possAcc.getFearOfWitches()*2 + possAcc.getFearOfAccusation() + (1.0-(edge.getWeight()/maxWeight))*2 + alreadyAccused ) /6 ; 
					//else{	
					//	accusation = (possAcc.getFearOfWitches() + possAcc.getFearOfAccusation() + (1.0-(edge.getWeight()/maxWeight))*2 ) /4 ;
					//}
					if (accusation >= accusationBound){
						incrementAccusation();
					}
					
				}
			}
		}
	}
	
	private void calcFear(int sentences, int accusations) {
		
		double sentFactor = (double) sentences /((double) howMany/200)-3;
		//System.out.println(sentFactor);
		setFearOfWitches(getFearOfWitches() - getFearOfWitches()*fearFactor * sentFactor);
		
		double accFactor = (double)accusations/((double)howMany/8)-2;
		
		setFearOfAccusation(getFearOfAccusation() + getFearOfAccusation()*fearFactor*accFactor);
	
	}

	
	public void setFearOfWitches(double fear ){
		if(fear > 1){
			fearOfWitches = 1;
		}
		else if (fear <0){
			fearOfWitches = 0;
		}
		else{
			fearOfWitches = fear;
		}
	}
	
	public void setFearOfAccusation(double fear ){
		if(fear > 1){
			fearOfAccusation = 1;
		}
		else if (fear <0){
			fearOfAccusation = 0;
		}
		else{
			fearOfAccusation = fear;
		}
	}

	@ScheduledMethod(start = 0.5, interval = 1.0)
	public void update(){
		if(!sentenced){
			accused = _accused;
			sentenced = _sentenced;
			_sentenced = false;
			
			
		}
	}

	public void incrementAccusation(){
		_accused++;
	}
	
	public int sentenced(){
		
		return (_sentenced? 1:0);
	}
	
	public int accused(){
		if(!isSentenced() && accused>0){
			return 1;
		}
		return 0;
		
	}

	public int nbAccusations(){
		return accused;
	}
	
	public boolean isSentenced(){
		return sentenced;
	}

	public static double getAccusationBound() {
		return accusationBound;
	}




	public static void setAccusationBound(double accusationBound) {
		PotentialWitch.accusationBound = accusationBound;
	}




	public static double getSentenceBound() {
		return sentenceBound;
	}




	public static void setSentenceBound(double sentenceBound) {
		PotentialWitch.sentenceBound = sentenceBound;
	}


	public static void setAccusationBound2(int accBound2) {
		accusationBound2 = accBound2;
		
	}




	public static void setMaxWeight(double maxWeight) {
		PotentialWitch.maxWeight = maxWeight;
		
	}




	public static void setMaxEdges(int maxEdges) {
		PotentialWitch.maxEdges = maxEdges;
		
	}




	public static void setHowMany(int howMany) {
		PotentialWitch.howMany = howMany;
		
	}
	
	public int getAccused() {
		return accused;
	}

	public void setAccused(int accused) {
		this.accused = accused;
	}
	
	private boolean getSentencedNew(){
		return _sentenced;
	}
	
	
	public double getSuggestibility() {
		// TODO Auto-generated method stub
		return suggestibility;
	}

	public double getFearOfAccusation() {
		// TODO Auto-generated method stub
		return fearOfAccusation;
	}

	public double getFearOfWitches() {
		// TODO Auto-generated method stub
		return fearOfWitches;
	}
}
