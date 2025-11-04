package org.cuoi_ki.bai2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//Thêm / Xóa / Tìm / Đếm thế hệ.
//Duyệt cây bằng đệ quy.
public class FamilyTree {
    private Person root;
    private Map<String, Person> people;

    public FamilyTree() {
        this.people = new HashMap<>();
    }

    public void setRoot(Person root) {
        this.root = root;
        people.put(root.getId(), root);
    }

    public Person getRoot() {
        return root;
    }

    public void addPerson(Person person) {
        people.put(person.getId(), person);
    }

    public void addPerson(Person parent, Person child) {
        if (parent != null && child != null) {
            parent.addChild(child);
            people.put(child.getId(), child);
        }
    }

    public Person findPersonById(String id) {
        return people.get(id);
    }

    //Nếu người đó là gốc → xóa gốc.
    //Nếu không → xóa người đó khỏi danh sách con của cha.
    //Gọi removeDescendants() để xóa toàn bộ con cháu.
    //Xóa luôn id của người đó khỏi people
    public void removePerson(Person person) {
        if (person == root) {
            root = null;
        } else {
            Person parent = person.getParent();
            if (parent != null) {
                parent.removeChild(person);
            }
        }

        // Xóa tất cả con cháu
        removeDescendants(person);
        people.remove(person.getId());
    }

    //Duyệt đệ quy toàn bộ con cháu và xóa dần từng người khỏi people
    private void removeDescendants(Person person) {
        // Tạo bản sao của danh sách children để tránh bug
        List<Person> childrenCopy = new ArrayList<>(person.getChildren());
        for (Person child : childrenCopy) {
            removeDescendants(child);
            people.remove(child.getId());
        }
    }

    //Lấy danh sách người theo thế hệ
    public List<Person> getPeopleInGeneration(int generation) {
        List<Person> result = new ArrayList<>();
        if (root != null) {
            getPeopleInGeneration(root, generation, 0, result);
        }
        return result;
    }

    //Nếu currentGeneration == targetGeneration → thêm vào kết quả.
    //Nếu chưa đến → duyệt qua tất cả child
    private void getPeopleInGeneration(Person person, int targetGeneration,
                                       int currentGeneration, List<Person> result) {
        if (currentGeneration == targetGeneration) {
            result.add(person);
            return;
        }

        for (Person child : person.getChildren()) {
            getPeopleInGeneration(child, targetGeneration, currentGeneration + 1, result);
        }
    }

    public int getTotalGenerations() {
        if (root == null) return 0;
        return getMaxGeneration(root, 0);
    }

    //Tính tổng số thế hệ trong cây
    private int getMaxGeneration(Person person, int currentGeneration) {
        int maxGeneration = currentGeneration;
        for (Person child : person.getChildren()) {
            int childGeneration = getMaxGeneration(child, currentGeneration + 1);
            maxGeneration = Math.max(maxGeneration, childGeneration);
        }
        return maxGeneration;
    }

    public List<Person> getAllPeople() {
        return new ArrayList<>(people.values());
    }

    public int getTotalPeople() {
        return people.size();
    }
}