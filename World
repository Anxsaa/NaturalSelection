import java.util.*;

public class World {
    public static int width = 100;
    public static int height = 100;
    static final List<Food> foodList = new ArrayList<>();
    static final List<Creature> creatureList = new ArrayList<>();
    private static Random r = new Random();

    public World() {
        // Add creatures to the world with set attributes
        initializeCreatures("Alice", 10, 5, 5, 5, 0.7);
        initializeCreatures("Bill", 10, 2, 4, 2, 0.1);
        initializeCreatures("Chester", 10, 6, 2, 7, 0.6);
        initializeCreatures("Dan", 10, 3, 3, 3, 0.3);
        initializeCreatures("George", 10, 1, 7, 4, 0.4);
        initializeCreatures("Luca", 10, 7, 1, 6, 0.5);
        initializeCreatures("Louis", 10, 4, 6, 1, 0.2);
    }

    private void initializeCreatures(String baseName, int count, int speed, int size, int sense, double intelligence) {
        for (int i = 0; i < count; i++) {
            Creature c = new Creature(baseName + i, speed, size, sense, intelligence);
            c.x = r.nextInt(width);
            c.y = r.nextInt(height);
            creatureList.add(c);
        }
    }

    // Define a class for actions or decisions
    static class Action {
        Creature creature;
        int targetX, targetY;
        Object target; // Can be Food or another Creature

        Action(Creature creature, int targetX, int targetY, Object target) {
            this.creature = creature;
            this.targetX = targetX;
            this.targetY = targetY;
            this.target = target;
        }
    }

    // Simultaneous action execution
    public void newDay() {
        printCreatureCounts("Start of the day");

        boolean actionsPending = true;

        while (actionsPending) {
            actionsPending = false;

            // Decision and Action phase
            for (Creature c : new ArrayList<>(creatureList)) {
                if (c.energy > 0) {
                    Action action = c.decideAction(foodList, creatureList);
                    if (action != null) {
                        System.out.println(c.name + " is deciding and moving.");
                        c.move(action.targetX, action.targetY);
                        if (action.target instanceof Food) {
                            c.eat((Food) action.target);
                        } else if (action.target instanceof Creature) {
                            // Additional checks for creature interactions
                        }
                        actionsPending = true;
                    }
                } else {
                    c.checkStatus();
                }
            }

            // Print a dot to show progress in long-running simulation loops
            System.out.print(".");
        }
        System.out.println(); // Newline after progress dots

        // Process end of day logic, such as removing eaten food
        processEndOfDay();

        printCreatureCounts("End of the day");
        printDailySummary();
    }

    private void processEndOfDay() {
        foodList.removeIf(food -> !isFoodAvailable(food));
    }

    public static void addCreature(Creature c) {
        c.x = r.nextInt(width);
        c.y = r.nextInt(height);
        creatureList.add(c);
    }

    public static void removeCreature(Creature c) {
        creatureList.remove(c);
    }

    public static void addFood(Food f) {
        foodList.add(f);
    }

    public static void removeFood(Food f) {
        foodList.remove(f);
    }

    public static boolean isFoodAvailable(Food targetFood) {
        return foodList.contains(targetFood);
    }

    public static boolean isOccupied(int x, int y) {
        for (Creature c : creatureList) {
            if (c.x == x && c.y == y) {
                return true;
            }
        }
        return false;
    }

    private void printCreatureCounts(String moment) {
        Map<String, Integer> counts = new HashMap<>();
        for (Creature c : creatureList) {
            String prefix = c.name.replaceAll("\\d", "");
            counts.put(prefix, counts.getOrDefault(prefix, 0) + 1);
        }
        System.out.println(moment + ":");
        counts.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println();
    }

    private void printDailySummary() {
        System.out.println("Daily Summary of Actions:");
        for (Creature c : creatureList) {
            System.out.println(c.name + "'s actions:");
            c.getActions().forEach(System.out::println);
            System.out.println();
        }
    }
}
