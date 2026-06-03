package com.example.project2;

class RecordingSegmentState {
    private int segmentCount = 0;
    private long primarySegmentStartedAt = 0L;

    boolean shouldOpenPrimarySegment(long timestamp, long segmentDurationMs) {
        return segmentCount == 0 || timestamp - primarySegmentStartedAt >= segmentDurationMs * 2L;
    }

    void markPrimarySegmentOpened(long timestamp) {
        primarySegmentStartedAt = timestamp;
        segmentCount++;
    }

    boolean shouldOpenSecondarySegment(long timestamp, long segmentDurationMs) {
        return segmentCount % 2 == 1 && timestamp - primarySegmentStartedAt >= segmentDurationMs;
    }

    void markSecondarySegmentOpened() {
        segmentCount++;
    }

    boolean shouldClosePrimarySegment(long timestamp, long segmentDurationMs) {
        return timestamp - primarySegmentStartedAt >= segmentDurationMs + 1000L;
    }

    boolean shouldCloseSecondarySegment(long timestamp) {
        return segmentCount % 2 == 1 && timestamp - primarySegmentStartedAt >= 1000L;
    }

    void reset() {
        segmentCount = 0;
        primarySegmentStartedAt = 0L;
    }
}
