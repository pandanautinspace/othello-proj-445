package othello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class GiraffeV1NoCache extends AIPlayer {
    private LookupTable maxStore = new LookupTable();
    private LookupTable minStore = new LookupTable();
    private Map<String, ArrayList<int[]>> childCache = new HashMap<>();
    @Override
    public String getName() {
        return "Sarah Connor v1N";
    }

    long numberExplored = 0;

    @Override
    public void getNextMove(Board board, int[] bestMove) throws IllegalCellException, IllegalMoveException {
//        String repr = BoardImplementation.getBoardString(board);
//        board = new BitBoardImplementation();
//        ((BitBoardImplementation)board).assignFromString(repr);
        long[] numExplored = {0};
        GreedyPlayer g = new GreedyPlayer();
        g.getNextMove(board, bestMove);
//        System.out.printf("Best Move: %d %d\n", bestMove[0], bestMove[1]);
        try {
//            throw new InterruptedException();
            int i;
            for(i = 1; i < 10; i++) {
                minimax(board, i, true, bestMove, numExplored);
//                System.out.println("Got to depth = " + i);
            }
            System.out.println("Print best move is: " + bestMove[0] + " " + bestMove[1]);
//            System.out.println("maxStore length = " + maxStore.keySet().size());;
//            System.out.printf("Best Move: %d %d\n", bestMove[0], bestMove[1]);
            numberExplored += numExplored[0];
            System.out.println("numberExplored = " + numberExplored);
        } catch(InterruptedException e) {
            System.out.printf("Best Move [Greedy]: %d %d\n", bestMove[0], bestMove[1]);
        }
    }

    @Override
    public double evaluate(Board board) {
        return board.countCells(board.BLACK) - board.countCells(board.WHITE);
    }

    private ArrayList<Board> genChildBoards(Board parent) throws IllegalMoveException {
        int c[] = new int[2];
        ArrayList<Board> boards = new ArrayList<>();
        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (parent.isLegalMove(c)) {
                    Board newBoard = parent.getClone();
                    newBoard.makeMove(c);
                    boards.add(newBoard);
                }
            }
        }
        return boards;
    }

    private ArrayList<BoardTuple> genChildBoardTuples(Board parent) throws IllegalMoveException {
        String key = BoardImplementation.getBoardString(parent);
        if(childCache.containsKey(key)) {
            ArrayList<BoardTuple> boards = new ArrayList<>();
            for(int[] c : childCache.get(key)) {
                Board newBoard = parent.getClone();
                newBoard.makeMove(c);
                boards.add(new BoardTuple(newBoard, c.clone()));
            }
            return boards;
        }
        int c[] = new int[2];
        ArrayList<BoardTuple> boards = new ArrayList<>();
        ArrayList<int[]> moves = new ArrayList<>();
        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (parent.isLegalMove(c)) {
                    Board newBoard = parent.getClone();
                    newBoard.makeMove(c);
                    boards.add(new BoardTuple(newBoard, c.clone()));
                    moves.add(c.clone());
                }
            }
        }
        childCache.put(key, moves);
        return boards;
    }

    public double MaxValue(Board board, int depthLimit, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
//        System.out.println("[Max] My color is " + (board.getPlayer() == Board.BLACK ? "BLACK" : "WHITE"));
        numNodesExplored[0] += 1;
        String key = BoardImplementation.getBoardString(board);
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY)
            return evaluate(board);
        int c[] = new int[2];
        int bestMoveHere[] = {-1, -1};
        double best = -Double.MAX_VALUE;
        ArrayList<Board> boards = genChildBoards(board);
        for(Board newBoard : boards) {
            double value = MinValue(newBoard, depthLimit - 1, bestMove, numNodesExplored);
            if(value > best) {
                best = value;
                bestMoveHere[0] = c[0];
                bestMoveHere[1] = c[1];
            }
        }
        bestMove[0] = bestMoveHere[0];
        bestMove[1] = bestMoveHere[1];
//        System.out.printf("[Max] Best Move: %d %d\n", bestMove[0], bestMove[1]);
        return best;
    }

    public double MinValue(Board board, int depthLimit, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY)
            return evaluate(board);
        int c[] = new int[2];
        int bestMoveHere[] = {-1, -1};
        double best = Double.MAX_VALUE;
        ArrayList<Board> boards = genChildBoards(board);
        for(Board newBoard : boards) {
            double value = MaxValue(newBoard, depthLimit - 1, bestMoveHere, numNodesExplored);
//                    System.out.printf("[Min (Max)] Best Child Move: %d %d\n", bestMove[0], bestMove[1]);
            if(value < best){
                best = value;
                bestMove[0] = c[0];
                bestMove[1] = c[1];
            }
            numNodesExplored[0] += 1;
        }
        return best;
    }

    public double MaxValueAB(Board board, int depthLimit, double[] alpha_beta, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY){
            return evaluate(board);
        }
        int c[];
        String repr = BoardImplementation.getBoardString(board);
        int bestMoveHere[] = {-1, -1};
        double best = -Double.MAX_VALUE;
        ArrayList<BoardTuple> boards = genChildBoardTuples(board);
//        boards.sort(Comparator.comparingDouble(x -> maxStore.getOrDefault(BoardImplementation.getBoardString(x.b),-Double.MAX_VALUE)));
        for(BoardTuple t : boards) {
            //System.out.println("Stored score: " + maxStore.getOrDefault(BoardImplementation.getBoardString(t.b),-Double.MAX_VALUE));
            Board newBoard = t.b;
            c = t.move;
//            ScoreTuple cached = minStore.getOrDefault(BoardImplementation.getBoardString(newBoard), alpha_beta[0], alpha_beta[1], new ScoreTuple(alpha_beta[0]));
//            alpha_beta[0] = Double.max(cached.score, alpha_beta[0]);
            double[] alpha_beta_l = {alpha_beta[0], alpha_beta[1]};
            double value = MinValueAB(newBoard, depthLimit - 1, alpha_beta_l, bestMoveHere, numNodesExplored);
            if(value > best) {
                best = value;
                bestMove[0] = c[0];
                bestMove[1] = c[1];
            }
            if(value >= alpha_beta[1]) {
//                System.out.println("key = " + BoardImplementation.getBoardString(board) + " value: " + value);
//                maxStore.set(repr, new ScoreTuple(value, alpha_beta[0], alpha_beta[1]));
                return value;
            }
            alpha_beta[0] = Double.max(alpha_beta[0], value);
        }
//        System.out.println("key = " + BoardImplementation.getBoardString(board) + " best: " + best);
//        maxStore.set(repr, new ScoreTuple(best, alpha_beta[0], alpha_beta[1]));
        return best;
    }

    public double MinValueAB(Board board, int depthLimit, double[] alpha_beta, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY) {
            return evaluate(board);
        }
        int c[];
        int bestMoveHere[] = {-1, -1};
        double best = Double.MAX_VALUE;
        String repr = BoardImplementation.getBoardString(board);
        ArrayList<BoardTuple> boards = genChildBoardTuples(board);
//        boards.sort(Comparator.comparingDouble(x -> -minStore.getOrDefault(BoardImplementation.getBoardString(x.b),-Double.MAX_VALUE)));
        for(BoardTuple t : boards) {
            //System.out.println("Stored score: " + minStore.getOrDefault(BoardImplementation.getBoardString(t.b),-Double.MAX_VALUE));
            Board newBoard = t.b;
            c = t.move;
//            ScoreTuple cached = maxStore.getOrDefault(BoardImplementation.getBoardString(newBoard), alpha_beta[0], alpha_beta[1], new ScoreTuple(alpha_beta[1]));
//            alpha_beta[1] = Double.min(cached.score, alpha_beta[1]);
            double[] alpha_beta_l = {alpha_beta[0], alpha_beta[1]};
            double value = MaxValueAB(newBoard, depthLimit - 1, alpha_beta_l, bestMoveHere, numNodesExplored);
            if(value < best){
                best = value;
                bestMove[0] = c[0];
                bestMove[1] = c[1];
            }
            if(value <= alpha_beta[0]) {
//                System.out.println("key = " + BoardImplementation.getBoardString(board) + " value: " + value);
//                minStore.set(repr, new ScoreTuple(value, alpha_beta[0], alpha_beta[1]));
                return value;
            }
            alpha_beta_l[1] = Double.min(alpha_beta_l[1], value);
        }
//        System.out.println("key = " + BoardImplementation.getBoardString(board) + " best: " + best);
//        minStore.set(repr, new ScoreTuple(best, alpha_beta[0], alpha_beta[1]));
        return best;
    }

    @Override
    public double minimax(Board board, int depthLimit, boolean useAlphaBetaPruning, int[] bestMove, long[] numNodesExplored) throws InterruptedException {
        try {
            if(!useAlphaBetaPruning) {
                if (board.getPlayer() == Board.BLACK) {
                    return MaxValue(board, depthLimit, bestMove, numNodesExplored);
                } else {
                    return MinValue(board, depthLimit, bestMove, numNodesExplored);
                }
            } else {
                double alpha_beta[] = {-Double.MAX_VALUE, Double.MAX_VALUE};
                if (board.getPlayer() == Board.BLACK) {
                    return MaxValueAB(board, depthLimit, alpha_beta, bestMove, numNodesExplored);
                } else {
                    return MinValueAB(board, depthLimit, alpha_beta, bestMove, numNodesExplored);
                }
            }
        }
        catch(IllegalMoveException e) {
            //pass
            System.out.println("Got a bad move");
            return -Double.MAX_VALUE;
        }
    }
}
