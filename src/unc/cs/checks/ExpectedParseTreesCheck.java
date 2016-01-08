package unc.cs.checks;

public class ExpectedParseTreesCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "expectedParseTrees";

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

	

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	public void setExpectedParseTrees(String[] aPatterns) {
		setExpectedTypesAndSpecifications(aPatterns);
	}

}
