package org.example.data.model;

import org.example.database.annotations.Column;
import org.example.database.annotations.ID;
import org.example.database.annotations.Table;

import java.math.BigInteger;
import java.util.Objects;

@Table(name = "divisions")
public class Divisions {
    @ID
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "name")
    private String name;

    public Divisions() {

    }

    public Divisions(BigInteger id, String name) {
        this.id = id;
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Divisions divisions = (Divisions) o;

        if (!Objects.equals(id, divisions.id)) return false;
        return Objects.equals(name, divisions.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id + ", " + name;
    }

}
