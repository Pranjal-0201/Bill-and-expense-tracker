public class Person {
    private final String name;
    private double balance; // + means they are owed, - means they owe

    public Person(String name) {
        this.name = name;
        this.balance = 0;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void addToBalance(double amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return name;
    }
}
