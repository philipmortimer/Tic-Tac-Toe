/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noughtsandcrossesdemo;

import java.util.Random;

/**
 * Object used to handle logic of a TicTacToe game.
 * Note that not all best practices have been followed. This is to enhance
 * readability for Java beginners.
 * @author phili
 */
public class TicTacToe {
    int moveNo = 0; //Stores the current move number, starting at 0 for the game beginning
    /*Stores the grid. Access this in the form grid[y][x] (zero indexed). For example,
    grid[0][0] is the top left corner of the grid.*/
    final int [][] GRID = new int [3][3]; 
    //Stores a number of constants used to encode the board and the game state.
    static final int NOUGHT_VAL = -1;
    static final int EMPTY_VAL = 0;
    static final int CROSS_VAL = 1;
    static final int GAME_NOT_COMPLETE = - 2;
    /*Stores the game state. Equal to:
    GAME_NOT_COMPLETE when there the game is still going
    EMPTY_VAL when the game is drawn
    CROSS_VAL when crosses win
    NOUGHT_VAL when noughts win
    */
    int gameState = GAME_NOT_COMPLETE;
    int statesExplored; //Tracks the number of states the minimax algorithm explores
    final boolean ALPHA_BETA_PRUNED;
    //Creates a new TicTacToe object to handle game logic.
    public TicTacToe(boolean alphaPrune){
        ALPHA_BETA_PRUNED = alphaPrune;
        //Sets entire board to be empty
        for(int y = 0; y < 3; y++){
            for(int x = 0; x < 3; x++){
                GRID[y][x] = EMPTY_VAL;
            }
        }
    }
    
    //Checks whether a given square is taken
    public boolean isSquareTaken(int x, int y){
        return GRID[y][x] != EMPTY_VAL;
    }
    
    //Adds move to board. This move must be valid, (i.e. the square choosen must be empty).
    //This must be validated before calling this function.
    public void addMove (int x, int y){
        GRID[y][x] = getPlayerToMove();
        moveNo ++;
        updateGameState(); //Updates the value of the game state variable.
    }
    
    //Calculates which player is going to move next. Return CROSS_VAl if crosses makes the next move.
    private int getPlayerToMove(){
        if(moveNo % 2 == 0){
            return CROSS_VAL;
        }else{
            return NOUGHT_VAL;
        }
    }
    //Gets all legal moves available in current position. Element o of each item in array is
    // x coord and element 1 is y coord.
    private int[][] getAllLegalMoves(){
        int i = 0;
        int [][] legalMoves = new int[9 - moveNo][2];
        for(int y = 0; y < 3; y++){
            for(int x = 0; x < 3; x++){
                if(!isSquareTaken(x, y)){
                    legalMoves[i][0] = x;
                    legalMoves[i][1] = y;
                    i++;
                }
            }
        }
        return legalMoves;
    }
    
    //Used by other classes to allow computer to play a random move.
    public void makeRandomMove(){
        //Selects a random legal move and makes it
        int [][] legalMoves = getAllLegalMoves();
        int moveNo = new Random().nextInt(legalMoves.length);
        addMove(legalMoves[moveNo][0], legalMoves[moveNo][1]);
    }
    
    //Updates game state to match board (i.e. checks to see if its a draw, win or still going).
    public void updateGameState(){
        boolean crossWin = checkDiagWin(CROSS_VAL) || checkAcrossWin(CROSS_VAL)
                || checkDownWin(CROSS_VAL);
        if(crossWin){
            gameState = CROSS_VAL;
            return;
        }
        boolean noughtWin = checkDiagWin(NOUGHT_VAL) || checkAcrossWin(NOUGHT_VAL)
                || checkDownWin(NOUGHT_VAL);
        if(noughtWin){
            gameState = NOUGHT_VAL;
            return;
        }
        //Checks to see whether game is a draw or still going
        if(moveNo == 9){
            gameState = EMPTY_VAL;
        }else{
            gameState = GAME_NOT_COMPLETE;
        }
    }
    
    /**
     * Checks whether a player has won on the diagonal.
     * @param VAL The value that represents the player
     * @return Whether the player has won.
     */
    public boolean checkDiagWin(final int VAL){
        return (GRID[0][0] == VAL && GRID[1][1] == VAL && GRID[2][2] == VAL)
                || (GRID[2][0] == VAL && GRID[1][1] == VAL && GRID[0][2] == VAL);
    }
    
    //Checks to see if a player has won by connecting three pieces across
    public boolean checkAcrossWin(final int VAL){
        return (GRID[0][0] == VAL && GRID[0][1] == VAL && GRID[0][2] == VAL)
                || (GRID[1][0] == VAL && GRID[1][1] == VAL && GRID[1][2] == VAL)
                || (GRID[2][0] == VAL && GRID[2][1] == VAL && GRID[2][2] == VAL);
    }
    //Checks to see if a player has won by connecting three pieces down
    public boolean checkDownWin(final int VAL){
        return (GRID[0][0] == VAL && GRID[1][0] == VAL && GRID[2][0] == VAL)
                || (GRID[0][1] == VAL && GRID[1][1] == VAL && GRID[2][1] == VAL)
                || (GRID[0][2] == VAL && GRID[1][2] == VAL && GRID[2][2] == VAL);
    }
    
    //Makes the best possible move.
    public void makeBestMove(){
        statesExplored = 0;
        //Used to allow one to toggle Alpha-beta pruning.
        int[][] legalMoves = getAllLegalMoves();
        final boolean IS_MAX = moveNo % 2 == 0;
        int bestMoveIndex = -1;
        int bestVal = IS_MAX? -3 : 3;
        int curVal = 0;
        for(int i = 0; i < legalMoves.length; i++){
            addMove(legalMoves[i][0], legalMoves[i][1]);//Makes move to generate new board
            statesExplored++;
            if(ALPHA_BETA_PRUNED && IS_MAX) curVal = min(-3, 3);
            else if(!ALPHA_BETA_PRUNED && IS_MAX) curVal = min();
            else if(ALPHA_BETA_PRUNED) curVal = max(-3, 3);
            else curVal = max();
            if((IS_MAX && curVal >= bestVal) || (!IS_MAX && curVal <= bestVal)){
                bestVal = curVal;
                bestMoveIndex = i;
            }
            //Reverts board to previous state
            moveNo --;
            GRID[legalMoves[i][1]][legalMoves[i][0]] = EMPTY_VAL;
            gameState = GAME_NOT_COMPLETE;//Game can't be complete as there are legal moves to be played
            //If winnining move exists, no need to search other states
            if((IS_MAX && curVal == CROSS_VAL) || (!IS_MAX && curVal == NOUGHT_VAL)) break;
        }
        addMove(legalMoves[bestMoveIndex][0], legalMoves[bestMoveIndex][1]);
    }
    
    //Executes the maximisation part of an alpha-beta pruned minimax algorithm
    private int max(int alpha, int beta){
        if(gameState != GAME_NOT_COMPLETE) return gameState;
        int[][]legalMoves = getAllLegalMoves();
        for(int i = 0; i < legalMoves.length; i++){
            statesExplored++;
            addMove(legalMoves[i][0], legalMoves[i][1]);//Makes move to generate new board
            int score = min(alpha, beta);
            //Reverts board to previous state
            moveNo --;
            GRID[legalMoves[i][1]][legalMoves[i][0]] = EMPTY_VAL;
            gameState = GAME_NOT_COMPLETE;//Game can't be complete as there are legal moves to be played
            if(score >= beta) return beta;
            if(score > alpha) alpha = score;
            if(score == CROSS_VAL) return score;//If winning move exists, no more states need to be searched.
        }
        return alpha;
    }
    
    //Executes the minimisation part of an alpha-beta pruned minimax algorithm
    private int min(int alpha, int beta){
        if(gameState != GAME_NOT_COMPLETE) return gameState;
        int[][]legalMoves = getAllLegalMoves();
        for(int i = 0; i < legalMoves.length; i++){
            statesExplored++;
            addMove(legalMoves[i][0], legalMoves[i][1]);//Makes move to generate new board
            int score = max(alpha, beta);
            //Reverts board to previous state
            moveNo --;
            GRID[legalMoves[i][1]][legalMoves[i][0]] = EMPTY_VAL;
            gameState = GAME_NOT_COMPLETE;//Game can't be complete as there are legal moves to be played
            if(score <= alpha) return alpha;
            if(score < beta) beta = score;
            if(score == NOUGHT_VAL) return score;//If winning move exists, no more states need to be searched.
        }
        return beta;
    }
    
    //Executes the maximisation stage of a minimax algorithm.
    private int max(){
        if(gameState != GAME_NOT_COMPLETE) return gameState;
        int[][]legalMoves = getAllLegalMoves();
        int best = -3;
        for(int i = 0; i < legalMoves.length; i++){
            statesExplored++;
            addMove(legalMoves[i][0], legalMoves[i][1]);//Makes move to generate new board
            int score = min();
            if(score > best) best = score;
            //Reverts board to previous state
            moveNo --;
            GRID[legalMoves[i][1]][legalMoves[i][0]] = EMPTY_VAL;
            gameState = GAME_NOT_COMPLETE;//Game can't be complete as there are legal moves to be played
        }
        return best;
    }
    
        //Executes the maximisation stage of a minimax algorithm.
    private int min(){
        if(gameState != GAME_NOT_COMPLETE) return gameState;
        int[][]legalMoves = getAllLegalMoves();
        int best = 3;
        for(int i = 0; i < legalMoves.length; i++){
            statesExplored++;
            addMove(legalMoves[i][0], legalMoves[i][1]);//Makes move to generate new board
            int score = max();
            if(score < best) best = score;
            //Reverts board to previous state
            moveNo --;
            GRID[legalMoves[i][1]][legalMoves[i][0]] = EMPTY_VAL;
            gameState = GAME_NOT_COMPLETE;//Game can't be complete as there are legal moves to be played
        }
        return best;
    }
}
