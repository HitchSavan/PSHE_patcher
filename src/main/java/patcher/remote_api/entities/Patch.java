package patcher.remote_api.entities;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class Patch {
    @Getter @Setter
    private String versionFrom;
    
    @Getter @Setter
    private String versionTo;

    @Getter @Setter
    private Integer filesCount;

    @Getter @Setter
    private Integer totalSize;

    public Patch(JSONObject patch) {
        this.versionFrom = patch.getString("version_from");
        this.versionTo = patch.getString("version_to");
        this.filesCount = patch.getInt("files_count");
        this.totalSize = patch.getInt("total_size");
    }
}
