package unc.cs.checks;

import java.util.ArrayList;
import java.util.List;

public class AContinuationNotifier implements ContinuationNotifier {
	List<ContinuationProcessor> continuationProcessors = new ArrayList();

	@Override
	public void addContinuationProcessor(
			ContinuationProcessor aContinuationProcesor) {
		if (continuationProcessors.contains(aContinuationProcesor))
			return;
		continuationProcessors.add(aContinuationProcesor);
		
	}

	@Override
	public void removeContinuationProcessor(
			ContinuationProcessor aContinuationProcesor) {
		continuationProcessors.remove(aContinuationProcesor);
		
	}

	@Override
	public void notifyContinuationProcessors() {
		for (ContinuationProcessor aContinuationProcessor:continuationProcessors) {
			aContinuationProcessor.processDeferredChecks();
		}
		
	}
	
}
