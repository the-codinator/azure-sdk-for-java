// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.implementation.converters;

import com.azure.search.documents.indexes.models.ImageAnalysisSkill;

/**
 * A converter between {@link com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill} and
 * {@link ImageAnalysisSkill}.
 */
public final class ImageAnalysisSkillConverter {
    /**
     * Maps from {@link com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill} to
     * {@link ImageAnalysisSkill}.
     */
    public static ImageAnalysisSkill map(com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill obj) {
        if (obj == null) {
            return null;
        }

        ImageAnalysisSkill imageAnalysisSkill = new ImageAnalysisSkill(obj.getInputs(), obj.getOutputs());

        String name = obj.getName();
        imageAnalysisSkill.setName(name);

        String context = obj.getContext();
        imageAnalysisSkill.setContext(context);

        String description = obj.getDescription();
        imageAnalysisSkill.setDescription(description);

        if (obj.getVisualFeatures() != null) {
            imageAnalysisSkill.setVisualFeatures(obj.getVisualFeatures());
        }

        if (obj.getDefaultLanguageCode() != null) {
            imageAnalysisSkill.setDefaultLanguageCode(obj.getDefaultLanguageCode());
        }

        if (obj.getDetails() != null) {
            imageAnalysisSkill.setDetails(obj.getDetails());
        }
        return imageAnalysisSkill;
    }

    /**
     * Maps from {@link ImageAnalysisSkill} to
     * {@link com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill}.
     */
    public static com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill map(ImageAnalysisSkill obj) {
        if (obj == null) {
            return null;
        }

        com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill imageAnalysisSkill =
            new com.azure.search.documents.indexes.implementation.models.ImageAnalysisSkill(obj.getInputs(),
                obj.getOutputs());

        String name = obj.getName();
        imageAnalysisSkill.setName(name);

        String context = obj.getContext();
        imageAnalysisSkill.setContext(context);

        String description = obj.getDescription();
        imageAnalysisSkill.setDescription(description);

        if (obj.getVisualFeatures() != null) {
            imageAnalysisSkill.setVisualFeatures(obj.getVisualFeatures());
        }

        if (obj.getDefaultLanguageCode() != null) {
            imageAnalysisSkill.setDefaultLanguageCode(obj.getDefaultLanguageCode());
        }

        if (obj.getDetails() != null) {
            imageAnalysisSkill.setDetails(obj.getDetails());
        }

        return imageAnalysisSkill;
    }

    private ImageAnalysisSkillConverter() {
    }
}
