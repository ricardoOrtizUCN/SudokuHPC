package cl.ucn.disc.hpc.sudoku;

import java.io.IOException;
import java.math.BigInteger;

class Sudoku {

    static final boolean parallel = true;

    public static void main(String[] args) {

        SudokuBoard board;

        // deal with invalid file paths
        try {

            board = SudokuBoard.parseFile(args[0]);

            SudokuAnalyser algorithm = (parallel) ? new ParallelSudoku(board) :
                    new SequentialSudoku(board);

            // solutions space
            BigInteger solSpace = board.solutionsSpace();
            System.out.println(solSpace);

            // filled cells percentage
            double filledPerc = board.completedPercentage();
            System.out.println(filledPerc);

            // number of possible solutions
            int solNumber = algorithm.computeSolutionsNumber();
            System.out.println(solNumber);


        } catch (IOException e) {
            System.out.println("File name '" + args[0] + "' is not a valid URI identifier. Try again!");
        }
    }

}
