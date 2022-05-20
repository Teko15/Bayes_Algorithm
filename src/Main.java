import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            System.exit(-args.length);
        List<BayesData> testSet = readFromFile(args[0]);
        List<BayesData> trainingSet = readFromFile(args[1]);
        learn(trainingSet, testSet);
        testSet.forEach(System.out::println);
    }

    private static void learn(List<BayesData> trainingSet, List<BayesData> testSet) {
        double pYes, pNo;
        for (BayesData bayesData : testSet) {

            double yes = trainingSet.stream().filter(BayesData::getPlay).count();
            double no = trainingSet.size() - yes;

            //YES
            pYes = yes / trainingSet.size();
            //Outlook
            pYes *= proportion(trainingSet.stream()
                    .filter(e -> e.getPlay() && e.getOutlook().equals(bayesData.getOutlook()))
                    .count(), yes, Outlook.values().length);
            //Temperature
            pYes *= proportion(trainingSet.stream()
                    .filter(e -> e.getPlay() && e.getTemperature().equals(bayesData.getTemperature()))
                    .count(), yes, Temperature.values().length);
            //Humidity
            pYes *= proportion(trainingSet.stream()
                    .filter(e -> e.getPlay() && e.getHumidity().equals(bayesData.getHumidity()))
                    .count(), yes, Humidity.values().length);
            //Windy
            pYes *= proportion(trainingSet.stream()
                    .filter(e -> e.getPlay() && e.isWindy() == bayesData.isWindy())
                    .count(), yes, 2.0);

            //NO
            pNo = no / trainingSet.size();
            //Outlook
            pNo *= proportion(trainingSet.stream()
                    .filter(e -> !e.getPlay() && e.getOutlook().equals(bayesData.getOutlook()))
                    .count(), no, Outlook.values().length);
            //Temperature
            pNo *= proportion(trainingSet.stream()
                    .filter(e -> !e.getPlay() && e.getTemperature().equals(bayesData.getTemperature()))
                    .count(), no, Temperature.values().length);
            //Humidity
            pNo *= proportion(trainingSet.stream()
                    .filter(e -> !e.getPlay() && e.getHumidity().equals(bayesData.getHumidity()))
                    .count(), no, Humidity.values().length);
            //Windy
            pNo *= proportion(trainingSet.stream()
                    .filter(e -> !e.getPlay() && e.isWindy() == bayesData.isWindy())
                    .count(), no, 2.0);

            bayesData.setPYes(pYes);
            bayesData.setPNo(pNo);
            bayesData.setPlay(pYes > pNo);
        }
    }

    private static List<BayesData> readFromFile(String path) throws IOException {
        List<BayesData> returner = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            returner.add(bayesData(line.split(",")));
        }
        return returner;
    }

    private static double proportion(double dividend, double divisor, double enumLength) {
        return dividend == 0 ? (1 / (divisor + enumLength)) : dividend / divisor;
    }

    private static BayesData bayesData(String[] singleData) {

        Outlook outlook = Outlook.valueOf(singleData[0]);
        boolean windy = singleData[1].equals("tak");
        Temperature temperature = Temperature.valueOf(singleData[2]);
        Humidity humidity = Humidity.valueOf(singleData[3]);
        if (singleData.length == 4)
            return new BayesData(outlook, temperature, humidity, windy);
        else {
            boolean answer = singleData[4].equals("tak");
            return new BayesData(outlook, temperature, humidity, windy, answer);
        }
    }


}

class BayesData {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private final Outlook outlook;
    private final Temperature temperature;
    private final Humidity humidity;
    private final boolean windy;
    private Boolean play;
    private double pYes;
    private double pNo;

    public BayesData(Outlook outlook, Temperature temperature, Humidity humidity, boolean windy) {
        this.outlook = outlook;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windy = windy;
        this.play = null;
    }

    public BayesData(Outlook outlook, Temperature temperature, Humidity humidity, boolean windy, boolean play) {
        this.outlook = outlook;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windy = windy;
        this.play = play;
    }

    public void setPYes(double pYes) {
        this.pYes = pYes;
    }

    public void setPNo(double pNo) {
        this.pNo = pNo;
    }

    public void setPlay(Boolean play) {
        this.play = play;
    }

    public Outlook getOutlook() {
        return outlook;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Humidity getHumidity() {
        return humidity;
    }

    public boolean isWindy() {
        return windy;
    }

    public boolean getPlay() {
        return play;
    }

    public String toString() {
        return outlook.toString() + ", " +
                temperature.toString() + ", " +
                humidity.toString() + ", " +
                windy + ". " +
                GREEN + "Yes - " + FORMAT.format(pYes * 1000) + '‰' + RESET + "; " +
                RED + "No - " + FORMAT.format(pNo * 1000) + '‰' + RESET +
                ". Result - " + (play? GREEN + true + RESET: RED + false + RESET);
    }
}