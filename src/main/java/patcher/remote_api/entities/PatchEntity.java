package patcher.remote_api.entities;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class PatchEntity {
    @Getter @Setter
    private String versionFrom;
    
    @Getter @Setter
    private String versionTo;

    @Getter @Setter
    private Long filesCount;

    @Getter @Setter
    private Long totalSize;

    public PatchEntity(JSONObject patch) {
        this.versionFrom = patch.getString("version_from");
        this.versionTo = patch.getString("version_to");
        this.filesCount = patch.getLong("files_count");
        this.totalSize = patch.getLong("total_size");
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("version_from", this.versionFrom)
                .put("version_to", this.versionTo)
                .put("files_count", this.filesCount)
                .put("total_size", this.totalSize);
    }
}
