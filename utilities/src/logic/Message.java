package logic;

import java.io.Serializable;
/**
 * Represents a message containing a command and a payload.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L; // Simple version identifier
    private String command;
    private Object msg;

    /**
     * Constructs a Message object with the specified command and payload.
     *
     * @param command The command associated with the message
     * @param payload The payload of the message
     */
    public Message(String command, Object payload) {
        this.command = command;
        this.msg = payload;
    }

    /**
     * Gets the command associated with the message.
     *
     * @return The command of the message
     */
    public String getCommand() {
        return command;
    }
    /**
     * Gets the payload of the message.
     *
     * @return The payload of the message
     */
    public Object getPayload() {
        return msg;
    }
}

