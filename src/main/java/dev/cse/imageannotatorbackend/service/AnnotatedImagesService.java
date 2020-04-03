package dev.cse.imageannotatorbackend.service;

import dev.cse.imageannotatorbackend.model.AnnotatedImages;
import dev.cse.imageannotatorbackend.model.Annotators;
import dev.cse.imageannotatorbackend.model.resource.AnnotatedImage;
import dev.cse.imageannotatorbackend.repository.AnnotatedImagesRepository;
import dev.cse.imageannotatorbackend.repository.AnnotatorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnotatedImagesService {

	private AnnotatedImagesRepository annotatedImagesRepository;
	private AnnotatorsRepository annotatorsRepository;

	@Autowired
	public AnnotatedImagesService(AnnotatedImagesRepository annotatedImagesRepository, AnnotatorsRepository annotatorsRepository) {
		this.annotatedImagesRepository = annotatedImagesRepository;
		this.annotatorsRepository = annotatorsRepository;
	}

	public void addImages(String username, String[] imageUrls, String[] categories) {
		Optional<Annotators> annotator = annotatorsRepository.findByUsername(username);
		annotator.ifPresent((annot) -> {
			long imagesAnnotated = annot.getNum_images_annotated();
			for (int i = 0; i < imageUrls.length; i++) {
				// Parse name (Image Name) and folderName from image url
				String[] urlSplit = imageUrls[i].split("/");
				int urlSplitLength = urlSplit.length;
				String name = urlSplit[urlSplitLength - 1];
				String folderName = urlSplit[urlSplitLength - 2];

				AnnotatedImages annotImg = new AnnotatedImages(annot, name, folderName, imageUrls[i], categories[i]);
				annotatedImagesRepository.save(annotImg);
				imagesAnnotated++;
			}
			annot.setNum_images_annotated(imagesAnnotated);
			annotatorsRepository.save(annot);
		});
	}

	public List<AnnotatedImage> getImages(String username) {
		List<AnnotatedImages> images =  annotatedImagesRepository.findByAnnotatorUsername(username);
		List<AnnotatedImage> annotatedImages = new ArrayList<>();

		for (AnnotatedImages img : images) {
			annotatedImages.add(new AnnotatedImage(img));
		}

		return annotatedImages;
	}
}