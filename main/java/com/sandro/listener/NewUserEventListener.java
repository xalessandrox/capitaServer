package com.sandro.listener;

import com.sandro.event.NewUserEvent;
import com.sandro.service.EventService;
import com.sandro.utils.EventUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {

    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent userEvent) {
        log.info("NewUserEvent is fired ! 🚀");
        eventService.addUserEvent(
                userEvent.getEmail(),
                userEvent.getEventType(),
                EventUtils.getDevice(request),
                EventUtils.getIpAddress(request));
    }

}
