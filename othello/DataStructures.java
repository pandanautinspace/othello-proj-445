package othello;

import java.util.HashMap;
import java.util.Map;

public class DataStructures {
}

class BoardTuple {
    public Board b;
    public int[] move;
    public BoardTuple(Board b, int[] move) {
        this.b = b;
        this.move = move;
    }
    public BoardTuple(Board b, int[] move, double alpha, double beta) {
        this.b = b;
        this.move = move;
    }
}

class ScoreTuple {
    public double score;
    public double alpha;
    public double beta;
    public ScoreTuple(double score, double alpha, double beta) {
        this.score = score;
        this.alpha = alpha;
        this.beta = beta;
    }

    public ScoreTuple(double score) {
        this.score = score;
        this.alpha = -Double.MAX_VALUE;
        this.beta = Double.MAX_VALUE;
    }
}

class LookupTable {
    public Map<String, ScoreTuple> map;
    public LookupTable() {
        map = new HashMap<>();
    }
    public void set(String key, ScoreTuple value) {
        if(map.containsKey(key)) {
            ScoreTuple s = map.get(key);
            s.alpha = Math.min(value.alpha, s.alpha);
            s.beta = Math.max(value.beta, s.beta);
        }
        map.put(key, value);
    }

    public ScoreTuple get(String key) {
        return map.get(key);
    }

    public ScoreTuple getOrDefault(String key, ScoreTuple def) {
        return map.getOrDefault(key, def);
    }

    public ScoreTuple get(String key, double alpha, double beta) {
        ScoreTuple retrieve = map.get(key);
        if(retrieve.alpha <= alpha && retrieve.beta >= beta) {
            return retrieve;
        }
        return null;
    }
    public ScoreTuple getOrDefault(String key, double alpha, double beta, ScoreTuple value) {
        ScoreTuple retrieve = map.getOrDefault(key, value);
        if(retrieve.alpha <= alpha && retrieve.beta >= beta) {
            return retrieve;
        }
        return value;
    }
}