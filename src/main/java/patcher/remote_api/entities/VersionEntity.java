package patcher.remote_api.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class VersionEntity {
    @Getter @Setter
    private String versionString;

    @Getter @Setter
    private String createdAt;
    
    @Getter @Setter
    private Map<Path, VersionFileEntity> files;

    @Getter @Setter
    private Long filesCount;

    @Getter @Setter
    private Long totalSize;

    @Getter @Setter
    private boolean isRoot;

    public VersionEntity(JSONObject version) {
        this.versionString = version.getString("v_string");
        this.createdAt = version.getString("created_at");
        this.filesCount = version.getLong("files_count");
        this.totalSize = version.getLong("total_size");
        this.isRoot = version.getBoolean("is_root");

        this.files = new HashMap<>();
        version.getJSONArray("files").iterator().forEachRemaining(item -> {
            JSONObject jsonItem = (JSONObject) item;
            this.files.put(Paths.get(jsonItem.getString("location")), new VersionFileEntity(jsonItem));
        });
    }

    public JSONObject toJSON() {
        JSONObject jsonData = new JSONObject();
        jsonData.put("v_string", this.versionString)
                .put("files_count", this.filesCount)
                .put("total_size", this.totalSize)
                .put("files", new JSONArray());
        files.forEach((location, file) -> {
            jsonData.getJSONArray("files").put(file.toJSON());
        });
        return jsonData;
    }
}
