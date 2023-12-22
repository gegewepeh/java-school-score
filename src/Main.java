import java.io.*;
import java.util.*;

public class Main {
    boolean isActive = true;
    String currentMenu = "main";

    public static void main(String[] args) {
        Main schoolApp = new Main();
        schoolApp.menuLoop(false);
    }

    private void menuLoop(boolean isRetry) {
        String filePath = "./";
        String file = "data_sekolah.csv";
        if (!isRetry) {
            printMainMenu(filePath, file);
        } else {
            Scanner keyboard = new Scanner(System.in);
            String input = keyboard.nextLine();
            switch (input) {
                case "0" -> this.isActive = false;
                default -> {
                    this.currentMenu = changeMenu(this.currentMenu, filePath, file, null);
                }
            }
        }

        try {
            while (this.isActive) {
                Scanner keyboard = new Scanner(System.in);
                String input = keyboard.nextLine();

                switch (input) {
                    case "1", "2", "3" -> {
                        exec(input, filePath, file);
                        this.currentMenu = changeMenu(this.currentMenu, filePath, file, null);
                        this.menuLoop(true);
                    }
                    case "0" -> this.isActive = false;
                    default -> printMainMenu(filePath, file);
                }
            }
        } catch (FileNotFoundException e) {
            this.currentMenu = changeMenu(this.currentMenu, filePath, file, "fileNotFound");
            this.menuLoop(true);
        } catch (NoSuchElementException e) {
            this.currentMenu = changeMenu(this.currentMenu, filePath, file, "noSuchElement");
            this.menuLoop(true);
        }
    }

    static String changeMenu(String currentMenu, String filePath, String file, String errStatus) {
        clearConsole();
        String nextMenu = null;
        if (errStatus != null) {
            nextMenu = "second";
            if (errStatus.equals("fileNotFound")) {
                System.out.println(fileNotFound());
            } else if (errStatus.equals("noSuchElement")) {
                System.out.println(fileBrokenorEmpty());
            }
            return nextMenu;
        }
        if (currentMenu.equals("main")) {
            nextMenu = "second";
            System.out.println(successScreen(filePath + file));
        } else {
            nextMenu = "main";
            printMainMenu(filePath, file);
        }
        return nextMenu;
    }

    static void printMainMenu(String filePath, String file) {
        System.out.println(menu(filePath + file));
    }

    static String title() {
        return """
                -------------------------------------------------------------
                Aplikasi Pengolah Nilai Siswa
                -------------------------------------------------------------
                """;
    }

    static void exec(String option, String filePath, String file) throws FileNotFoundException, NoSuchElementException {
        String result = null;
        Scanner sc = new Scanner(new File(filePath + file));
        sc.useDelimiter(",");

        HashMap<String, ArrayList<Integer>> scoreHashMap = new HashMap<String, ArrayList<Integer>>();
        List<Integer> rawScoreList = new ArrayList<>();

        while (sc.hasNext()) {
            String data = sc.nextLine();
            StringTokenizer tokenized = new StringTokenizer(data, ";");

            ArrayList<Integer> list = new ArrayList<Integer>();
            int index = 0;
            String key = null;
            while (tokenized.hasMoreTokens()) {
                if (index == 0) {
                    key = tokenized.nextToken();
                    scoreHashMap.put(key, new ArrayList<>());
                } else {
                    String nextToken = tokenized.nextToken();
                    rawScoreList.add(Integer.parseInt(nextToken));
                    list.add(Integer.parseInt(nextToken));
                }
                index++;
            }
            scoreHashMap.put(key, list);
        }
        sc.close();

        Report testReport = new Report(scoreHashMap, rawScoreList);
        switch (option) {
            case "1" -> {
                result = testReport.produceGroupByScore();
                save(filePath + "data_sekolah_modus.txt", result);
            }
            case "2" -> {
                result = testReport.produceMeanMedianMode();
                save(filePath + "data_sekolah_modus_median.txt", result);
            }
            case "3" -> {
                String result1 = testReport.produceGroupByScore();
                String result2 = testReport.produceMeanMedianMode();
                save(filePath + "data_sekolah_modus.txt", result1);
                save(filePath + "data_sekolah_modus_median.txt", result2);
            }
        }
    }

    static void save(String savePath, String data) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(savePath));
        pw.print(data);
        pw.close();
    }

    static String menu(String filePath) {
        String title = title();
        return title + """
                Letakkan file csv dengan nama file data_sekolah di direktori
                berikut:\s""" + filePath + """
                \n
                pilih menu:
                1. Generate txt untuk menampilkan modus
                2. Generate txt untuk menampilkan nilai rata-rata, median
                3. Generate kedua file
                0. Exit""";
    }

    static String fileNotFound() {
        String title = title();
        return title + """
                File tidak ditemukan
                                
                0. Exit
                1. Kembali""";
    }

    static String fileBrokenorEmpty() {
        String title = title();
        return title + """
                File kosong atau rusak
                                
                0. Exit
                1. Kembali""";
    }

    static String successScreen(String filePath) {
        String title = title();
        return title + """
                File telah di generate di\s""" + filePath + """
                                
                silahkan cek
                                
                0. Exit
                1. Kembali ke menu utama""";
    }

    static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
