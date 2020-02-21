import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        int[] totalDistance = new int[nouns.length];
        for (int i = 0; i < nouns.length; ++i)
            for (int j = i + 1; j < nouns.length; ++j) {
                int distance = wordNet.distance(nouns[i], nouns[j]);
                totalDistance[i] += distance;
                totalDistance[j] += distance;
            }
        int maxDist = 0;
        String outcast = null;
        for (int i = 0; i < totalDistance.length; ++i)
            if (totalDistance[i] > maxDist) {
                maxDist = totalDistance[i];
                outcast = nouns[i];
            }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}