package io.ebean.enhance.common;

/**
 * Filters classes for entity and transactional enhancement.
 */
class FilterEntityTransactional {

	private final boolean noFiltering;

	private final String[] topLevelPackages;

	FilterEntityTransactional(AgentManifest manifest) {

		// if no packages for either then run detection on everything
		noFiltering = manifest.getTransactionalPackages().isEmpty() || manifest.getEntityPackages().isEmpty();

		DistillPackages distill = new DistillPackages().add(manifest.getEntityPackages());
		if (!manifest.isTransactionalNone()) {
			distill.add(manifest.getTransactionalPackages());
		}
		this.topLevelPackages = distill.distill();
	}

	/**
	 * Return true if enhancement/detection should be run on this class.
	 */
	boolean detectEnhancement(String className) {

		if (noFiltering) {
			return true;
		}
		for (String pkg :topLevelPackages) {
			if (className.startsWith(pkg)) {
				return true;
			}
		}
		return false;
	}
}
