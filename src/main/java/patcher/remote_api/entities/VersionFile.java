package patcher.remote_api.entities;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class VersionFile {
    @Getter @Setter
    private String location;

    @Getter @Setter
    private String checksum;
    
    @Getter @Setter
    private Integer size;

    public VersionFile(JSONObject file) {
        this.location = file.getString("location");
        this.checksum = file.getString("checksum");
        this.size = file.getInt("size");
    }
}
