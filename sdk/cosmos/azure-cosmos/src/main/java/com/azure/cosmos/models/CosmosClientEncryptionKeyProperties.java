// Copyright (c) Microsoft Corporation. All rights reserved.
//// Licensed under the MIT License.

package com.azure.cosmos.models;

import com.azure.cosmos.implementation.ClientEncryptionKey;
import com.azure.cosmos.implementation.Resource;
import com.azure.cosmos.util.Beta;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Details of an encryption key for use with the Azure Cosmos DB service.
 */
@Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
public final class CosmosClientEncryptionKeyProperties {
    private ClientEncryptionKey clientEncryptionKey;

    /**
     * Initialize a ClientEncryptionKey object from json string.
     *
     * @param jsonString the json string that represents the database clientEncryptionKey.
     */
    CosmosClientEncryptionKeyProperties(String jsonString) {
        this.clientEncryptionKey = new ClientEncryptionKey(jsonString);
    }

    CosmosClientEncryptionKeyProperties(ClientEncryptionKey clientEncryptionKey) {
        this.clientEncryptionKey = clientEncryptionKey;
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public CosmosClientEncryptionKeyProperties(String id,
                                        String encryptionAlgorithm,
                                        byte[] wrappedDataEncryptionKey,
                                        EncryptionKeyWrapMetadata encryptionKeyWrapMetadata) {
        this.clientEncryptionKey = new ClientEncryptionKey();
        this.clientEncryptionKey.setId(id);
        this.clientEncryptionKey.setEncryptionAlgorithm(encryptionAlgorithm);
        this.clientEncryptionKey.setWrappedDataEncryptionKey(wrappedDataEncryptionKey);
        this.clientEncryptionKey.setEncryptionKeyWrapMetadata(encryptionKeyWrapMetadata);
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public String getEncryptionAlgorithm() {
        return this.clientEncryptionKey.getEncryptionAlgorithm();
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public CosmosClientEncryptionKeyProperties setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.clientEncryptionKey.setEncryptionAlgorithm(encryptionAlgorithm);
        return this;
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public byte[] getWrappedDataEncryptionKey() {
        return this.clientEncryptionKey.getWrappedDataEncryptionKey();
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public CosmosClientEncryptionKeyProperties setWrappedDataEncryptionKey(byte[] wrappedDataEncryptionKey) {
        this.clientEncryptionKey.setWrappedDataEncryptionKey(wrappedDataEncryptionKey);
        return this;
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public EncryptionKeyWrapMetadata getEncryptionKeyWrapMetadata() {
        return this.clientEncryptionKey.getEncryptionKeyWrapMetadata();
    }

    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public CosmosClientEncryptionKeyProperties setEncryptionKeyWrapMetadata(EncryptionKeyWrapMetadata encryptionKeyWrapMetadata) {
        this.clientEncryptionKey.setEncryptionKeyWrapMetadata(encryptionKeyWrapMetadata);
        return this;
    }

    /**
     * Gets the name of the resource.
     *
     * @return the name of the resource.
     */
    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public String getId() {
        return this.clientEncryptionKey.getId();
    }

    /**
     * Sets the name of the resource.
     *
     * @param id the name of the resource.
     * @return the current instance of {@link CosmosContainerProperties}.
     */
    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public CosmosClientEncryptionKeyProperties setId(String id) {
        this.clientEncryptionKey.setId(id);
        return this;
    }

    /**
     * Gets the ID associated with the resource.
     *
     * @return the ID associated with the resource.
     */
    String getResourceId() {
        return this.clientEncryptionKey.getResourceId();
    }

    /**
     * Get the last modified timestamp associated with the resource.
     * This is only relevant when getting response from the server.
     *
     * @return the timestamp.
     */
    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public Instant getTimestamp() {
        return this.clientEncryptionKey.getTimestamp();
    }

    /**
     * Get the entity tag associated with the resource.
     * This is only relevant when getting response from the server.
     *
     * @return the e tag.
     */
    @Beta(value = Beta.SinceVersion.V4_13_0, warningText = Beta.PREVIEW_SUBJECT_TO_CHANGE_WARNING)
    public String getETag() {
        return this.clientEncryptionKey.getETag();
    }

    Resource getResource() {
        return this.clientEncryptionKey;
    }

    ClientEncryptionKey getClientEncryptionKey() {
        return new ClientEncryptionKey(this.clientEncryptionKey.toJson());
    }

    static List<CosmosClientEncryptionKeyProperties> getClientEncryptionKeys(List<ClientEncryptionKey> results) {
        return results.stream().map(CosmosClientEncryptionKeyProperties::new).collect(Collectors.toList());
    }

}
