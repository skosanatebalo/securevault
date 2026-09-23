package za.co.securevault.model;

public class Asset {

    private long id;
    private String hostname;
    private String ipAddress;
    private String operatingSystem;
    private String owner;

    public Asset(long id, String hostname, String ipAddress, String operatingSystem, String owner){
        this.id = id;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.operatingSystem = operatingSystem;
        this.owner = owner;
    }    

    public long getId(){
        return id;
    }
    public String getHostname(){
        return hostname;
    }
    public String getIpAddress(){
        return ipAddress;
    }
    public String getOperatingSystem(){
        return operatingSystem;
    }
    public String getOwner(){
        return owner;
    } 
}