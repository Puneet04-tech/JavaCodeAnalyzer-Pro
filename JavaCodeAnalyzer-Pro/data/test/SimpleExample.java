package test;

/**
 * Simple test class for analysis
 */
public class SimpleExample {

    private String name;

    public SimpleExample(String name) {
        this.name = name;
    }

    public void printName() {
        System.out.println("Name: " + name);
    }

    public static void main(String[] args) {
        SimpleExample ex = new SimpleExample("Test");
        ex.printName();
    }
}
