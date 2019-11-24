package unc.cs.checks;

public class NameComponentMetrics {
	public int numChars;
	public int numLetters;
	public int numVowels;
	public int numDigits;

public static boolean isVowel(char c) {
		  return "AEIOUaeiou".indexOf(c) != -1;
}
public static NameComponentMetrics computeComponentMetrics(String aName) {
	NameComponentMetrics retVal =  new NameComponentMetrics();
	for (int i = 0; i < aName.length(); i++) {
		char ch = aName.charAt(i); 
		if (Character.isDigit(ch)) {
			retVal.numDigits++;			
		}
		if (Character.isLetter(ch)) {
			retVal.numLetters++;
			if (isVowel(ch)) {
				retVal.numVowels++;
			}
		}
	}
	retVal.numChars = aName.length();
	return retVal;
}
}