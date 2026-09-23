package za.co.securevault.model;

public class SecurityEvent {

    private long id;
    private long assetId;
    private EventType eventType;
    private String sourceIp;
    private String description;
    private Severity severity;

    public SecurityEvent(
            long id,
            long assetId,
            EventType eventType,
            String sourceIp,
            String description,
            Severity severity
    ) {
        this.id = id;
        this.assetId = assetId;
        this.eventType = eventType;
        this.sourceIp = sourceIp;
        this.description = description;
        this.severity = severity;
    }

    public long getId() {
        return id;
    }

    public long getAssetId() {
        return assetId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getDescription() {
        return description;
    }

    public Severity getSeverity() {
        return severity;
    }
}
