package dorkbox.util.messagebus.common;

import java.util.Arrays;

/**
 * The dead message event is published whenever no message
 * handlers could be found for a given message publication.
 *
 * @author bennidi
 *         Date: 1/18/13
 * @author dorkbox, llc
 *         Date: 2/2/15
 */
public final class DeadMessage {

    private final Object[] relatedMessages;


    public DeadMessage(Object message) {
        this.relatedMessages = new Object[1];
        this.relatedMessages[0] = message;
    }

    public DeadMessage(Object message1, Object message2) {
        this.relatedMessages = new Object[2];
        this.relatedMessages[0] = message1;
        this.relatedMessages[1] = message2;
    }

    public DeadMessage(Object message1, Object message2, Object message3) {
        this.relatedMessages = new Object[3];
        this.relatedMessages[0] = message1;
        this.relatedMessages[1] = message2;
        this.relatedMessages[2] = message3;
    }

    public DeadMessage(Object[] messages) {
        this.relatedMessages = Arrays.copyOf(messages, messages.length);
    }

    public Object[] getMessages() {
        return this.relatedMessages;
    }
}
