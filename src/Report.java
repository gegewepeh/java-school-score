import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Report {
    Map<String, ArrayList<Integer>> mappedByClass = null;
    List<Integer> rawScoreList = null;
    List<Integer> sortedScoreList = null;
    Map<String, Integer> groupByScoreMap = new HashMap<String, Integer>();
    String groupedByScoreResult = null;
    String meanMedianModeResult = null;
    Double mean = null;
    Double median = null;
    Integer mode = null;
    Integer spaceSuffix = 14;
    Integer spacePrefix = 4;

    public Report(Map<String, ArrayList<Integer>> hm, List<Integer> rawScoreList) {
        this.mappedByClass = hm;
        this.rawScoreList = rawScoreList;
    }

    public void groupByScore(String option) {
        System.out.println(option);
        this.mappedByClass.forEach((key, value) ->
        {
            for (int val : value) {
                String replaceKey = Integer.toString(val);
                if (val < 6 && option.equals("<6")){
                    replaceKey = "kurang dari 6";

                }
                groupByScoreMap.merge(replaceKey, 1, (oldVal, newVal) -> oldVal + 1);
            }
        });
    }

    public void findMean() throws NoSuchElementException {
        Double average = this.rawScoreList.stream().mapToDouble(a -> a).average().getAsDouble();
        this.mean = Double.valueOf(String.format("%.2f", average));
    }

    public void findMedian() {
        this.sortList();
        Collections.sort(this.sortedScoreList);
        int listSize = this.sortedScoreList.size();
        int midPointIndex = (listSize / 2) - 1;
        if (listSize % 2 == 0) {
            Double medianVal1 = Double.valueOf(this.sortedScoreList.get(midPointIndex));
            Double medianVal2 = Double.valueOf(this.sortedScoreList.get(midPointIndex + 1));
            this.median = (medianVal1 + medianVal2) / 2;
        } else {
            this.median = Double.valueOf(this.sortedScoreList.get(midPointIndex + 1));
        }
    }

    public void findMode() {
        this.groupByScoreMap = new HashMap<String, Integer>();
        this.groupByScore("");
        int max = 0;
        String mode = "0";
        for (Map.Entry<String, Integer> set : this.groupByScoreMap.entrySet()) {
            if (set.getValue() > max) {
                max = set.getValue();
                mode = set.getKey();
            }
        }

        this.mode = Integer.parseInt(mode);
    }

    private void sortList() {
        if (this.sortedScoreList == null) {
            this.sortedScoreList = new ArrayList<>(this.rawScoreList);
        }
    }

    private static String title() {
        return "Berikut Hasil Pengolahan Nilai:\n";
    }

    private static String generateSpace(int spaceAmount) {
        if (spaceAmount <= 0) {
            spaceAmount = 0;
        }
        StringBuilder spaces = new StringBuilder();
        spaces.append(" ".repeat(spaceAmount));
        return spaces.toString();
    }

    public String produceGroupByScore() {
        this.groupByScore("<6");
        String stringNilai = "Nilai";
        this.groupedByScoreResult = title() + "\n";
        this.groupedByScoreResult += "Nilai" + generateSpace(this.spaceSuffix - stringNilai.length()) + "|"
                + generateSpace(this.spacePrefix) + "Frekuensi" + "\n";
        this.groupByScoreMap.forEach((key, value) ->
                {
                    this.groupedByScoreResult += key + generateSpace(this.spaceSuffix - key.length())
                            + "|" + generateSpace(this.spacePrefix) + value + "\n";
                }
        );
        return this.groupedByScoreResult;
    }

    public String produceMeanMedianMode() throws NoSuchElementException {
        this.findMean();
        this.findMedian();
        this.findMode();

        this.meanMedianModeResult = title() + "\n";
        this.meanMedianModeResult += "Berikut hasil sebaran data nilai\n";
        this.meanMedianModeResult += String.format("""
                Mean: %s
                Median: %s
                Modus: %s""", this.mean, this.median, this.mode);

        return this.meanMedianModeResult;
    }
}
