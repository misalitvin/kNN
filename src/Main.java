import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter K hyperparameter");
        int k = scanner.nextInt();
        System.out.println("Enter path to the training file");
        String trainingfile = scanner.nextLine();
        trainingfile = scanner.nextLine();
        System.out.println("Enter path to the test file");
        String testfile = scanner.nextLine();

        int correctansw = 0, allanswers = 0;

        String line;
        int n=0,v=0;
        try (BufferedReader br = new BufferedReader(new FileReader(trainingfile))) {
            while ((line = br.readLine()) != null) {
                if(n==0){
                    String[] row = line.split(",");
                    v=row.length-1;
                }
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] types = new String[n];
        double[][] vectors = new double[n][v];
        n=0;
        try (BufferedReader br = new BufferedReader(new FileReader(trainingfile))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                for(int i=0;i<v;i++){
                    vectors[n][i]=Double.parseDouble(row[i]);
                }
                types[n] = row[row.length-1];
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(testfile))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                double[] values = new double[v];
                for(int i=0;i<v;i++){
                    values[i]=Double.parseDouble(row[i]);
                }
                int[] indexes = indexes(vectors,values,k);

                Map<String, Integer> origintypes = new HashMap<>();
                for (String value : types) {
                    if (!origintypes.containsKey(value)) {
                        origintypes.put(value, 0);
                    }
                }

                for(int i = 0;i<indexes.length;i++){
                    for (Map.Entry<String, Integer> entry : origintypes.entrySet()) {
                        if(types[indexes[i]].equals(entry.getKey())){
                            entry.setValue(entry.getValue() + 1);
                        }
                    }
                }

                String otvet = null;
                int maxValue = 0;
                int countMaxValue = 0;
                Random random = new Random();
                for (Map.Entry<String, Integer> entry : origintypes.entrySet()) {
                    if (entry.getValue() > maxValue) {
                        otvet = entry.getKey();
                        maxValue = entry.getValue();
                        countMaxValue = 1;
                    } else if (entry.getValue() == maxValue) {
                        countMaxValue++;
                        if (random.nextInt(countMaxValue) == 0) {
                            otvet = entry.getKey();
                        }
                    }
                }

                if(otvet.equals(row[row.length-1])) correctansw++;
                allanswers++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println((double)correctansw/allanswers);
    }
    public static int[] indexes(double[][] training, double[] test,int k){
        double[][] lengths = new double[k][2];
        for(int i=0;i< training.length;i++){
            double length = 0;
            for(int j = 0;j<training[0].length;j++){
                length += Math.pow(test[j]-training[i][j],2);
            }
            if(i<k){
                lengths[i][1] = length;
                lengths[i][0] = i;
            }else if(lengths[0][1]>length){
                lengths[0][1] = length;
                lengths[0][0] = i;
                double max = lengths[0][1];
                int ind = 0;
                for(int m = 0;m<k;m++){
                    if(max<lengths[m][1]){
                        max = lengths[m][1];
                        ind = m;
                    }
                }
                max = lengths[0][1];
                lengths[0][1] = lengths[ind][1];
                lengths[ind][1] = max;
                max = lengths[0][0];
                lengths[0][0] = lengths[ind][0];
                lengths[ind][0] = max;
            }
        }
        int[] indexes = new int[k];
        for(int i = 0;i< lengths.length;i++){
            indexes[i] = (int)lengths[i][0];
        }
        return indexes;
    }
}