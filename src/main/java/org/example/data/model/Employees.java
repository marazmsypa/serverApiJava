package org.example.data.model;

import org.example.database.annotations.Column;
import org.example.database.annotations.ForeignKey;
import org.example.database.annotations.ID;
import org.example.database.annotations.Table;

import java.util.Objects;

@Table(name = "employees")
public class Employees {

    @ID
    @Column(name = "id")
    private Integer id;
    @Column(name = "surname")
    private String surname;
    @Column(name = "name")
    private String name;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "code")
    private Integer code;
    @Column(name = "subdivision_id")
    private Integer subdivision_id;
    @ForeignKey
    @Column(name = "division_id")
    private Divisions division;

    public Employees() {
    }

    public Employees(Integer id, String surname, String name, String patronymic, Integer code, Integer subdivision_id, Divisions division) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.code = code;
        this.subdivision_id = subdivision_id;
        this.division = division;
    }

    public Divisions getDivision() {
        return division;
    }

    public void setDivision(Divisions division) {
        this.division = division;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getSubdivision_id() {
        return subdivision_id;
    }

    public void setSubdivision_id(Integer subdivision_id) {
        this.subdivision_id = subdivision_id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employees employees = (Employees) o;

        if (!Objects.equals(id, employees.id)) return false;
        if (!Objects.equals(surname, employees.surname)) return false;
        if (!Objects.equals(name, employees.name)) return false;
        if (!Objects.equals(patronymic, employees.patronymic)) return false;
        if (!Objects.equals(code, employees.code)) return false;
        if (!Objects.equals(subdivision_id, employees.subdivision_id))
            return false;
        return Objects.equals(division, employees.division);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (patronymic != null ? patronymic.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (subdivision_id != null ? subdivision_id.hashCode() : 0);
        result = 31 * result + (division != null ? division.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Employees{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", code=" + code +
                ", subdivision_id=" + subdivision_id +
                ", division=" + division +
                '}';
    }
}
