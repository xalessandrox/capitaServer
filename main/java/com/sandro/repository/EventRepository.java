package com.sandro.repository;

import com.sandro.domain.UserEvent;
import com.sandro.enumeration.EventType;

import java.util.Collection;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */


public interface EventRepository {
    Collection<UserEvent> getEventsByUserId(Long userId);

    void addUserEvent(String email, EventType type, String device, String ipAddress);
    void addUserEvent(Long userId, EventType type, String device, String ipAddress);
}
