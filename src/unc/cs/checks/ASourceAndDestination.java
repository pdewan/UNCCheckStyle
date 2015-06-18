package unc.cs.checks;

public class ASourceAndDestination implements SourceAndDestination {
		
	String source;
	String destination;
	
    public ASourceAndDestination(String aSpecification) {
    	 String[] aSourceAndDestination = aSpecification.split(">");
		 
		 if (aSourceAndDestination.length == 2) {
			 source = aSourceAndDestination[0];
			 destination = aSourceAndDestination[1];
		 } else {
			 source = null;			 
			 destination = aSourceAndDestination[0];
		 }		
	}

	@Override
	public String getSource() {
		return source;
	}
	
	@Override
	public String getDestination() {
		return destination;
	}
	public String toString() {
		return source + ">" + destination;
	}
	
	
    

}
