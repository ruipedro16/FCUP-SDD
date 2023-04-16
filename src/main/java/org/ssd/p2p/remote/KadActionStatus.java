package org.ssd.p2p.remote;

/**
 * An enumeration representing the status of a request.
 * <p>
 * The possible values are:
 * <ul>
 * <li>{@code NOT_ASKED}: The request has not been made yet.</li>
 * <li>{@code AWAITING_RESPONSE}: The request has been made and is waiting for a response.</li>
 * <li>{@code RESPONDED}: The request has been made and has received a response.</li>
 * <li>{@code FAILED}: The request has failed or encountered an error.</li>
 * </ul>
 */

public enum KadActionStatus {
    NOT_ASKED,
    AWAITING_RESPONSE,
    RESPONDED,
    FAILED
}
