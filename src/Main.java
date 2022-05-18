import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            System.exit(-args.length);
        List<BayesData> trainingSet = readFromFile(args[0]);
        List<BayesData> testSet = readFromFile(args[1]);
        learn(trainingSet);
        test(testSet);
    }

    private static void learn(List<BayesData> trainingSet) {
        trainingSet.forEach(System.out::println);
    }

    private static void test(List<BayesData> testSet) {
        testSet.forEach(System.out::println);
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

    private static double proportion(double dividend, double divisor) {
        return dividend == 0 ? (dividend + 1 / divisor + 1) : dividend / divisor;
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
    private final Outlook outlook;
    private final Temperature temperature;
    private final Humidity humidity;
    private final boolean windy;
    private final Boolean play;

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

    public String toString() {
        return outlook.toString() + ", " +
                temperature.toString() + ", " +
                humidity.toString() + ", " +
                windy + ", " + play;
    }
}