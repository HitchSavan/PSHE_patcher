package patcher.remote_api.entities;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class Version {
    @Getter @Setter
    private String versionString;

    @Getter @Setter
    private String createdAt;
    
    @Getter @Setter
    private Map<String, VersionFile> files;

    @Getter @Setter
    private Integer filesCount;

    @Getter @Setter
    private Integer totalSize;

    public Version(JSONObject version) {
        this.versionString = version.getString("v_string");
        this.createdAt = version.getString("created_at");
        this.filesCount = version.getInt("files_count");
        this.totalSize = version.getInt("total_size");

        this.files = new HashMap<>();
        version.getJSONArray("files").iterator().forEachRemaining(item -> {
            JSONObject jsonItem = (JSONObject) item;
            this.files.put(jsonItem.getString("location"), new VersionFile(jsonItem));
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
