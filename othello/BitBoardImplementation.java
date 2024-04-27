package othello;

/**
 * A basic implementation of the Board interface.
 * @author Lacey Neagle
 * @author Britton Wolfe (Yes, this is code I wrote in college :-)  It's not pretty)
 */

public class BitBoardImplementation implements Board {
	long boardW;
	long boardB;
	int playr;
	private static  long[][] masks;

	/**
	 * Returns a string representation of this board
	 * That string can be used to reconstruct the board
	 */
	public static String getBoardString(Board b){
//		System.out.println(Long.toBinaryString(((BitBoardImplementation)b).boardB));
		StringBuilder sb = new StringBuilder(200);
		int[] loc = new int[2];

		sb.append(b.getPlayer() + "|");
		for(loc[0]=0; loc[0]<BOARD_DIM; loc[0]++){
			for(loc[1]=0; loc[1]<BOARD_DIM; loc[1]++){
				try{
					sb.append(b.getCell(loc) + ",");
				}catch(IllegalCellException ice){
					System.err.println("In getBoardString, this error should never happen:");
					System.err.println(ice);
					System.exit(1);
				}
			}
			sb.append("|");
		}

		return sb.toString();
	}

	/**
	 * Set this Board to be like the one given by str
	 */
	public void assignFromString(String str){
		String[] tokens = str.split("[,|]+");
		int index = 0;

		playr = Integer.parseInt(tokens[index]);
		index++;

		for(int x=0; x<BOARD_DIM; x++){
			for(int y=0; y<BOARD_DIM; y++){
				int a = Integer.parseInt(tokens[index]);
				if(a == Board.BLACK) {
					boardB |= (1L << x) << (8 * y);
				}
				else if(a == Board.WHITE) {
					boardW |= (1L << x) << (8 * y);
				}
				index++;
			}
		}
	}

	public BitBoardImplementation(){
		masks = new long[8][8];
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				masks[x][y] = (1L << x) << (8 * y);
			}
		}
		initBoard();
	}

	public BitBoardImplementation(BitBoardImplementation orig){
				boardB = orig.boardB;
				boardW = orig.boardW;
		playr = orig.playr;
	}

	public void initBoard(){
		playr = BLACK;
		boardB = ((1L << 4)  << (3*8)) | ((1L << 3)  << (4*8));
//		boardB = (1 << 4)  << (24);
		boardW = ((1L << 4)  << (4*8)) | ((1L << 3)  << (3*8));
	}

	public Board getClone(){
		Board CloneBoard = new BitBoardImplementation(this);
		return CloneBoard;
	}

	public int getCell(int[] location) throws IllegalCellException {
		if((location[0]>=0)&&(location[0]<BOARD_DIM)&&(location[1]>=0)&&(location[1]<BOARD_DIM)) {
			long mask = masks[location[0]][location[1]];
			if((boardB & mask) != 0) {
				return Board.BLACK;
			} else if ((boardW & mask) != 0) {
				return Board.WHITE;
			} else {
				return Board.EMPTY;
			}
		}
		else
			throw new IllegalCellException();
	}

	public int countCells(int cellType){
		int count = 0;
		if(cellType == Board.BLACK || cellType == Board.EMPTY) {
			long a = boardB;
			while(a != 0) {
				a &= a-1L;
				count++;
			}
		}
		if(cellType == Board.WHITE || cellType == Board.EMPTY) {
			long a = boardW;
			while(a != 0) {
				a &= a-1L;
				count++;
			}
		}
		if(cellType == Board.EMPTY) {
			return 64 - count;
		}
		return count;
	}

	public boolean isLegalMove(int[] location){
		try {
			if ((location[0] >= 0) && (location[0] < BOARD_DIM) && (location[1] >= 0) && (location[1] < BOARD_DIM)) {
				int player = getPlayer();
				boolean legal = false;
				int i = 1;
				long oppBoard, myBoard;
				if (player == WHITE) {
					myBoard = boardW;
					oppBoard = boardB;
				}
				else{
					myBoard = boardB;
					oppBoard = boardW;
				}
				int xVal = location[0];
				int yVal = location[1];
				if (getCell(location) == EMPTY) {
					if ((xVal > 1) && (yVal > 1) && ((oppBoard & masks[xVal - 1][yVal - 1]) != 0)) {
						legal = check(1, xVal - 2, yVal - 2, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((yVal > 1) && (oppBoard & masks[xVal][yVal - 1]) != 0) {
						legal = check(2, xVal, yVal - 2, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((xVal < 6) && (yVal > 1) && (oppBoard & masks[xVal+1][yVal - 1]) != 0) {
						legal = check(3, xVal + 2, yVal - 2, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((xVal < 6) && (oppBoard & masks[xVal+1][yVal]) != 0) {
						legal = check(4, xVal + 2, yVal, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((xVal < 6) && (yVal < 6) && (oppBoard & masks[xVal+1][yVal+1]) != 0) {
						legal = check(5, xVal + 2, yVal + 2, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((yVal < 6) && (oppBoard & masks[xVal][yVal+1]) != 0) {
						legal = check(6, xVal, yVal + 2, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((xVal > 1) && (yVal < 6) && (oppBoard & masks[xVal-1][yVal+1]) != 0) {
						legal = check(7, xVal - 2, yVal + 2, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
					if ((xVal > 1) && (oppBoard & masks[xVal-1][yVal]) != 0) {
						legal = check(BOARD_DIM, xVal - 2, yVal, player, oppBoard, myBoard);
						if (legal == true)
							return legal;
					}
				}
				return legal;
			} else
				return false;
		} catch(IllegalCellException e) {
			return false;
		}
	}

	public void makeMove(int[] location) throws IllegalMoveException{
		int player=getPlayer();
		long oppBoard;
		long playerBoard;
		int opp;
		if (player == WHITE) {
			oppBoard = boardB;
			playerBoard = boardW;
			opp = BLACK;
		}
		else{
			oppBoard = boardW;
			playerBoard = boardB;
			opp = WHITE;
		}
		if (isLegalMove(location)){
			int xVal = location[0];
			int yVal = location[1];
			if(player == WHITE)
				boardW |= masks[xVal][yVal];
			else
				boardB |= masks[xVal][yVal];
			if(xVal>1 && yVal>1 && (oppBoard & masks[xVal-1][yVal-1]) != 0){
				if(check(1, xVal-2, yVal-2, player, oppBoard, playerBoard)){
					int x = xVal-1;
					int y = yVal-1;
					while((oppBoard & masks[x][y]) != 0){
						playerBoard |= masks[x][y];
						x--;
						y--;
					}
				}
			}
			if(yVal>1 && (oppBoard & masks[xVal][yVal-1]) != 0){
				if(check(2, xVal, yVal-2, player, oppBoard, playerBoard)){
					int c = yVal-1;
					while((oppBoard & masks[xVal][c]) != 0){
						playerBoard |= masks[xVal][c];
						c--;
					}
				}
			}
			if(xVal<7 && yVal>1 && (oppBoard & masks[xVal+1][yVal-1]) != 0){
				if(check(3, xVal+2, yVal-2, player, oppBoard, playerBoard)){
					int x = xVal+1;
					int y = yVal-1;
					while((oppBoard & masks[x][y]) != 0){
						playerBoard |= masks[x][y];
						x++;
						y--;
					}
				}
			}
			if(xVal<7 && (oppBoard & masks[xVal+1][yVal]) != 0){
				if(check(4, xVal+2, yVal, player, oppBoard, playerBoard)){
					int x = xVal+1;
					while((oppBoard & masks[x][yVal]) != 0){
						playerBoard |= masks[x][yVal];
						x++;
					}
				}
			}
			if(xVal<7 && yVal<7 && (oppBoard & masks[xVal+1][yVal+1]) != 0){
				if(check(5, xVal+2, yVal+2, player, oppBoard, playerBoard)){
					int x = xVal+1;
					int y = yVal+1;
					while((oppBoard & masks[x][y]) != 0){
						playerBoard |= masks[x][y];
						x++;
						y++;
					}
				}
			}
			if(yVal<7 && (oppBoard & masks[xVal][yVal+1]) != 0){
				if(check(6, xVal, yVal+2, player, oppBoard, playerBoard)){
					int y = yVal+1;
					while((oppBoard & masks[xVal][y]) != 0){
						playerBoard |= masks[xVal][y];
						y++;
					}
				}
			}
			if(xVal>1 && yVal<7 && (oppBoard & masks[xVal-1][yVal+1]) != 0){
				if(check(7, xVal-2, yVal+2, player, oppBoard, playerBoard)){
					int x = xVal-1;
					int y = yVal+1;
					while((oppBoard & masks[x][y]) != 0){
						playerBoard |= masks[x][y];
						x--;
						y++;
					}
				}
			}
			if((xVal>1) && (oppBoard & masks[xVal-1][yVal]) != 0){
				if(check(BOARD_DIM, xVal-2, yVal, player, oppBoard, playerBoard)){
					int x = xVal-1;
					while((oppBoard & masks[x][yVal]) != 0){
						playerBoard |= masks[x][yVal];
						x--;
					}
				}
			}
			playr = opp;
			if(!hasLegalMove()){
				playr=player;
				if(!hasLegalMove())
					playr=EMPTY;
			}
		}
		else{
			throw new IllegalMoveException();
		}
	}




	public int getPlayer(){
		return playr;
	}

	public int getWinner(){
		if(playr != EMPTY){
			// someone has a legal move remaining
			return EMPTY;
		}

		int wcount = countCells(WHITE);
		int bcount = countCells(BLACK);
		if (bcount>wcount)
			return BLACK;
		else if (wcount>bcount)
			return WHITE;
		else
			return EMPTY;
	}

	boolean check(int code, int xVal, int yVal, int player, long oppBoard, long playerBoard){
		boolean checker = false;
		if((xVal>=0)&&(xVal<BOARD_DIM)&&(yVal>=0)&&(yVal<BOARD_DIM)&&((playerBoard & masks[xVal][yVal]) != 0))
			checker = true;
		else switch (code){
			case 1:
				if((xVal>0) && (yVal>0) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(1,xVal-1,yVal-1,player,oppBoard,playerBoard);
				break;
			case 2:
				if((yVal>0)&&((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(2,xVal,yVal-1,player,oppBoard,playerBoard);
				break;
			case 3:
				if((xVal<7) && (yVal>0) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(3,xVal+1,yVal-1,player,oppBoard,playerBoard);
				break;
			case 4:
				if((xVal<7) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(4,xVal+1,yVal,player,oppBoard,playerBoard);
				break;
			case 5:
				if((xVal<7) && (yVal<7) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(5,xVal+1,yVal+1,player,oppBoard,playerBoard);
				break;
			case 6:
				if((yVal<7) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(6,xVal,yVal+1,player,oppBoard,playerBoard);
				break;
			case 7:
				if((xVal>0) && (yVal<7) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(7,xVal-1,yVal+1,player,oppBoard,playerBoard);
				break;
			case 8:
				if((xVal>0) && ((oppBoard & masks[xVal][yVal]) != 0))
					checker = check(BOARD_DIM,xVal-1,yVal,player,oppBoard,playerBoard);
				break;
		}
		return checker;
	}

	boolean hasLegalMove(){
		int[] loc = new int[2];
		for(int i=0; i<BOARD_DIM; i++)
			for(int j=0; j<BOARD_DIM; j++){
				if(((boardB & masks[i][j]) == 0) && ((boardW & masks[i][j]) == 0)) {
					loc[0] = i;
					loc[1] = j;
					if (isLegalMove(loc))
						return true;
				}
			}
		return false;
	}
}
