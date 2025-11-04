package org.cuoi_ki.bai2;

import java.util.ArrayList;
import java.util.List;

//Person là một node trong cây – nó biết:
//Tên mình là gì.
//Mình thuộc thế hệ nào.
//Ai là cha mẹ.
//Mình có bao nhiêu con/cháu.
public class Person {
    private String id;
    private String name;
    private int birthYear;
    private String gender;
    private Person spouse;
    private List<Person> children;
    private Person parent;

    //Hàm khởi tạo: tạo một người mới chỉ có tên, chưa có con, chưa biết cha.
    public Person(String id, String name, int birthYear, String gender) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        this.gender = gender;
        this.children = new ArrayList<>();
    }

    // Getter và Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Person getSpouse() { return spouse; }
    public void setSpouse(Person spouse) { this.spouse = spouse; }

    public List<Person> getChildren() { return children; }
    public void addChild(Person child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Person child) {
        this.children.remove(child);
    }

    public Person getParent() { return parent; }
    public void setParent(Person parent) { this.parent = parent; }

    public int getNumberOfChildren() {
        return children.size();
    }

    public int getNumberOfDescendants() {
        int count = children.size();
        for (Person child : children) {
            count += child.getNumberOfDescendants();
        }
        return count;
    }

    @Override
    public String toString() {
        return name + " (" + birthYear + ")";
    }
}
