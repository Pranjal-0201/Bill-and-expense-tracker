import java.util.*;

public class ExpenseManager {

    private final Map<String, Person> persons = new HashMap<>();
    private final List<Bill> bills = new ArrayList<>();

    public boolean addPerson(String name) {
        if(name == null || name.trim().isEmpty() || persons.containsKey(name.trim())) {
            return false;
        }
        persons.put(name.trim(), new Person(name.trim()));
        return true;
    }

    public Collection<Person> getAllPersons() {
        return persons.values();
    }

    public List<Bill> getAllBills() {
        return bills;
    }

    public Person getPersonByName(String name) {
        return persons.get(name);
    }

    public boolean addBill(String description, Person payer, double amount, List<Person> participants) {
        if(description == null || description.trim().isEmpty() || payer == null || amount <= 0 || participants == null || participants.isEmpty()) {
            return false;
        }

        Bill bill = new Bill(description.trim(), payer, amount, participants);
        bills.add(bill);

        // Update balances
        double splitAmount = amount / participants.size();

        for(Person p : participants) {
            if(p == payer) {
                p.addToBalance(amount - splitAmount);
            } else {
                p.addToBalance(-splitAmount);
            }
        }
        return true;
    }
}
