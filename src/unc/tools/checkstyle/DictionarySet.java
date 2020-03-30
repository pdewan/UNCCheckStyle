package unc.tools.checkstyle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import unc.cs.tools.checkstyle.dictionary.DictionaryA;
import unc.cs.tools.checkstyle.dictionary.DictionaryB;
import unc.cs.tools.checkstyle.dictionary.DictionaryC;
import unc.cs.tools.checkstyle.dictionary.DictionaryD;
import unc.cs.tools.checkstyle.dictionary.DictionaryE;
import unc.cs.tools.checkstyle.dictionary.DictionaryF;
import unc.cs.tools.checkstyle.dictionary.DictionaryG;
import unc.cs.tools.checkstyle.dictionary.DictionaryH;
import unc.cs.tools.checkstyle.dictionary.DictionaryI;
import unc.cs.tools.checkstyle.dictionary.DictionaryJ;
import unc.cs.tools.checkstyle.dictionary.DictionaryK;
import unc.cs.tools.checkstyle.dictionary.DictionaryL;
import unc.cs.tools.checkstyle.dictionary.DictionaryM;
import unc.cs.tools.checkstyle.dictionary.DictionaryN;
import unc.cs.tools.checkstyle.dictionary.DictionaryO;
import unc.cs.tools.checkstyle.dictionary.DictionaryP;
import unc.cs.tools.checkstyle.dictionary.DictionaryQ;
import unc.cs.tools.checkstyle.dictionary.DictionaryR;
import unc.cs.tools.checkstyle.dictionary.DictionaryS;
import unc.cs.tools.checkstyle.dictionary.DictionarySPlus;
import unc.cs.tools.checkstyle.dictionary.DictionaryT;
import unc.cs.tools.checkstyle.dictionary.DictionaryU;
import unc.cs.tools.checkstyle.dictionary.DictionaryV;
import unc.cs.tools.checkstyle.dictionary.DictionaryW;
import unc.cs.tools.checkstyle.dictionary.DictionaryX;
import unc.cs.tools.checkstyle.dictionary.DictionaryY;
import unc.cs.tools.checkstyle.dictionary.DictionaryZ;

public class DictionarySet {
  static Set<String> dictionarySet;
  public static Set<String> getDictionary() {
	  if (dictionarySet == null) {
		  dictionarySet = new HashSet();
		  dictionarySet.addAll(Arrays.asList(DictionaryA.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryB.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryC.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryD.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryE.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryF.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryG.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryH.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryI.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryJ.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryK.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryL.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryM.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryN.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryO.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryP.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryQ.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryR.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryS.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionarySPlus.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryT.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryU.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryV.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryW.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryX.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryY.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryZ.dictionary));		  
	  }
	  return dictionarySet;
  }
	

}
