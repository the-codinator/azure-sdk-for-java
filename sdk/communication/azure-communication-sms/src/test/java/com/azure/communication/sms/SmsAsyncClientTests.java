// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.communication.sms;

import com.azure.communication.sms.models.SmsSendOptions;
import com.azure.communication.sms.models.SmsSendResult;
import com.azure.core.credential.TokenCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.azure.core.http.HttpClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class SmsAsyncClientTests extends SmsTestBase {
    private SmsAsyncClient asyncClient;

    @Override
    protected void beforeTest() {
        super.beforeTest();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsUsingConnectionString(HttpClient httpClient) {
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsUsingConnectionString");
        assertNotNull(asyncClient);
        StepVerifier.create(asyncClient.send(FROM_PHONE_NUMBER, TO_PHONE_NUMBER, MESSAGE))
            .assertNext(sendResult -> {
                assertHappyPath(sendResult);
            })
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsUsingTokenCredential(HttpClient httpClient) {
        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder().build();
        SmsClientBuilder  builder = getSmsClientWithToken(httpClient, tokenCredential);
        asyncClient = setupAsyncClient(builder, "sendSmsUsingTokenCredential");
        assertNotNull(asyncClient);
        StepVerifier.create(asyncClient.send(FROM_PHONE_NUMBER, TO_PHONE_NUMBER, MESSAGE))
            .assertNext(sendResult -> {
                assertHappyPath(sendResult);
            })
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsToGroup(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsToGroup");

        // Action & Assert
        StepVerifier.create(asyncClient.send(FROM_PHONE_NUMBER, Arrays.asList(TO_PHONE_NUMBER, TO_PHONE_NUMBER), MESSAGE).next())
            .assertNext(result -> {
                assertHappyPath(result);
            })
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsToGroupWithOptions(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsToGroupWithOptions");
        SmsSendOptions options = new SmsSendOptions();
        options.setDeliveryReportEnabled(true);
        options.setTag("New Tag");

        // Action & Assert
        PagedFlux<SmsSendResult> response = asyncClient.send(FROM_PHONE_NUMBER, Arrays.asList(TO_PHONE_NUMBER, TO_PHONE_NUMBER), MESSAGE);
        PagedIterable<SmsSendResult> sendResults = new PagedIterable<>(response);
        for (SmsSendResult result : sendResults) {
            assertHappyPath(result);
        }
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsToSingleNumber(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsToSingleNumber");

        // Action & Assert
        PagedFlux<SmsSendResult> response = asyncClient.send(FROM_PHONE_NUMBER, Arrays.asList(TO_PHONE_NUMBER, TO_PHONE_NUMBER), MESSAGE);
        PagedIterable<SmsSendResult> sendResults = new PagedIterable<>(response);
        for (SmsSendResult result : sendResults) {
            assertHappyPath(result);
        }
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsToSingleNumberWithOptions(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsToSingleNumberWithOptions");
        SmsSendOptions options = new SmsSendOptions();
        options.setDeliveryReportEnabled(true);
        options.setTag("New Tag");

        // Action & Assert
        StepVerifier.create(asyncClient.send(FROM_PHONE_NUMBER, TO_PHONE_NUMBER, MESSAGE, options))
            .assertNext((SmsSendResult sendResult) -> {
                assertHappyPath(sendResult);
            })
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendFromFakeNumber(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendFromFakeNumber");
        // Action & Assert
        Mono<SmsSendResult> response = asyncClient.send("+155512345678", TO_PHONE_NUMBER, MESSAGE);
        StepVerifier.create(response)
            .expectErrorMatches(exception ->
                ((HttpResponseException) exception).getResponse().getStatusCode() == 400).verify();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendFromUnauthorizedNumber(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendFromUnauthorizedNumber");

        // Action & Assert
        Mono<SmsSendResult> response = asyncClient.send("+18007342577", TO_PHONE_NUMBER, MESSAGE);
        StepVerifier.create(response)
            .expectErrorMatches(exception ->
                ((HttpResponseException) exception).getResponse().getStatusCode() == 404).verify();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendToFakePhoneNumber(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendToFakePhoneNumber");
        PagedFlux<SmsSendResult> smsSendResults = asyncClient.send(FROM_PHONE_NUMBER, Arrays.asList("+15550000000"), MESSAGE);

        // Action & Assert
        StepVerifier.create(smsSendResults)
            .assertNext(item -> {
                assertNotNull(item);
            })
            .verifyComplete();

        Iterable<SmsSendResult> smsSendResultList = smsSendResults.collectList().block();
        for (SmsSendResult result : smsSendResultList) {
            assertFalse(result.isSuccessful());
            assertEquals(result.getHttpStatusCode(), 400);
        }
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendTwoMessages(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendTwoMessages");

        // Action & Assert
        StepVerifier.create(asyncClient.send(FROM_PHONE_NUMBER, TO_PHONE_NUMBER, MESSAGE))
            .assertNext(firstResult -> {
                StepVerifier.create(asyncClient.send(FROM_PHONE_NUMBER, TO_PHONE_NUMBER, MESSAGE))
                    .assertNext((SmsSendResult secondResult) -> {
                        assertNotEquals(firstResult.getMessageId(), secondResult.getMessageId());
                        assertHappyPath(firstResult);
                        assertHappyPath(secondResult);
                    });
            })
            .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsToNullNumber(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsToSingleNumber");

        // Action & Assert
        String to = null;
        Mono<SmsSendResult> response = asyncClient.send(FROM_PHONE_NUMBER, to, MESSAGE);
        StepVerifier.create(response).verifyError();
    }

    @ParameterizedTest
    @MethodSource("com.azure.core.test.TestBase#getHttpClients")
    public void sendSmsFromNullNumber(HttpClient httpClient) {
        // Arrange
        SmsClientBuilder builder = getSmsClientUsingConnectionString(httpClient);
        asyncClient = setupAsyncClient(builder, "sendSmsFromNullNumber");

        // Action & Assert
        String from = null;
        Mono<SmsSendResult> response = asyncClient.send(from, TO_PHONE_NUMBER, MESSAGE);
        StepVerifier.create(response).verifyError();
    }


    private SmsAsyncClient setupAsyncClient(SmsClientBuilder builder, String testName) {
        return addLoggingPolicy(builder, testName).buildAsyncClient();
    }

    private void assertHappyPath(SmsSendResult sendResult) {
        assertTrue(sendResult.isSuccessful());
        assertEquals(sendResult.getHttpStatusCode(), 202);
        assertNotNull(sendResult.getMessageId());
    }
}
