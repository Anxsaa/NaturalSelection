import java.util.*;

import static java.lang.Math.random;

public class Creature {
    private static int idCounter = 1;

    public int speed;
    public int size;
    public int sense;
    public double intelligence;
    public int energy;
    public int foodEaten;
    public int x;
    public int y;
    public String name;
    public int offspringCount;
    private List<String> actions; // List to store actions

    public Creature(String name, int speed, int size, int sense, double intelligence) {
        this.name = name;
        this.speed = speed;
        this.size = size;
        this.sense = sense;
        this.intelligence = intelligence;
        this.energy = 100;
        this.foodEaten = 0;
        this.offspringCount = 0;
        this.actions = new ArrayList<>();
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public List<String> getActions() {
        return actions;
    }

    public void search(List<Food> allFoods, List<Creature> allCreatures) {
        List<Food> visibleFoods = new ArrayList<>();
        List<Creature> nearbyCreatures = new ArrayList<>();

        for (Food food : allFoods) {
            if (calculateDistance(this.x, this.y, food.getX(), food.getY()) <= this.sense) {
                visibleFoods.add(food);
            }
        }

        for (Creature creature : allCreatures) {
            if (creature != this && calculateDistance(this.x, this.y, creature.getX(), creature.getY()) <= this.sense) {
                nearbyCreatures.add(creature);
            }
        }

        Food targetFood = bestDecision(visibleFoods, nearbyCreatures);
        if (targetFood != null) {
            move(targetFood.getX(), targetFood.getY());
            if (calculateDistance(this.x, this.y, targetFood.getX(), targetFood.getY()) == 0) {
                eat(targetFood);
            }
        }
    }

    public void move(int targetX, int targetY) {
        double distance = calculateDistance(this.x, this.y, targetX, targetY);
        if (distance == 0) return;

        int deltaX = targetX - this.x;
        int deltaY = targetY - this.y;
        double norm = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX /= norm;
        deltaY /= norm;

        this.x += deltaX * speed;
        this.y += deltaY * speed;

        energy -= 10 + speed; // Deduct energy for moving
        actions.add("Moved to (" + targetX + ", " + targetY + ")");
        System.out.println(name + " moved to (" + targetX + ", " + targetY + ")");
    }

    public void eat(Food food) {
        if (calculateDistance(this.x, this.y, food.getX(), food.getY()) == 0) {
            energy -= 20;
            foodEaten++;
            World.removeFood(food);
            actions.add("Ate food at (" + food.getX() + ", " + food.getY() + ")");
            System.out.println(name + " ate food at (" + food.getX() + ", " + food.getY() + ")");
        }
    }

    public void checkStatus() {
        if (energy <= 0) {
            actions.add("Died");
            System.out.println(name + " died.");
            die();
        } else {
            for (int i = 0; i + 1 < foodEaten; i++) {
                reproduce();
            }
            rest();
        }
    }

    public void die() {
        World.removeCreature(this);
    }

    public void reproduce() {
        String offSpringName = this.name + offspringCount;
        World.addCreature(new Creature(offSpringName, this.speed, this.size, this.sense, this.intelligence));
        offspringCount++;
        actions.add("Reproduced, offspring: " + offSpringName);
        System.out.println(name + " reproduced, offspring: " + offSpringName);
    }

    public void rest() {
        energy = 100;
        foodEaten = 0;
        Random rand = new Random();
        boolean placed = false;
        while (!placed) {
            int edgePosition = rand.nextInt(2 * (World.width + World.height) - 4);
            int x = 0, y = 0;

            if (edgePosition < World.width) {
                x = edgePosition;
                y = 0;
            } else if (edgePosition < World.width + World.height - 1) {
                x = World.width - 1;
                y = edgePosition - World.width + 1;
            } else if (edgePosition < 2 * World.width + World.height - 2) {
                x = 2 * World.width + World.height - 3 - edgePosition;
                y = World.height - 1;
            } else {
                x = 0;
                y = 2 * (World.width + World.height) - 4 - edgePosition;
            }

            if (!World.isOccupied(x, y)) {
                this.x = x;
                this.y = y;
                placed = true;
            }
        }
        actions.add("Rested at (" + this.x + ", " + this.y + ")");
        System.out.println(name + " rested at (" + this.x + ", " + this.y + ")");
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public Food bestDecision(List<Food> visibleFoods, List<Creature> nearbyCreatures) {
        Collections.sort(visibleFoods, Comparator.comparingDouble(food -> calculateDistance(this.x, this.y, food.getX(), food.getY())));
        List<Food> closestFoods = visibleFoods.subList(0, Math.min(2, visibleFoods.size()));

        Food chosenFood = null;
        double minTimeToFood = Double.MAX_VALUE;

        for (Food food : closestFoods) {
            double distanceToFood = calculateDistance(this.x, this.y, food.getX(), food.getY());
            double timeToFood = distanceToFood / this.speed;

            boolean canReachFirst = true;
            for (Creature creature : nearbyCreatures) {
                double otherDistanceToFood = calculateDistance(creature.getX(), creature.getY(), food.getX(), food.getY());
                double otherTimeToFood = otherDistanceToFood / creature.speed;

                if (otherTimeToFood < timeToFood || creature.size > this.size) {
                    canReachFirst = false;
                    break;
                }
            }

            if (canReachFirst && timeToFood < minTimeToFood) {
                minTimeToFood = timeToFood;
                chosenFood = food;
            }
        }

        return chosenFood;
    }

    public World.Action decideAction(List<Food> visibleFoods, List<Creature> nearbyCreatures) {
        search(visibleFoods, nearbyCreatures);
        Food targetFood = bestDecision(visibleFoods, nearbyCreatures);
        if (targetFood != null) {
            return new World.Action(this, targetFood.getX(), targetFood.getY(), targetFood);
        }
        return new World.Action(this, this.x, this.y, null);
    }
}
