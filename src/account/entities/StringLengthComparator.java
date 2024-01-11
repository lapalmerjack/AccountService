package account.entities;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<Role> {
    @Override
    public int compare(Role role1, Role role2) {
        System.out.println("GETTING THAT ASSORTMENT");
        // Custom comparison logic based on the length of the strings
        return role1.getRole().compareTo(role2.getRole());
    }
}
