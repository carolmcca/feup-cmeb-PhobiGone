package com.example.phobigone;

public class Badge {
    private String icon;
    private String title;
    private String description;
    private Integer id;

    public Badge(Integer id, String title, String description, String icon) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public Badge(String title, String description, String icon) {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() { return id; }

}
