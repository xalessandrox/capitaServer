package com.sandro.event;

import com.sandro.enumeration.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {
    private EventType eventType;
    private String email;

    public NewUserEvent(String email, EventType eventType) {
        super(email);
        this.email = email;
        this.eventType = eventType;
    }
}
