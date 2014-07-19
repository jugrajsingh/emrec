package edu.ubbcluj.emotion;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureExtractor;
import org.openimaj.image.FImage;

public class CombinedFeatureExtractor implements FeatureExtractor<DoubleFV, FImage> {

	private FeatureExtractor<FImage, FImage>	facialFeatureExtractor;
	private FeatureExtractor<DoubleFV, FImage>	featureExtractor;

	public CombinedFeatureExtractor(FeatureExtractor<FImage, FImage> facialFeatureExtractor, FeatureExtractor<DoubleFV, FImage> featureExtractor) {
		this.facialFeatureExtractor = facialFeatureExtractor;
		this.featureExtractor = featureExtractor;
	}

	@Override
	public DoubleFV extractFeature(FImage object) {
		FImage f1 = facialFeatureExtractor.extractFeature(object);
		return featureExtractor.extractFeature(f1);
	}

}