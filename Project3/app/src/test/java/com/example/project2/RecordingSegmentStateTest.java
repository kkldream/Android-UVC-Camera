package com.example.project2;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecordingSegmentStateTest {
    @Test
    public void segmentStateIsIndependentForEachCamera() {
        RecordingSegmentState firstCamera = new RecordingSegmentState();
        RecordingSegmentState secondCamera = new RecordingSegmentState();

        firstCamera.markPrimarySegmentOpened(1000L);

        assertFalse(firstCamera.shouldOpenPrimarySegment(1000L, 5000L));
        assertTrue(secondCamera.shouldOpenPrimarySegment(1000L, 5000L));
    }

    @Test
    public void segmentStateSwitchesToSecondarySegmentAfterDuration() {
        RecordingSegmentState state = new RecordingSegmentState();

        state.markPrimarySegmentOpened(1000L);

        assertFalse(state.shouldOpenSecondarySegment(5999L, 5000L));
        assertTrue(state.shouldOpenSecondarySegment(6000L, 5000L));
    }
}
