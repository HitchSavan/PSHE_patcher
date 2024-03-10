package patcher.remote_api.entities;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class FileEntity {
    @Getter @Setter
    private Path location;

    @Getter @Setter
    private String checksum;
    
    @Getter @Setter
    private Long size;

    public FileEntity(JSONObject file) {
        this.location = Paths.get(file.getString("location"));
        this.checksum = file.getJSONObject("version").getString("checksum");
        this.size = file.getJSONObject("version").getLong("size");
    }

    public JSONObject toJSON() {
        return new JSONObject().put("location", this.location).put("version",
                new JSONObject().put("checksum", this.checksum).put("size", this.size));
    }
}
