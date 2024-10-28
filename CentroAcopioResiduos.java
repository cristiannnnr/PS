package ps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CentroAcopioResiduos {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ingreso de datos que se usa con validación
        char diaInicio = ' ';
        while (true) {
            System.out.println("Ingrese el día inicial (L, M, X, J, V, S, D): ");
            String diaInicioStr = scanner.next();
            if (diaInicioStr.length() == 1 && "LMXJVSD".indexOf(diaInicioStr.charAt(0)) != -1) {
                diaInicio = diaInicioStr.charAt(0);
                break;
            } else {
                System.out.println("Día inicial no válido");
            }
        }

        int diasDelMes = 0;
        while (true) {
            System.out.println("Ingrese la cantidad de días del mes: ");
            if (scanner.hasNextInt()) {
                diasDelMes = scanner.nextInt();
                if (diasDelMes > 0) {
                    break;
                } else {
                    System.out.println("La cantidad de días debe ser un número positivo.");
                }
            } else {
                System.out.println("Entrada no válida. Intente de nuevo.");
                scanner.next(); // se limpia la entrada
            }
        }

        int diaConsulta = 0;
        while (true) {
            System.out.println("Ingrese el día de consulta: ");
            if (scanner.hasNextInt()) {
                diaConsulta = scanner.nextInt();
                if (diaConsulta > 0 && diaConsulta <= diasDelMes) {
                    break;
                } else {
                    System.out.println("El día de consulta debe estar dentro del rango de días del mes. Intente de nuevo.");
                }
            } else {
                System.out.println("Entrada no válida. Intente de nuevo.");
                scanner.next(); // se limpia la entrada
            }
        }

        scanner.nextLine();

        System.out.println("Ingrese los residuos de la sede principal");
        List<String> residuosSedePrincipal = Arrays.asList(scanner.nextLine().split(",\\s*"));

        System.out.println("Ingrese los residuos de la oficina");
        List<String> residuosOficina = Arrays.asList(scanner.nextLine().split(",\\s*"));

        System.out.println("Ingrese los residuos de la sede secundaria");
        List<String> residuosSedeSecundaria = Arrays.asList(scanner.nextLine().split(",\\s*"));

        // se ejecuita el calculo y se obtiene el resultado
        int[][] resultado = calcularResiduos(diaInicio, diasDelMes, diaConsulta, residuosSedePrincipal, residuosOficina, residuosSedeSecundaria);

        // Imprime los resultados en el formato solicitado
        System.out.println(formatearResultado(resultado[0]));
        System.out.println(formatearResultado(resultado[1]));

        scanner.close();
    }

    public static int[][] calcularResiduos(char diaInicio, int diasDelMes, int diaConsulta,
                                           List<String> residuosSedePrincipal, List<String> residuosOficina,
                                           List<String> residuosSedeSecundaria) {

        int[] llegada = new int[4]; // Zona de llegada [r, v, b, a]
        int[] almacenamiento = new int[4]; // Almacenamiento [r, v, b, a]

        int[] diasRecoleccionSedePrincipal = calcularDiasRecoleccion(diasDelMes, "impar");
        int[] diasRecoleccionOficina = calcularDiasOficina(diaInicio, diasDelMes);
        int[] diasRecoleccionSedeSecundaria = calcularDiasRecoleccion(diasDelMes, "par");

        // se simula cada día
        for (int dia = 1; dia <= diaConsulta; dia++) {
            // se cuenta los residuos solo si es día de recolección
            if (esDiaRecoleccion(dia, diasRecoleccionSedePrincipal)) {
                int index = (dia - 1) / 2; // este es el indice en base a días de recolección
                if (index < residuosSedePrincipal.size()) {
                    contarResiduos(residuosSedePrincipal.get(index), llegada);
                }
            }

            if (esDiaRecoleccion(dia, diasRecoleccionOficina)) {
                int index = (dia - 1) / 2; 
                if (index < residuosOficina.size()) {
                    contarResiduos(residuosOficina.get(index), llegada);
                }
            }

            if (esDiaRecoleccion(dia, diasRecoleccionSedeSecundaria)) {
                int index = (dia - 1) / 2; 
                if (index < residuosSedeSecundaria.size()) {
                    contarResiduos(residuosSedeSecundaria.get(index), llegada);
                }
            }

            transferirAAlmacenamiento(llegada, almacenamiento);
        }

        return new int[][] {llegada, almacenamiento};
    }

    private static int[] calcularDiasRecoleccion(int diasDelMes, String tipo) {
        List<Integer> dias = new ArrayList<>();
        for (int dia = 1; dia <= diasDelMes; dia++) {
            if ((tipo.equals("impar") && dia % 2 != 0) || (tipo.equals("par") && dia % 2 == 0)) {
                dias.add(dia);
            }
        }
        return dias.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int[] calcularDiasOficina(char diaInicio, int diasDelMes) {
        int diaBase = calcularDiaBase(diaInicio);

        List<Integer> dias = new ArrayList<>();
        for (int dia = diaBase; dia <= diasDelMes; dia++) {
            if ((dia % 7 == 2) || (dia % 7 == 4)) {
                dias.add(dia);
            }
        }
        return dias.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int calcularDiaBase(char diaInicio) {
        int diaBase;
        switch (diaInicio) {
            case 'L':
                diaBase = 1;
                break;
            case 'M':
                diaBase = 2;
                break;
            case 'X':
                diaBase = 3;
                break;
            case 'J':
                diaBase = 4;
                break;
            case 'V':
                diaBase = 5;
                break;
            case 'S':
                diaBase = 6;
                break;
            case 'D':
                diaBase = 7;
                break;
            default:
                throw new IllegalArgumentException("Día inicial no válido");
        }
        return diaBase;
    }

    private static boolean esDiaRecoleccion(int dia, int[] diasRecoleccion) {
        for (int d : diasRecoleccion) {
            if (d == dia) {
                return true;
            }
        }
        return false;
    }

    private static void contarResiduos(String residuos, int[] contenedor) {
        for (char tipo : residuos.toCharArray()) {
            switch (tipo) {
                case 'r':
                    contenedor[0]++;
                    break;
                case 'v':
                    contenedor[1]++;
                    break;
                case 'b':
                    contenedor[2]++;
                    break;
                case 'a':
                    contenedor[3]++;
                    break;
            }
        }
    }

    private static void transferirAAlmacenamiento(int[] llegada, int[] almacenamiento) {
        for (int i = 0; i < llegada.length; i++) {
            almacenamiento[i] += llegada[i];
            llegada[i] = 0; // se reinicia la zona de llegada
        }
    }

    private static String formatearResultado(int[] contenedor) {
        return String.format("[%dr, %dv, %db, %da]", contenedor[0], contenedor[1], contenedor[2], contenedor[3]);
    }
}
