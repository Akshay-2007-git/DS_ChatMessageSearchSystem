package p1;
import java.util.*;

public class code {

    // ============================================================
    // MESSAGE CLASS
    // ============================================================

    static class Message {
        int id;
        String sender;
        String timestamp;
        String text;

        Message(int id, String sender, String timestamp, String text) {
            this.id = id;
            this.sender = sender;
            this.timestamp = timestamp;
            this.text = text;
        }

        @Override
        public String toString() {
            return "[" + id + "] " + sender + " (" + timestamp + "): " + text;
        }
    }

    // ============================================================
    // COMPARISON COUNTER
    // ============================================================

    static class Counter {
        long count = 0;
    }

    // ============================================================
    // 1. NAIVE STRING MATCHING
    // ============================================================

    static int naiveSearch(String text, String pattern, Counter counter) {

        int n = text.length();
        int m = pattern.length();

        if (m > n) {
            return -1;
        }

        for (int i = 0; i <= n - m; i++) {

            int j = 0;

            while (j < m) {

                counter.count++;

                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }

                j++;
            }

            if (j == m) {
                return i;
            }
        }

        return -1;
    }

    // ============================================================
    // 2. KMP
    // ============================================================

    static int[] buildLPS(String pattern) {

        int[] lps = new int[pattern.length()];

        int len = 0;
        int i = 1;

        while (i < pattern.length()) {

            if (pattern.charAt(i) == pattern.charAt(len)) {

                len++;
                lps[i] = len;
                i++;

            } else {

                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    static int kmpSearch(String text, String pattern, Counter counter) {

        int n = text.length();
        int m = pattern.length();

        if (m > n) {
            return -1;
        }

        int[] lps = buildLPS(pattern);

        int i = 0;
        int j = 0;

        while (i < n) {

            counter.count++;

            if (text.charAt(i) == pattern.charAt(j)) {

                i++;
                j++;

                if (j == m) {
                    return i - j;
                }

            } else {

                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1;
    }

    // ============================================================
    // 3. RABIN-KARP
    // ============================================================

    static int rabinKarpSearch(
            String text,
            String pattern,
            Counter counter) {

        int n = text.length();
        int m = pattern.length();

        if (m > n) {
            return -1;
        }

        int base = 256;
        int prime = 101;

        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Calculate h = base^(m-1)
        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % prime;
        }

        // Calculate initial hash values
        for (int i = 0; i < m; i++) {

            patternHash =
                    (base * patternHash + pattern.charAt(i))
                            % prime;

            textHash =
                    (base * textHash + text.charAt(i))
                            % prime;
        }

        // Search
        for (int i = 0; i <= n - m; i++) {

            // Hash values match
            if (patternHash == textHash) {

                int j = 0;

                while (j < m) {

                    counter.count++;

                    if (text.charAt(i + j)
                            != pattern.charAt(j)) {
                        break;
                    }

                    j++;
                }

                if (j == m) {
                    return i;
                }
            }

            // Calculate next window hash
            if (i < n - m) {

                textHash =
                        (base *
                                (textHash
                                        - text.charAt(i) * h)
                                + text.charAt(i + m))
                                % prime;

                if (textHash < 0) {
                    textHash += prime;
                }
            }
        }

        return -1;
    }

    // ============================================================
    // 4. Z ALGORITHM
    // ============================================================

    static int zSearch(
            String text,
            String pattern,
            Counter counter) {

        String combined = pattern + "$" + text;

        int n = combined.length();

        int[] z = new int[n];

        int left = 0;
        int right = 0;

        for (int i = 1; i < n; i++) {

            if (i <= right) {
                z[i] = Math.min(
                        right - i + 1,
                        z[i - left]
                );
            }

            while (i + z[i] < n
                    && combined.charAt(z[i])
                    == combined.charAt(i + z[i])) {

                counter.count++;

                z[i]++;
            }

            if (i + z[i] - 1 > right) {

                left = i;
                right = i + z[i] - 1;
            }

            // Pattern found
            if (z[i] == pattern.length()) {

                return i - pattern.length() - 1;
            }
        }

        return -1;
    }

    // ============================================================
    // 5. AHO-CORASICK
    // ============================================================

    static class ACNode {

        Map<Character, ACNode> children =
                new HashMap<>();

        ACNode failure;

        boolean isEnd;
    }

    static int ahoCorasickSearch(
            String text,
            String pattern,
            Counter counter) {

        ACNode root = new ACNode();

        ACNode current = root;

        // --------------------------------------------------------
        // Build Trie
        // --------------------------------------------------------

        for (char c : pattern.toCharArray()) {

            if (!current.children.containsKey(c)) {

                current.children.put(
                        c,
                        new ACNode()
                );
            }

            current = current.children.get(c);
        }

        current.isEnd = true;

        // --------------------------------------------------------
        // Build Failure Links
        // --------------------------------------------------------

        Queue<ACNode> queue =
                new LinkedList<>();

        for (ACNode node :
                root.children.values()) {

            node.failure = root;

            queue.add(node);
        }

        while (!queue.isEmpty()) {

            ACNode node = queue.poll();

            for (Map.Entry<Character, ACNode> entry :
                    node.children.entrySet()) {

                char c = entry.getKey();

                ACNode child = entry.getValue();

                ACNode failureNode =
                        node.failure;

                while (failureNode != root
                        && !failureNode.children.containsKey(c)) {

                    failureNode =
                            failureNode.failure;
                }

                if (failureNode.children.containsKey(c)
                        && failureNode.children.get(c) != child) {

                    child.failure =
                            failureNode.children.get(c);

                } else {

                    child.failure = root;
                }

                queue.add(child);
            }
        }

        // --------------------------------------------------------
        // Search Text
        // --------------------------------------------------------

        current = root;

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            counter.count++;

            while (current != root
                    && !current.children.containsKey(c)) {

                current = current.failure;
            }

            if (current.children.containsKey(c)) {

                current =
                        current.children.get(c);
            }

            if (current.isEnd) {

                return i - pattern.length() + 1;
            }
        }

        return -1;
    }

    // ============================================================
    // RUN ONE ALGORITHM
    // ============================================================

    static void searchMessages(
            ArrayList<Message> messages,
            String pattern,
            int choice) {

        String algorithmName = "";

        switch (choice) {

            case 1:
                algorithmName = "Naive String Matching";
                break;

            case 2:
                algorithmName = "Knuth-Morris-Pratt (KMP)";
                break;

            case 3:
                algorithmName = "Rabin-Karp";
                break;

            case 4:
                algorithmName = "Z Algorithm";
                break;

            case 5:
                algorithmName = "Aho-Corasick";
                break;
        }

        System.out.println();
        System.out.println(
                "=================================================="
        );

        System.out.println("                SEARCH RESULTS");

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "Algorithm : " + algorithmName
        );

        System.out.println(
                "Pattern   : " + pattern
        );

        int matches = 0;

        long totalComparisons = 0;

        long startTime = System.nanoTime();

        for (Message message : messages) {

            String text =
                    message.text.toLowerCase();

            String searchPattern =
                    pattern.toLowerCase();

            Counter counter =
                    new Counter();

            int position = -1;

            switch (choice) {

                case 1:

                    position =
                            naiveSearch(
                                    text,
                                    searchPattern,
                                    counter
                            );

                    break;

                case 2:

                    position =
                            kmpSearch(
                                    text,
                                    searchPattern,
                                    counter
                            );

                    break;

                case 3:

                    position =
                            rabinKarpSearch(
                                    text,
                                    searchPattern,
                                    counter
                            );

                    break;

                case 4:

                    position =
                            zSearch(
                                    text,
                                    searchPattern,
                                    counter
                            );

                    break;

                case 5:

                    position =
                            ahoCorasickSearch(
                                    text,
                                    searchPattern,
                                    counter
                            );

                    break;
            }

            totalComparisons += counter.count;

            if (position != -1) {

                matches++;

                System.out.println();

                System.out.println("MATCH FOUND");

                System.out.println(message);

                System.out.println(
                        "Position    : " + position
                );

                System.out.println(
                        "Comparisons : " + counter.count
                );
            }
        }

        long endTime = System.nanoTime();

        double executionTime =
                (endTime - startTime) / 1_000_000.0;

        System.out.println();

        System.out.println(
                "--------------------------------------------------"
        );

        System.out.println(
                "Messages Searched : "
                        + messages.size()
        );

        System.out.println(
                "Messages Found    : "
                        + matches
        );

        System.out.println(
                "Total Comparisons : "
                        + totalComparisons
        );

        System.out.printf(
                "Execution Time    : %.4f ms%n",
                executionTime
        );

        System.out.println(
                "=================================================="
        );
    }

    // ============================================================
    // COMPARE ALL ALGORITHMS
    // ============================================================

    static void compareAllAlgorithms(
            ArrayList<Message> messages,
            String pattern) {

        String[] names = {
                "Naive",
                "KMP",
                "Rabin-Karp",
                "Z Algorithm",
                "Aho-Corasick"
        };

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                 ALGORITHM PERFORMANCE"
        );

        System.out.println(
                "================================================================"
        );

        System.out.println(
                String.format(
                        "%-20s %-10s %-15s %-15s",
                        "Algorithm",
                        "Matches",
                        "Comparisons",
                        "Time (ms)"
                )
        );

        System.out.println(
                "----------------------------------------------------------------"
        );

        for (int algorithm = 1;
             algorithm <= 5;
             algorithm++) {

            int matches = 0;

            long totalComparisons = 0;

            long startTime =
                    System.nanoTime();

            for (Message message : messages) {

                String text =
                        message.text.toLowerCase();

                String searchPattern =
                        pattern.toLowerCase();

                Counter counter =
                        new Counter();

                int position = -1;

                switch (algorithm) {

                    case 1:

                        position =
                                naiveSearch(
                                        text,
                                        searchPattern,
                                        counter
                                );

                        break;

                    case 2:

                        position =
                                kmpSearch(
                                        text,
                                        searchPattern,
                                        counter
                                );

                        break;

                    case 3:

                        position =
                                rabinKarpSearch(
                                        text,
                                        searchPattern,
                                        counter
                                );

                        break;

                    case 4:

                        position =
                                zSearch(
                                        text,
                                        searchPattern,
                                        counter
                                );

                        break;

                    case 5:

                        position =
                                ahoCorasickSearch(
                                        text,
                                        searchPattern,
                                        counter
                                );

                        break;
                }

                if (position != -1) {
                    matches++;
                }

                totalComparisons +=
                        counter.count;
            }

            long endTime =
                    System.nanoTime();

            double time =
                    (endTime - startTime)
                            / 1_000_000.0;

            System.out.println(
                    String.format(
                            "%-20s %-10d %-15d %-15.4f",
                            names[algorithm - 1],
                            matches,
                            totalComparisons,
                            time
                    )
            );
        }

        System.out.println(
                "================================================================"
        );
    }

    // ============================================================
    // MAIN METHOD
    // ============================================================

    public static void main(String[] args) {

        Scanner sc =
                new Scanner(System.in);

        // --------------------------------------------------------
        // CHAT DATASET
        // --------------------------------------------------------

        ArrayList<Message> messages =
                new ArrayList<>();

        messages.add(
                new Message(
                        1,
                        "Akshay",
                        "09:15",
                        "Hey team, shall we discuss the machine learning project?"
                )
        );

        messages.add(
                new Message(
                        2,
                        "Manoj",
                        "09:18",
                        "Yes, I completed the data preprocessing part."
                )
        );

        messages.add(
                new Message(
                        3,
                        "Pranith",
                        "09:22",
                        "The machine learning model is giving good accuracy."
                )
        );

        messages.add(
                new Message(
                        4,
                        "Balaji",
                        "09:30",
                        "I will work on the database integration."
                )
        );

        messages.add(
                new Message(
                        5,
                        "Sai Charan",
                        "09:35",
                        "We should compare all the algorithms."
                )
        );

        messages.add(
                new Message(
                        6,
                        "Akshay",
                        "10:00",
                        "KMP and Rabin Karp are useful for pattern matching."
                )
        );

        messages.add(
                new Message(
                        7,
                        "Manoj",
                        "10:10",
                        "The chat message search system is almost complete."
                )
        );

        messages.add(
                new Message(
                        8,
                        "Pranith",
                        "10:20",
                        "Let's test the search system with different patterns."
                )
        );

        messages.add(
                new Message(
                        9,
                        "Balaji",
                        "10:30",
                        "The algorithms should be compared using execution time."
                )
        );

        messages.add(
                new Message(
                        10,
                        "Sai Charan",
                        "10:40",
                        "Our DSA project demonstrates efficient string searching."
                )
        );

        // --------------------------------------------------------
        // MENU
        // --------------------------------------------------------

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "             CHAT MESSAGE SEARCH SYSTEM"
        );

        System.out.println(
                "=================================================="
        );

        System.out.println();

        System.out.println(
                "Available Algorithms:"
        );

        System.out.println(
                "1. Naive String Matching"
        );

        System.out.println(
                "2. Knuth-Morris-Pratt (KMP)"
        );

        System.out.println(
                "3. Rabin-Karp"
        );

        System.out.println(
                "4. Z Algorithm"
        );

        System.out.println(
                "5. Aho-Corasick"
        );

        System.out.println(
                "6. Compare All Algorithms"
        );

        System.out.println();

        System.out.print(
                "Enter search pattern: "
        );

        String pattern =
                sc.nextLine();

        System.out.print(
                "Select option (1-6): "
        );

        int choice =
                sc.nextInt();

        if (choice >= 1 && choice <= 5) {

            searchMessages(
                    messages,
                    pattern,
                    choice
            );

        } else if (choice == 6) {

            compareAllAlgorithms(
                    messages,
                    pattern
            );

        } else {

            System.out.println(
                    "Invalid option."
            );
        }

        sc.close();
    }
}