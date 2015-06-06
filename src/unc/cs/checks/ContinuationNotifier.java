package unc.cs.checks;

public interface ContinuationNotifier {
	void addContinuationProcessor(ContinuationProcessor aContinuationProcesor);
	void removeContinuationProcessor(ContinuationProcessor aContinuationProcesor);
	void notifyContinuationProcessors();
}
