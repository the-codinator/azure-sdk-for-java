// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.containers.containerregistry.implementation.authentication;

import com.azure.containers.containerregistry.implementation.models.PostContentSchemaGrantType;
import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpPipeline;
import com.azure.core.util.serializer.JacksonAdapter;
import com.azure.core.util.serializer.SerializerAdapter;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

/**
 * Token service implementation that wraps the authentication rest APIs for ACR.
 */
public class TokenServiceImpl {

    private final AccessTokensImpl accessTokensImpl;
    private final RefreshTokensImpl refreshTokenImpl;

    /**
     * Creates an instance of the token service impl class.TokenServiceImpl.java
     *  @param url the service endpoint.
     * @param pipeline the pipeline to use to make the call.
     * @param serializerAdapter the serializer adapter for the rest client.
     *
     */
    public TokenServiceImpl(String url, HttpPipeline pipeline, SerializerAdapter serializerAdapter) {
        if (serializerAdapter == null) {
            serializerAdapter = JacksonAdapter.createDefaultSerializerAdapter();
        }

        this.accessTokensImpl = new AccessTokensImpl(url, pipeline, serializerAdapter);
        this.refreshTokenImpl = new RefreshTokensImpl(url, pipeline, serializerAdapter);
    }

    /**
     * Gets the ACR access token.
     * @param acrRefreshToken Given the ACRs refresh token.
     * @param scope - Token scope.
     * @param serviceName The name of the service.
     *
     */
    public Mono<AccessToken> getAcrAccessTokenAsync(String acrRefreshToken, String scope, String serviceName) {
        return this.accessTokensImpl.getAccessTokenAsync(PostContentSchemaGrantType.REFRESH_TOKEN.toString(), serviceName, scope, acrRefreshToken)
            .map(token -> {
                String accessToken = token.getAccessToken();
                OffsetDateTime expirationTime = JsonWebToken.retrieveExpiration(accessToken);
                return new AccessToken(accessToken, expirationTime);
            });
    }

    /**
     * Gets an ACR refresh token.
     * @param aadAccessToken Given the ACR access token.
     * @param serviceName Given the ACR service.
     *
     */
    public Mono<AccessToken> getAcrRefreshTokenAsync(String aadAccessToken, String serviceName) {
        return this.refreshTokenImpl.getRefreshTokenAsync(
            PostContentSchemaGrantType.ACCESS_TOKEN.toString(),
            aadAccessToken,
            null,
            serviceName).map(token -> {
                String refreshToken = token.getRefreshToken();
                OffsetDateTime expirationTime = JsonWebToken.retrieveExpiration(refreshToken);
                return new AccessToken(refreshToken, expirationTime);
            });
    }
}
