// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation.handler;

import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.amqp.messaging.Modified;
import org.apache.qpid.proton.engine.Delivery;
import org.apache.qpid.proton.engine.EndpointState;
import org.apache.qpid.proton.engine.Event;
import org.apache.qpid.proton.engine.Link;
import org.apache.qpid.proton.engine.Receiver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReceiveLinkHandler extends LinkHandler {
    private final String linkName;
    private final AtomicBoolean isFirstResponse = new AtomicBoolean(true);
    private final Sinks.Many<Delivery> deliveries = Sinks.many().multicast().directBestEffort();
    private final Set<Delivery> queuedDeliveries = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final String entityPath;

    public ReceiveLinkHandler(String connectionId, String hostname, String linkName, String entityPath) {
        super(connectionId, hostname, entityPath, new ClientLogger(ReceiveLinkHandler.class));
        this.linkName = linkName;
        this.entityPath = entityPath;
    }

    public String getLinkName() {
        return linkName;
    }

    public Flux<Delivery> getDeliveredMessages() {
        return deliveries.asFlux().doOnNext(delivery -> queuedDeliveries.remove(delivery));
    }

    @Override
    public void close() {
        deliveries.emitComplete((signalType, emitResult) -> {
            logger.verbose("connectionId[{}], entityPath[{}], linkName[{}] Could not emit complete.",
                getConnectionId(), entityPath, linkName);
            return false;
        });

        super.close();

        queuedDeliveries.forEach(delivery -> {
            // abandon the queued deliveries as the receive link handler is closed
            delivery.disposition(new Modified());
            delivery.settle();
        });
        queuedDeliveries.clear();
    }

    @Override
    public void onLinkLocalOpen(Event event) {
        final Link link = event.getLink();
        if (link instanceof Receiver) {
            logger.verbose("onLinkLocalOpen connectionId[{}], entityPath[{}], linkName[{}], localSource[{}]",
                getConnectionId(), entityPath, link.getName(), link.getSource());
        }
    }

    @Override
    public void onLinkRemoteOpen(Event event) {
        final Link link = event.getLink();
        if (!(link instanceof Receiver)) {
            return;
        }

        if (link.getRemoteSource() != null) {
            logger.info("onLinkRemoteOpen connectionId[{}], entityPath[{}], linkName[{}], remoteSource[{}]",
                getConnectionId(), entityPath, link.getName(), link.getRemoteSource());

            if (isFirstResponse.getAndSet(false)) {
                onNext(EndpointState.ACTIVE);
            }
        } else {
            logger.info("onLinkRemoteOpen connectionId[{}], entityPath[{}], linkName[{}], action[waitingForError]",
                getConnectionId(), entityPath, link.getName());
        }
    }

    @Override
    public void onDelivery(Event event) {
        if (isFirstResponse.getAndSet(false)) {
            onNext(EndpointState.ACTIVE);
        }

        final Delivery delivery = event.getDelivery();
        final Receiver link = (Receiver) delivery.getLink();

        // If a message spans across deliveries (for ex: 200k message will be 4 frames (deliveries) 64k 64k 64k 8k),
        // all until "last-1" deliveries will be partial
        // reactor will raise onDelivery event for all of these - we only need the last one
        if (!delivery.isPartial()) {
            // One of our customers hit an issue - where duplicate 'Delivery' events are raised to Reactor in
            // proton-j layer
            // While processing the duplicate event - reactor hits an IllegalStateException in proton-j layer
            // before we fix proton-j - this work around ensures that we ignore the duplicate Delivery event
            if (delivery.isSettled()) {
                if (link != null) {
                    logger.verbose("onDelivery connectionId[{}], entityPath[{}], linkName[{}], updatedLinkCredit[{}],"
                            + " remoteCredit[{}], remoteCondition[{}], delivery.isSettled[{}]",
                        getConnectionId(), entityPath, link.getName(), link.getCredit(), link.getRemoteCredit(),
                        link.getRemoteCondition(), delivery.isSettled());
                } else {
                    logger.warning("connectionId[{}], delivery.isSettled[{}]", getConnectionId(), delivery.isSettled());
                }
            } else {
                if (link.getLocalState() == EndpointState.CLOSED) {
                    // onDelivery() method may get called even after the local and remote link states are CLOSED.
                    // So, when the local link is CLOSED, we just abandon the delivery.
                    // Not settling every delivery will result in `TransportSession` storing all unsettled deliveries
                    // in the session leading to a memory leak when multiple links are opened and closed in the same
                    // session.
                    delivery.disposition(new Modified());
                    delivery.settle();
                } else {
                    queuedDeliveries.add(delivery);
                    deliveries.emitNext(delivery, (signalType, emitResult) -> {
                        logger.warning("connectionId[{}], entityPath[{}], linkName[{}] Could not emit delivery. {}",
                            getConnectionId(), entityPath, linkName, delivery);
                        return false;
                    });
                }
            }
        }

        if (link != null) {
            logger.verbose("onDelivery connectionId[{}], entityPath[{}], linkName[{}], updatedLinkCredit[{}],"
                    + "remoteCredit[{}], remoteCondition[{}], delivery.isPartial[{}]",
                getConnectionId(), entityPath, link.getName(), link.getCredit(), link.getRemoteCredit(),
                link.getRemoteCondition(), delivery.isPartial());
        }
    }

    @Override
    public void onLinkRemoteClose(Event event) {
        deliveries.emitComplete((signalType, emitResult) -> {
            logger.info("connectionId[{}] linkName[{}] signalType[{}] emitResult[{}] Could not complete 'deliveries'.",
                getConnectionId(), linkName, signalType, emitResult);
            return false;
        });

        super.onLinkRemoteClose(event);
    }
}
