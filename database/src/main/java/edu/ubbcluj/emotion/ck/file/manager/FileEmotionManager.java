package edu.ubbcluj.emotion.ck.file.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.ubbcluj.emotion.ck.file.info.FileResourceInformationService;
import edu.ubbcluj.emotion.database.file.info.ResourceInformationService;
import edu.ubbcluj.emotion.database.file.manager.EmotionManager;
import edu.ubbcluj.emotion.database.file.manager.ResourceManagerException;
import edu.ubbcluj.emotion.model.Emotion;
import edu.ubbcluj.emotion.util.Constants;
import edu.ubbcluj.emotion.util.StringUtil;

public class FileEmotionManager implements EmotionManager {

	private final ResourceInformationService	resInfo	= new FileResourceInformationService();

	@Override
	public Emotion loadEmotion(final String folder, final String subject, final String sequence) {
		final String baseFolder = StringUtil.buildEmotionFolderName(Constants.ORIGINAL_FOLDER).concat(StringUtil.buildSubjectFolder(subject))
				.concat(StringUtil.buildSequenceFolder(sequence));
		String name = resInfo.getEmotion(folder, subject, sequence);
		if (name == null) {
			// throw new
			// ResourceManagerException("No action unit found for subject " +
			// subject + " sequence " + sequence);
			return null;
		}
		final File f = new File(baseFolder.concat(name));
		Emotion emotion = null;

		try (Scanner scanner = new Scanner(f)) {
			if (scanner.hasNextFloat()) {
				final int em = ((Float) scanner.nextFloat()).intValue();
				emotion = Emotion.fromInteger(em);
			}
		} catch (final FileNotFoundException e) {
			// throw new
			// ResourceManagerException("Error loading emotion for subject " +
			// subject + " sequence " + sequence, e);
		}
		return emotion;
	}

	@Override
	public void saveEmotion(final String folder, final Emotion emotion, final String subject, final String sequence) {
		throw new ResourceManagerException("Not implemented");
	}

}
