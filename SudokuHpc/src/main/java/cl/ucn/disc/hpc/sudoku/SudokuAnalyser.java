package cl.ucn.disc.hpc.sudoku;

@FunctionalInterface
interface SudokuAnalyser {

    /**
     * Returns number of possible solutions for the given board.
     * @return  number of possible solutions.
     */
    int computeSolutionsNumber();

}
