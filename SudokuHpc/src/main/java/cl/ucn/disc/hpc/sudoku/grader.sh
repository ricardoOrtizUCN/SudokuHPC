#! / bin / zsh

TEST_DIR = " pruebas "

ROJO = ' \ 033 [1; 31m '
VERDE = ' \ 033 [1; 32m '
NC = ' \ 033 [0m '

para  TEST  en  $ ( busque " ./ $ TEST_DIR / " -type f -name " * .txt " )
hacer
    nombre = $ ( cortar -d / -f4 <<<  $ TEST  | cortar -d. -f1 )

    # ejecutar prueba
    inicio = $ ( fecha +% s% 3N )
    java Sudoku " $ TEST "  & >> log.out
    end = $ ( fecha +% s% 3N )

    # imprimir resultado
    transcurrido = $ (( fin - inicio ))
    n = $ (( % transcurrido 1000 ))
    s = $ (( transcurrido / 1000 ))
    m = $ (( s / 60 ))
    echo  $ nombre  $ {VERDE} ✓ $ {NC}  $ {RED} $ m " m "  $ (( s % 60 )) " s "  $ n " ms " $ {NC}
hecho