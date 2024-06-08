import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Buscaminas {

    public static char[][] leerTableroDelArchivo(String nombreArchivo) throws FileNotFoundException {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            throw new IllegalArgumentException("Error: Debe proporcionar un nombre de archivo válido.");
        }
        try (Scanner scanner = new Scanner(new File(nombreArchivo))) {
            // Verifique las dos primeras líneas (dimensiones
            if (!scanner.hasNextInt()) {
                throw new InputMismatchException("Error: Formato de archivo inválido. Falta el número de filas.");
            }
            int filas = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                throw new InputMismatchException("Error: Formato de archivo inválido. Falta el número de columnas.");
            }
            int columnas = scanner.nextInt();
            scanner.nextLine(); // Consumir nueva línea después de las dimensiones

            // Validar datos en líneas restantes
            char[][] tablero = new char[filas][columnas];
            for (int i = 0; i < filas; i++) {
                if (!scanner.hasNextLine()) {
                    throw new InputMismatchException("Error: Formato de archivo inválido. Faltan filas en el tablero.");
                }
                String linea = scanner.nextLine();
                if (linea.length() != columnas) {
                    throw new InputMismatchException("Error: Formato de archivo inválido. Las filas deben tener la misma longitud.");
                }
                tablero[i] = linea.toCharArray();
                for (char caracter : tablero[i]) {
                    if (!"*. ".contains(String.valueOf(caracter))) {
                        throw new InputMismatchException("Error: Formato de archivo inválido. Solo se permiten caracteres '.' y '*'");
                    }
                }
            }
            return tablero;
        }
    }



    public static char[][] crearBuscaminas(char[][] tablero) {
        int filas = tablero.length;
        int columnas = tablero[0].length;

        // Validar longitud consistente de las columnas (todas las filas deben tener la misma longitud)
        if (!IntStream.range(0, filas).allMatch(i -> tablero[i].length == columnas)) {
            throw new IllegalArgumentException("Tablero inválido: Las filas deben tener la misma longitud.");
        }

        // Validar caracteres permitidos en el tablero (solo '.', '*', y espacios)
        if (!IntStream.range(0, filas)
                .allMatch(i -> IntStream.range(0, columnas)
                        .allMatch(j -> "*. ".indexOf(tablero[i][j]) != -1))) {
            throw new IllegalArgumentException("Tablero inválido: Solo se permiten caracteres '.' y '*'");
        }

        // Inicializar la matriz de resultado con las mismas dimensiones que el tablero de entrada
        char[][] resultado = new char[filas][columnas];
        for (int i = 0; i < filas; i++) {
            Arrays.fill(resultado[i], '*');
        }

// Recorrer cada celda del tablero
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                if (tablero[fila][columna] == '*') {  // Si es una mina, continuar a la siguiente celda
                    continue;
                }
                int conteo = 0;

                // Contar el número de minas adyacentes a la celda actual
                for (int i = Math.max(0, fila - 1); i <= Math.min(filas - 1, fila + 1); i++) {
                    for (int j = Math.max(0, columna - 1); j <= Math.min(columnas - 1, columna + 1); j++) {
                        // Comprobar si la celda está dentro de los límites del tablero y si es una mina
                        if (tablero[i][j] == '*') {
                            conteo++;
                        }
                    }
                }

// Excluir la celda actual del conteo (evitar contarse como adyacente)
                if (tablero[fila][columna] != '*') {  // Sólo si la celda actual no es una mina.
                    conteo--;
                }
                // Asignar el conteo de minas adyacentes a la celda correspondiente en el tablero de resultado
                resultado[fila][columna] = (conteo == -1) ? '0' : (char) (Math.max(conteo, 0) + '0');
            }
        }


        // Devolver el tablero de Buscaminas completado
        return resultado;
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Error: Debe proporcionar un nombre de archivo como argumento.");
        }

        String filename = args[0];
        try {
            char[][] tablero = leerTableroDelArchivo(filename);
            char[][] minesweeper = crearBuscaminas(tablero);
            for (char[] fila : minesweeper) {
                System.out.println(Arrays.toString(fila).replace(",", ""));
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.toString()+"Error: Archivo no encontrado: " + filename);
        }
    }
}
