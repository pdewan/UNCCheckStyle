package unc.cs.checks;

public class ATypeAndMethod implements TypeAndMethod {
	
	String type;
	String method;
    public ATypeAndMethod(String aSpecificaion) {
    	if (aSpecificaion == null)
    		return;
    	String[] aTypeAndCall = aSpecificaion.split("\\.");
		 type = aTypeAndCall[0];

		 if (aTypeAndCall.length == 2) {
//			 type = aTypeAndCall[0];
			 method = aTypeAndCall[1];
		 } else {
//			 type = null;
//			 method = aTypeAndCall[0];
//			 type = null;
//			 method = aTypeAndCall[0];
			 method = null;
		 }
		
	} 
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getMethod() {
		return method;
	}
	public String toString() {
		return type + "." + method;
	}
	

}
