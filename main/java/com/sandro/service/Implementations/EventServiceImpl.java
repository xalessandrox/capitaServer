package com.sandro.service.Implementations;

import com.sandro.domain.UserEvent;
import com.sandro.enumeration.EventType;
import com.sandro.repository.EventRepository;
import com.sandro.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return repository.getEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType type, String device, String ipAddress) {
        repository.addUserEvent(email, type, device, ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType type, String device, String ipAddress) {

    }
}
