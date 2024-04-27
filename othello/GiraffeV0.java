package othello;

import java.util.ArrayList;

public class GiraffeV0 extends AIPlayer {
    @Override
    public String getName() {
        return "Sarah Connor v0";
    }

    long numberExplored = 0;

    @Override
    public void getNextMove(Board board, int[] bestMove) throws IllegalCellException, IllegalMoveException {
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

    public double MaxValue(Board board, int depthLimit, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
//        System.out.println("[Max] My color is " + (board.getPlayer() == Board.BLACK ? "BLACK" : "WHITE"));
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY)
            return evaluate(board);
        int c[] = new int[2];
        int bestMoveHere[] = {-1, -1};
        double best = -Double.MAX_VALUE;
        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (board.isLegalMove(c)) {
//                    System.out.printf("[Max] Checking Legal Move: %d %d\n", c[0], c[1]);
                    Board newBoard = board.getClone();
                    newBoard.makeMove(c);
                    double value = MinValue(newBoard, depthLimit - 1, bestMove, numNodesExplored);
//                    System.out.printf("[Max (Min)] Best Child Move: %d %d With Value %f\n", bestMove[0], bestMove[1], value);
                    if(value > best) {
                        best = value;
                        bestMoveHere[0] = c[0];
                        bestMoveHere[1] = c[1];
                    }
                }
            }
        }
        bestMove[0] = bestMoveHere[0];
        bestMove[1] = bestMoveHere[1];
//        System.out.printf("[Max] Best Move: %d %d\n", bestMove[0], bestMove[1]);
        return best;
    }

    public double MinValue(Board board, int depthLimit, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
//        System.out.println("[Min] My color is " + (board.getPlayer() == Board.BLACK ? "BLACK" : "WHITE"));
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY)
            return evaluate(board);
        int c[] = new int[2];
        int bestMoveHere[] = {-1, -1};
        double best = Double.MAX_VALUE;
        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (board.isLegalMove(c)) {
//                    System.out.printf("[Min] Checking Legal Move: %d %d\n", c[0], c[1]);
                    Board newBoard = board.getClone();
                    newBoard.makeMove(c);
                    double value = MaxValue(newBoard, depthLimit - 1, bestMoveHere, numNodesExplored);
//                    System.out.printf("[Min (Max)] Best Child Move: %d %d\n", bestMove[0], bestMove[1]);
                    if(value < best){
                        best = value;
                        bestMove[0] = c[0];
                        bestMove[1] = c[1];
                    }
                    numNodesExplored[0] += 1;
                }
            }
        }
//        System.out.printf("[Min] Best Move: %d %d\n", bestMove[0], bestMove[1]);
        return best;
    }

    public double MaxValueAB(Board board, int depthLimit, double[] alpha_beta, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
//        System.out.println("[Max] My color is " + (board.getPlayer() == Board.BLACK ? "BLACK" : "WHITE"));
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY)
            return evaluate(board);
        int c[] = new int[2];
        int bestMoveHere[] = {-1, -1};
        double best = -Double.MAX_VALUE;
        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (board.isLegalMove(c)) {
//                    System.out.printf("[Max] Checking Legal Move: %d %d\n", c[0], c[1]);
                    Board newBoard = board.getClone();
                    newBoard.makeMove(c);
                    double[] alpha_beta_l = {alpha_beta[0], alpha_beta[1]};
                    double value = MinValueAB(newBoard, depthLimit - 1, alpha_beta_l, bestMoveHere, numNodesExplored);
//                    System.out.printf("[Max (Min)] Best Child Move: %d %d With Value %f\n", bestMove[0], bestMove[1], value);
                    if(value > best) {
                        best = value;
                        bestMove[0] = c[0];
                        bestMove[1] = c[1];
                    }
                    if(value >= alpha_beta[1]) {
                        return value;
                    }
                    alpha_beta[0] = Double.max(alpha_beta[0], value);
                }
            }
        }
//        System.out.printf("[Max] Best Move: %d %d\n", bestMove[0], bestMove[1]);
        return best;
    }

    public double MinValueAB(Board board, int depthLimit, double[] alpha_beta, int[] bestMove, long[] numNodesExplored) throws IllegalMoveException {
//        System.out.println("[Min] My color is " + (board.getPlayer() == Board.BLACK ? "BLACK" : "WHITE"));
        numNodesExplored[0] += 1;
        if(depthLimit == 0 || board.getWinner() != Board.EMPTY)
            return evaluate(board);
        int c[] = new int[2];
        int bestMoveHere[] = {-1, -1};
        double best = Double.MAX_VALUE;
        for (c[0]=0; c[0]<Board.BOARD_DIM; c[0]++) {
            for (c[1]=0; c[1]<Board.BOARD_DIM; c[1]++) {
                if (board.isLegalMove(c)) {
//                    System.out.printf("[Min] Checking Legal Move: %d %d\n", c[0], c[1]);
                    Board newBoard = board.getClone();
                    newBoard.makeMove(c);
                    double[] alpha_beta_l = {alpha_beta[0], alpha_beta[1]};
                    double value = MaxValueAB(newBoard, depthLimit - 1, alpha_beta_l, bestMoveHere, numNodesExplored);
//                    System.out.printf("[Min (Max)] Best Child Move: %d %d\n", bestMove[0], bestMove[1]);
                    if(value < best){
                        best = value;
                        bestMove[0] = c[0];
                        bestMove[1] = c[1];
                    }
                    if(value <= alpha_beta[0]) {
                        return value;
                    }
                    alpha_beta_l[1] = Double.min(alpha_beta_l[1], value);
                }
            }
        }
//        System.out.printf("[Min] Best Move: %d %d\n", bestMove[0], bestMove[1]);
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
