package com.sandro.query;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */


public class EventQuery {

    public static final String SELECT_EVENTS_BY_USER_ID =
            "SELECT users_events.device, users_events.ip_address, users_events.created_at, events.type, events.description FROM users_events " +
                    "JOIN events ON users_events.event_id = events.id " +
                    "JOIN users ON users_events.user_id = users.id " +
                    "WHERE users.id = :userId " +
                    "ORDER BY users_events.created_at DESC LIMIT 15";

    public static final String INSERT_EVENT_BY_USER_EMAIL_QUERY =
            "INSERT INTO users_events (user_id, event_id, device, ip_address) VALUES ((SELECT id FROM users WHERE email = :email), (SELECT id FROM events WHERE type = :eventType), :device, :ipAddress)";

}
