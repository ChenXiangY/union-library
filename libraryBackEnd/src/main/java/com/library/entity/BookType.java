package com.library.entity;

import javax.persistence.Id;

public class BookType {
    @Id
    private Integer id;
    private String typeName;

    @Override
    public String toString() {
        return "BookType{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
