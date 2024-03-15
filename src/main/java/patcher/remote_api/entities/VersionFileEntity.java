package patcher.remote_api.entities;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class VersionFileEntity {
    @Getter @Setter
    private Path location;

    @Getter @Setter
    private String checksum;
    
    @Getter @Setter
    private Long size;

    public VersionFileEntity(JSONObject file) {
        this.location = Paths.get(file.getString("location"));
        this.checksum = file.getString("checksum");
        this.size = file.getLong("size");
    }
    public VersionFileEntity(JSONObject file, Path location) {
        this.location = location;
        this.checksum = file.getString("checksum");
        this.size = file.getLong("size");
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("location", this.location)
                .put("checksum", this.checksum)
                .put("size", this.size);
    }
}
