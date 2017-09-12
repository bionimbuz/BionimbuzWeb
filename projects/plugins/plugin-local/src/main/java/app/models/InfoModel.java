package app.models;

public class InfoModel {

    private final long id;
    private final String content;

    public InfoModel(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
