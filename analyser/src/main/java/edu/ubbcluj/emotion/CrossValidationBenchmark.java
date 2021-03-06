package edu.ubbcluj.emotion;

import java.lang.reflect.Field;
import java.util.List;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.experiment.ExperimentContext;
import org.openimaj.experiment.RunnableExperiment;
import org.openimaj.experiment.annotations.DependentVariable;
import org.openimaj.experiment.annotations.Experiment;
import org.openimaj.experiment.annotations.IndependentVariable;
import org.openimaj.experiment.annotations.Time;
import org.openimaj.experiment.evaluation.classification.ClassificationEvaluator;
import org.openimaj.experiment.evaluation.classification.analysers.confusionmatrix.AggregatedCMResult;
import org.openimaj.experiment.evaluation.classification.analysers.confusionmatrix.CMAggregator;
import org.openimaj.experiment.evaluation.classification.analysers.confusionmatrix.CMAnalyser;
import org.openimaj.experiment.evaluation.classification.analysers.confusionmatrix.CMResult;
import org.openimaj.experiment.validation.ValidationOperation;
import org.openimaj.experiment.validation.ValidationRunner;
import org.openimaj.experiment.validation.cross.CrossValidator;
import org.openimaj.image.FImage;
import org.openimaj.util.parallel.GlobalExecutorPool;

import edu.ubbcluj.emotion.annotator.BatchAnnotatorProvider;
import edu.ubbcluj.emotion.crossvalidation.cm.MatrixCMResult;
import edu.ubbcluj.emotion.engine.EmotionRecogniser;
import edu.ubbcluj.emotion.engine.EmotionRecogniserProvider;
import edu.ubbcluj.emotion.model.Emotion;

@Experiment(author = "Bencze Balazs", dateCreated = "2014-07-12", description = "Emotion recognition cross validation experiment")
public class CrossValidationBenchmark implements RunnableExperiment {

	@IndependentVariable
	private CrossValidator<GroupedDataset<Emotion, ListDataset<FImage>, FImage>>	crossValidator;

	@IndependentVariable
	private EmotionRecogniserProvider<?>											engine;

	@IndependentVariable
	private GroupedDataset<Emotion, ListDataset<FImage>, FImage>					dataset;

	@IndependentVariable
	private BatchAnnotatorProvider<Emotion>											annotatorProvider;

	@DependentVariable
	protected AggregatedCMResult<Emotion>											result;

	@DependentVariable
	protected MatrixCMResult<Emotion>												confusionMatrix;

	public CrossValidationBenchmark(CrossValidator<GroupedDataset<Emotion, ListDataset<FImage>, FImage>> crossValidator,
			GroupedDataset<Emotion, ListDataset<FImage>, FImage> dataset, EmotionRecogniserProvider<?> engine,
			BatchAnnotatorProvider<Emotion> annotatorProvider) {
		this.crossValidator = crossValidator;
		this.dataset = dataset;
		this.engine = engine;
		this.annotatorProvider = annotatorProvider;
	}

	@Override
	public void setup() {
		GlobalExecutorPool.getPool().setCorePoolSize(4);
		GlobalExecutorPool.getPool().setMaximumPoolSize(4);
	}

	@Override
	public void perform() {
		final CMAggregator<Emotion> aggregator = new CMAggregator<Emotion>();

		result = ValidationRunner.run(aggregator, dataset, crossValidator,
				new ValidationOperation<GroupedDataset<Emotion, ListDataset<FImage>, FImage>, CMResult<Emotion>>() {

					@Time(identifier = "Train and Evaluate recogniser")
					@Override
					public CMResult<Emotion> evaluate(GroupedDataset<Emotion, ListDataset<FImage>, FImage> training,
							GroupedDataset<Emotion, ListDataset<FImage>, FImage> validation) {
						final EmotionRecogniser recogniser = engine.create(training, annotatorProvider);

						final ClassificationEvaluator<CMResult<Emotion>, Emotion, FImage> eval = new ClassificationEvaluator<>(recogniser,
								validation, new CMAnalyser<FImage, Emotion>(CMAnalyser.Strategy.SINGLE));

						return eval.analyse(eval.evaluate());
					}

				});
	}

	@Override
	public void finish(ExperimentContext context) {
		doSmth();
	}
	
	private void doSmth() {
		try {
			Class<?> clazz = result.getClass();
			Field field = clazz.getDeclaredField("matrices");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<CMResult<Emotion>> matrices = (List<CMResult<Emotion>>) field.get(result);
			confusionMatrix = new MatrixCMResult<>(matrices);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
