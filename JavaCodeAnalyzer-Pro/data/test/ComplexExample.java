package test;

/**
 * Complex test class with high cyclomatic complexity
 */
public class ComplexExample {

    public void complexMethod(int x) {
        if (x > 0) {
            for (int i = 0; i < x; i++) {
                if (i % 2 == 0) {
                    System.out.println("Even: " + i);
                } else {
                    System.out.println("Odd: " + i);
                }
            }
        } else if (x < 0) {
            System.out.println("Negative");
        } else {
            System.out.println("Zero");
        }
    }

    public void switchExample(String type) {
        switch(type) {
            case "A":
                if (true) {
                    System.out.println("A");
                }
                break;
            case "B":
                System.out.println("B");
                break;
            case "C":
                System.out.println("C");
                break;
            default:
                System.out.println("Unknown");
        }
    }
}
