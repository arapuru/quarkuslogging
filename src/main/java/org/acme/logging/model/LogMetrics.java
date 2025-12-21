package org.acme.logging.model;

public class LogMetrics {
    private long processing_time_ms;
    private String payload_size_bytes;

    public long getProcessing_time_ms() {
        return processing_time_ms;
    }

    public void setProcessing_time_ms(long processing_time_ms) {
        this.processing_time_ms = processing_time_ms;
    }

    public String getPayload_size_bytes() {
        return payload_size_bytes;
    }

    public void setPayload_size_bytes(String payload_size_bytes) {
        this.payload_size_bytes = payload_size_bytes;
    }




}
