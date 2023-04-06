import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Employees {

    public static void main(String[] args) {
        //CSV file path
        String fileName = "C:\\Users\\iveli\\IdeaProjects\\Employees\\testInput.csv";
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] row = line.split(", ");
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate working periods for each pair of employees
        Map<String, Long> periods = new HashMap<>();
        for (int i = 0; i < data.size() - 1; i++) {
            String[] row1 = data.get(i);
            for (int j = i + 1; j < data.size(); j++) {
                String[] row2 = data.get(j);
                if (!row1[1].equals(row2[1])) {
                    continue;
                }
                LocalDate dateFrom1 = LocalDate.parse(row1[2]);
                LocalDate dateTo1 = row1[3].equals("NULL") ? LocalDate.now() : LocalDate.parse(row1[3]);
                LocalDate dateFrom2 = LocalDate.parse(row2[2]);
                LocalDate dateTo2 = row2[3].equals("NULL") ? LocalDate.now() : LocalDate.parse(row2[3]);
                if (dateTo1.isBefore(dateFrom2) || dateTo2.isBefore(dateFrom1)) {
                    continue;
                }
                long period = ChronoUnit.DAYS.between(
                        dateFrom1.isBefore(dateFrom2) ? dateFrom2 : dateFrom1,
                        dateTo1.isBefore(dateTo2) ? dateTo1 : dateTo2);
                String key;
                if (row1[0].compareTo(row2[0]) < 0) {
                    key = row1[0] + "," + row2[0];
                } else key = row2[0] + "," + row1[0];
                //String key = row1[0] + "," + row2[0] + " " + row1[1];
                periods.put(key, periods.getOrDefault(key, 0L) + period);
            }
        }

        // Find the pair of employees with the longest working period
        long maxPeriod = 0;
        String maxPair = null;
        for (Map.Entry<String, Long> entry : periods.entrySet()) {
            if (entry.getValue() > maxPeriod) {
                maxPeriod = entry.getValue();
                maxPair = entry.getKey();
            }
        }

        // Output the result (employee1, employee2, daysOfWorkingTogether)
        if (maxPair != null) {
            String[] pair = maxPair.split(",");
            System.out.printf("%s, %s, %d", pair[0], pair[1], maxPeriod);
        } else {
            System.out.println("No common projects found.");
        }
    }
}
