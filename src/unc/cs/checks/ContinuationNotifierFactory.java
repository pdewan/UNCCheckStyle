package unc.cs.checks;

public class ContinuationNotifierFactory {
	static ContinuationNotifier singleton;
	public static ContinuationNotifier getOrCreateSingleton() {
		if (singleton == null) {
			singleton = new AContinuationNotifier();
		}
		return singleton;
	}

}
