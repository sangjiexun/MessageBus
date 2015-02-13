package net.engio.mbassy.multi.disruptor;

import java.util.Collection;
import java.util.Queue;

import net.engio.mbassy.multi.MultiMBassador;
import net.engio.mbassy.multi.error.PublicationError;
import net.engio.mbassy.multi.subscription.Subscription;
import net.engio.mbassy.multi.subscription.SubscriptionManager;

import com.lmax.disruptor.EventHandler;

/**
 * @author dorkbox, llc Date: 2/2/15
 */
public class DispatchProcessor implements EventHandler<DispatchHolder> {
    private final MultiMBassador publisher;
    private final long ordinal;
    private final long numberOfConsumers;

    private final SubscriptionManager subscriptionManager;
//    private final RingBuffer<MessageHolder> invoke_RingBuffer;
    private final Queue<MessageHolder> queue;

    public DispatchProcessor(final MultiMBassador publisher, final long ordinal, final long numberOfConsumers,
                             final SubscriptionManager subscriptionManager, Queue<MessageHolder> queue) {
        this.publisher = publisher;

        this.ordinal = ordinal;
        this.numberOfConsumers = numberOfConsumers;
        this.subscriptionManager = subscriptionManager;
        this.queue = queue;
    }


    @Override
    public void onEvent(DispatchHolder event, long sequence, boolean endOfBatch) throws Exception {
        if (sequence % this.numberOfConsumers == this.ordinal) {
            // Process the event
            // switch (event.messageType) {
            // case ONE: {
            publish(event.message1);
            event.message1 = null; // cleanup
            // return;
            // }
            // case TWO: {
            // // publisher.publish(this.message1, this.message2);
            // event.message1 = null; // cleanup
            // event.message2 = null; // cleanup
            // return;
            // }
            // case THREE: {
            // // publisher.publish(this.message1, this.message2, this.message3);
            // event.message1 = null; // cleanup
            // event.message2 = null; // cleanup
            // event.message3 = null; // cleanup
            // return;
            // }
            // case ARRAY: {
            // // publisher.publish(this.messages);
            // event.messages = null; // cleanup
            // return;
            // }
            // }

        }
    }

    private void publish(Object message) {
        Class<?> messageClass = message.getClass();

        SubscriptionManager manager = this.subscriptionManager;
        Collection<Subscription> subscriptions = manager.getSubscriptionsByMessageType(messageClass);

        try {
            boolean empty = subscriptions.isEmpty();
            if (empty) {
                // Dead Event
                subscriptions = manager.getSubscriptionsByMessageType(DeadMessage.class);

                message = new DeadMessage(message);

                empty = subscriptions.isEmpty();
            }

            if (!empty) {
//                // put this on the disruptor ring buffer
//                final RingBuffer<MessageHolder> ringBuffer = this.invoke_RingBuffer;
//
//                // setup the job
//                final long seq = ringBuffer.next();
//                try {
//                    MessageHolder eventJob = ringBuffer.get(seq);
//                    eventJob.messageType = MessageType.ONE;
//                    eventJob.message1 = message;
//                    eventJob.subscriptions = subscriptions;
//                } catch (Throwable e) {
//                    this.publisher.handlePublicationError(new PublicationError()
//                                                .setMessage("Error while adding an asynchronous message")
//                                                .setCause(e)
//                                                .setPublishedObject(message));
//                } finally {
//                    // always publish the job
//                    ringBuffer.publish(seq);
//                }



//                // this is what gets parallelized. The collection IS NOT THREAD SAFE, but it's contents are
//                ObjectPoolHolder<MessageHolder> messageHolder = this.pool.take();
//                MessageHolder value = messageHolder.getValue();
                MessageHolder messageHolder = new MessageHolder();
                messageHolder.subscriptions= subscriptions;
                messageHolder.messageType = MessageType.ONE;
                messageHolder.message1 = message;

//                this.queue.put(messageHolder);

//                int counter = 200;
//                while (!this.queue.offer(messageHolder)) {
//                    if (counter > 100) {
//                        --counter;
//                    } else if (counter > 0) {
//                        --counter;
//                        Thread.yield();
//                    } else {
//                        LockSupport.parkNanos(1L);
//                    }
//                }
            }
        } catch (Throwable e) {
            this.publisher.handlePublicationError(new PublicationError().setMessage("Error during publication of message").setCause(e)
                            .setPublishedObject(message));
        }
    }

}
