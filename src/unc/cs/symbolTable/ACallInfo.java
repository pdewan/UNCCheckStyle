package unc.cs.symbolTable;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class ACallInfo implements CallInfo {
	String caller;
	String calledType;
	String calleee;
	
	List<DetailAST> actuals;
	
	String[] normalizedCall;
	
	
//	public ACallInfo(String caller, String calledType,
//			String calleee) {
//		super();
//		this.caller = caller;
//		this.calledType = calledType;
//		this.calleee = calleee;
//	}
	public ACallInfo(String caller, String calledType, String calleee,
			List<DetailAST> actuals, String[] notmalizedCall) {
		super();
		this.caller = caller;
		this.calledType = calledType;
		this.calleee = calleee;
		this.actuals = actuals;
		this.normalizedCall = notmalizedCall;
	}
	@Override
	public String getCaller() {
		return caller;
	}
	@Override
	public String getCalledType() {
		return calledType;
	}
	@Override
	public String getCalleee() {
		return calleee;
	}
	@Override
	public List<DetailAST> getActuals() {
		return actuals;
	}
	@Override
	public String[] getNormalizedCall() {
		return normalizedCall;
	}
	public String toString() {
		return caller + "-->" + calledType + "." + calleee;
	}

}
