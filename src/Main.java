import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Main {

    static final List strategySel = Arrays.asList("ALL", "ANY", "NONE");
    static List<String> people;
    static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {

        String fileName = "person.txt";
        try {
            people = readFileAsListOfStrings(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        printMenu();
        int option = Integer.parseInt(reader.nextLine());
        while (true) {
            if (option == 0) {
                System.out.println("Bye!");
                break;
            } else if (option == 1) {
                selection();
                printMenu();
            } else if (option == 2) {
                printPeople();
                printMenu();
            } else if (option == 3) {
                print();
                printMenu();
            }
            else {
                System.out.println("Incorrect option! Try again.");
                printMenu();
            }
            option = Integer.parseInt(reader.nextLine());
        }


    }

    public static abstract class strategy {
        public abstract void search();
    }

    public static class All extends strategy {
        @Override
        public void search(){
            System.out.println("Enter a name or email to search all suitable people.");
            reader = new Scanner(System.in);
            List<String> query = Arrays.asList(reader.nextLine().toLowerCase().split("\\s+"));

            TreeMap<String, List<Integer>> invertedIndex = getInverterIndex();
            Map<String, List<Integer>> matches = new HashMap<>();
            for(String str : query) {
                if (invertedIndex.containsKey(str)) {
                    matches.put(str, invertedIndex.get(str));
                }
            }
            List<Integer> List1 = new ArrayList<>();
            List<Integer> final1 = new ArrayList<>();
            for (Map.Entry<String, List<Integer>> entry : matches.entrySet()) {
                final1 = getIntersectOfLists2(List1,entry.getValue());
                List1 = entry.getValue();
            }
            System.out.println(final1.size() + " persons found: ");
            final1.forEach(index -> System.out.println(people.get(index)));
        }
    }

    public static class None extends strategy {

        @Override
        public void search(){
            System.out.println("Enter a name or email to search all suitable people.");
            reader = new Scanner(System.in);
            List<String> query = Arrays.asList(reader.nextLine().toLowerCase().split("\\s+"));

            TreeMap<String, List<Integer>> invertedIndex = getInverterIndex();
            Map<String, List<Integer>> matches = new HashMap<>();
            for(String str : query) {
                if (invertedIndex.containsKey(str)) {
                    matches.put(str, invertedIndex.get(str));
                }
            }

            List<Integer> List1 = new ArrayList<>();
            List<Integer> final1 = new ArrayList<>();
            for (Map.Entry<String, List<Integer>> entry : matches.entrySet()) {
                final1 = getUnionOfLists(List1,entry.getValue());
                List1 = entry.getValue();
            }
            List<Integer> List2 = new ArrayList<>();

            for (int i = 0; i < people.size(); i++) {
                if (!final1.contains(i))
                    List2.add(i);
            }
            System.out.println(List2.size() + " persons found: ");
            List2.forEach(index -> System.out.println(people.get(index)));
        }

    }

    public static class Any extends strategy {
        @Override
        public void search(){
            System.out.println("Enter a name or email to search all suitable people.");
            reader = new Scanner(System.in);
            List<String> query = Arrays.asList(reader.nextLine().toLowerCase().split("\\s+"));

            TreeMap<String, List<Integer>> invertedIndex = getInverterIndex();
            Map<String, List<Integer>> matches = new HashMap<>();
            for(String str : query) {
                if (invertedIndex.containsKey(str)) {
                    matches.put(str, invertedIndex.get(str));
                }
            }

            List<Integer> List1 = new ArrayList<>();
            List<Integer> final1 = new ArrayList<>();
            for (Map.Entry<String, List<Integer>> entry : matches.entrySet()) {
                final1 = getUnionOfLists(List1,entry.getValue());
                List1 = entry.getValue();
            }
            System.out.println(final1.size() + " persons found: ");
            final1.forEach(index -> System.out.println(people.get(index)));
        }
    }

    private static void printPeople() {
        System.out.println("=== List of people ===");
        for (String person : people) {
            System.out.println(person);
        }
    }


    public static void printMenu() {
        System.out.println("=== Menu ===\n" +
                "1. Find a person\n" +
                "2. Print all people\n" +
                "3. Print TreeMAP\n" +
                "0. Exit");
    }



    private static TreeMap<String, List<Integer>> getInverterIndex() {
        TreeMap<String, List<Integer>> inverterIndex = new TreeMap<>();
        int index = 0;
        List<Integer> indexes;
        for (String person : people) {
            for (String word : person.split(" ")) {
                if (inverterIndex.containsKey(word.toLowerCase())) {
                    indexes = inverterIndex.get(word.toLowerCase());
                } else {
                    indexes = new ArrayList<>();
                }
                indexes.add(index);
                inverterIndex.put(word.toLowerCase(), indexes);
            }
            index++;
        }
        return inverterIndex;
    }

    public static void selection() {
        String choice;
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        Scanner sc = new Scanner(System.in);
        choice = sc.next().toUpperCase();
        if(strategySel.contains(choice)) {
            switch (choice) {
                case "ALL":
                    strategy all = new All();
                    all.search();
                    break;
                case "ANY":
                    strategy any = new Any();
                    any.search();
                    break;
                case "NONE":
                    strategy none = new None();
                    none.search();
                    break;
            }
        }
    }

    private static void print() {
        for (Map.Entry<String, List<Integer>> entry : getInverterIndex().entrySet()) {
            System.out.println("Key: " + entry.getKey() + " -- Value: " + entry.getValue());
        }
    }

    public static List<String> readFileAsListOfStrings(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(fileName));
    }


        private static List<Integer> getUnionOfLists(List<Integer> list1, List<Integer> list2) {

            Set<Integer> set = new HashSet<>();
            set.addAll(list1);
            set.addAll(list2);

            return new ArrayList<>(set);
        }

        private static List<Integer> getIntersectOfLists1(List<Integer> list1, List<Integer> list2) {
            List<Integer> intersectElements = list1.stream()
                    .filter(list2 :: contains)
                    .collect(Collectors.toList());

            if(!intersectElements.isEmpty()) {
                return intersectElements;
            }else {
                return Collections.emptyList();
            }
        }

        private static List<Integer> getIntersectOfLists2(List<Integer> list1, List<Integer> list2) {
            list1.retainAll(list2);

            return list1;
        }

    }