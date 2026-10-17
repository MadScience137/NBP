package ch.nbodyproblems.ui;

import ch.nbodyproblems.input.InputSystem;
import com.spinyowl.legui.system.event.*;
import com.spinyowl.legui.system.handler.processor.SystemEventProcessorImpl;

public class EventProcessor extends SystemEventProcessorImpl {
    private final InputSystem inputSystem;

    public EventProcessor(InputSystem inputSystem) {
        this.inputSystem = inputSystem;
    }

    @Override
    public void pushEvent(SystemEvent event) {
        boolean pointerEvent = event instanceof SystemCursorPosEvent
                || event instanceof SystemCursorEnterEvent
                || event instanceof SystemMouseClickEvent
                || event instanceof SystemScrollEvent;

        if (pointerEvent && inputSystem.isMouseCaptured()) return;

        super.pushEvent(event);
    }
}
