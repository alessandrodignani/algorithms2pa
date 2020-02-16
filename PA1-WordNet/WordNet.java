import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import java.util.ArrayList;

public class WordNet {
    private final SeparateChainingHashST<String, ArrayList<Integer>> word2id;
    private final SeparateChainingHashST<Integer, String> id2word;
    private int V;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("File name is null.");
        }

        word2id = new SeparateChainingHashST<String, ArrayList<Integer>>();
        id2word = new SeparateChainingHashST<Integer, String>();
        V = 0;
        addSynsets(synsets);
        addHypernyms(hypernyms);
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println(args[0] + " " + args[1]);
            WordNet wordNet = new WordNet(args[0], args[1]);
            String wordA = "wire";
            String wordB = "wheel";
            System.out.println("distance: " + wordNet.distance(wordA, wordB));
            System.out.println("shortest ancestral path: " + wordNet.sap(wordA, wordB));
        }
    }

    private void addSynsets(String synsets) {
        In in = new In(synsets);
        String line;
        while ((line = in.readLine()) != null) {
            String[] synsetArray = line.split(",");
            if (synsetArray.length < 2) continue;
            int id = Integer.parseInt(synsetArray[0]);
            id2word.put(id, synsetArray[1]);
            for (String noun : synsetArray[1].split(" ")) {
                ArrayList<Integer> ids = word2id.get(noun);
                if (ids != null) ids.add(id);
                else {
                    ArrayList<Integer> nids = new ArrayList<Integer>();
                    nids.add(id);
                    word2id.put(noun, nids);
                }
            }
            V++;
        }
    }

    private void addHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        String line;
        Digraph digraph = new Digraph(V);
        while ((line = in.readLine()) != null) {
            String[] strs = line.split(",");
            if (strs.length < 2) continue;
            int start = Integer.parseInt(strs[0]);
            for (int i = 1; i < strs.length; i++) {
                digraph.addEdge(start, Integer.parseInt(strs[i]));
            }
        }

        // Check cycles
        DirectedCycle dc = new DirectedCycle(digraph);
        if (dc.hasCycle()) throw new IllegalArgumentException("Not a DAG: cycle detected " + dc.cycle().toString());

        // Check multiple roots
        boolean rootAlreadyFound = false;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) {
                if (rootAlreadyFound) throw new IllegalArgumentException("Found more than 1 root");
                rootAlreadyFound = true;
            }
        }
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return word2id.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("word is null.");
        return word2id.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkInput(nounA, nounB);

        return sap.length(word2id.get(nounA), word2id.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkInput(nounA, nounB);

        return id2word.get(sap.ancestor(word2id.get(nounA), word2id.get(nounB)));
    }

    private void checkInput(String nounA, String nounB) {
        if (!isNoun(nounA)) throw new IllegalArgumentException("\"" + nounA + "\" is not a WordNet noun.");
        if (!isNoun(nounB)) throw new IllegalArgumentException("\"" + nounB + "\" is not a WordNet noun.");
    }
}