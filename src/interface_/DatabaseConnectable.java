package interface_;

import java.util.List;
import java.util.Map;

public interface DatabaseConnectable {
    void connect() throws Exception;
    void save(String collection, String documentId, Map<String, Object> data) throws Exception;
    Map<String, Object> get(String collection, String documentId) throws Exception;
    List<Map<String, Object>> getAll(String collection) throws Exception;
    void update(String collection, String documentId, Map<String, Object> data) throws Exception;
    void delete(String collection, String documentId) throws Exception;
}