package patcher.remote_api.entities;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class File {
    @Getter @Setter
    private String location;

    @Getter @Setter
    private String checksum;
    
    @Getter @Setter
    private Long size;

    public File(JSONObject file) {
        this.location = file.getString("location");
        this.checksum = file.getJSONObject("version").getString("checksum");
        this.size = file.getJSONObject("version").getLong("size");
    }

    public JSONObject toJSON() {
        return new JSONObject().put("location", this.location).put("version",
                new JSONObject().put("checksum", this.checksum).put("size", this.size));
    }
}
