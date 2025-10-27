import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * A programm that computes {@link Circle} and {@link Cylinder} properties. 
 *
 * @author Tim Raphael (mail@tim-raphael.dev)
 * @version v0.1.0
 */
public class CylinderCalculation {
    /**
     * Processes the radius and height inputs, then prints the computed 
     * properties to the console.
     *
     * @param args; Get ignored 🤷.
     */
    public static void main(String[] args) {
        // Did previously go with an approach that looked like this: 
        //
        //  processInputs()
        //      .into()
        //      .display()
        //      .into()
        //      .display();
        //
        // The issue here was that Java does not allow me to implement Into<T>
        // for more than one generic type per class. That's also the reason why 
        // no further type annotations allowed/needed, which would read a lot
        // clearer.
        //
        // Example: 
        //  processInputs().into::<Cylinder>();
        //
        // Coming from Rust; Display triggering side effects, i.e. io 
        // (System.out), may seem like an anti pattern, but I think it would've
        // been fine, especially when looking at the scope of this programm.
        //
        // It should be easier to reason what is happening here: 
        Dimensions dimesions = processInputs();
        Cylinder cylinder = dimesions.into();
        System.out.print(cylinder.fmt());
        Circle circle = cylinder.into();
        System.out.print(circle.fmt());
    }

    /**
     * Processes keyboard inputs.
     *
     * @return {@link Dimensions}
     */
    static Dimensions processInputs() {
        Scanner keyboard = new Scanner(System.in);
        keyboard.useDelimiter(System.lineSeparator());
        int radius = getPositiveInt(keyboard, "Provide the radius of the cylinder:");
        int height = getPositiveInt(keyboard, "Provide the height of the cylinder:");
        keyboard.close();
        return new Dimensions(radius, height);
    }

    /**
     * @param scanner
     * @param prompt
     * @return positiveInteger
     */
    static int getPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                int value = scanner.nextInt();
                if (value <= 0) {
                    System.err.println("Error: Value must be positive. Please try again.");
                    continue;
                }
                return value;
            } catch (InputMismatchException err) {
                System.err.println("Error: Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }
    }
}

// My Java is a bit rusty 🦀.
// I think Java has no generic interface for type conversions?
//
// This approach does not really make sense, because you can't impl this for 
// more than one type per class.  
/**
 * Generic interface for converting Into another type. 
 *
 * @param <T>; Type to convert Into.
 */
interface Into<T> {
    /**
     * @return T; Type to convert Into.
     */
    public T into();
}

/**
 * Generic interface to Display data.
 */
interface Display {
    /**
     * A method that formats the attributes of a class. 
     *
     * @return String; Formatted string representing values of this instance.  
     */
    public String fmt();
}

// Java forcing me to use named types, because their are no tuple like 
// structures for this purpose? 
/**
 * Dimensions of a {@link Cylinder}.
 */
class Dimensions implements Into<Cylinder> {
    public final int RADIUS; 
    public final int HEIGHT; 

    /**
     * @param radius
     * @param height
     */
    public Dimensions(int radius, int height) { 
        this.RADIUS = radius; 
        this.HEIGHT = height; 
    } 

    @Override
    public Cylinder into() {
        return new Cylinder(this.RADIUS, this.HEIGHT);
    }
}

/**
 * Representation of a Cylinder.
 */
class Cylinder implements 
    Into<Circle>,
    Display 
{
    public final double VOLUME;
    public final double LATERAL_SURFACE_AREA;
    public final double TOTAL_SURFACE_AREA;
    final Circle CIRCLE; 

    /**
     * @param radius
     * @param height
     */
    public Cylinder(double radius, double height) {
        this.CIRCLE = new Circle(radius);
        this.VOLUME = getVolume(CIRCLE.AREA, height);
        this.LATERAL_SURFACE_AREA = getLateralSurfaceArea(CIRCLE.CIRCUMFERENCE, height); 
        this.TOTAL_SURFACE_AREA = getTotalSurfaceArea(CIRCLE.AREA, LATERAL_SURFACE_AREA);
    }

    /**
     * @param area
     * @param height
     * @return volume
     */
    static public double getVolume(double area, double height) {
        return area * height;
    }

    /**
     * @param circumference
     * @param height
     * @return lateralSurfaceArea
     */
    static public double getLateralSurfaceArea(double circumference, double height) {
        return circumference * height;
    }

    /**
     * @param area
     * @param lateralSufaceArea
     * @return totalSufaceArea
     */
    static public double getTotalSurfaceArea(double area, double lateralSufaceArea) {
        return (2 * area) + lateralSufaceArea;
    }

    @Override
    public Circle into() {
        return this.CIRCLE;
    }

    @Override
    public String fmt() {
        return """
            Cylinder:
                - Volume: %s
                - Lateral surface area: %s
                - Total surface area: %s
            """.formatted(this.VOLUME, this.LATERAL_SURFACE_AREA, this.TOTAL_SURFACE_AREA);
    }
}

/**
 * Representation of a Circle.
 */
class Circle implements Display {
    public static final double PI = 3.141592; // Math.PI
    public final double DIAMETER;
    public final double CIRCUMFERENCE;
    public final double AREA;

    /**
     * @param radius
     */
    public Circle(double radius) {
        this.DIAMETER = getDiameter(radius);
        this.CIRCUMFERENCE = getCircumference(radius);
        this.AREA = getArea(radius);
    }

    /**
     * @param radius
     * @return diameter
     */
    static public double getDiameter(double radius) {
        return 2 * radius;
    }

    /**
     * @param radius
     * @return circumference
     */
    static public double getCircumference(double radius) {
        return 2 * Circle.PI * radius;
    }

    /**
     * @param radius
     * @return area
     */
    static public double getArea(double radius) {
        return Math.pow(radius, 2) * Circle.PI;
    }

    @Override
    public String fmt () {
        return """
            Circle:
                - Diameter: %s
                - Circumference: %s
                - Area: %s
            """.formatted(this.DIAMETER, this.CIRCUMFERENCE, this.AREA);
    }
}
