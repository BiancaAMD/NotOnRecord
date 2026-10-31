import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NotOnRecord {
    public static void main(String[] args) throws IOException{
        //parte declarativa
        BufferedReader leer = new BufferedReader(new InputStreamReader(System.in));

        // ===================== COLORES =====================
        final String RESET = "\u001B[0m";
        final String ROJO  = "\u001B[31m";
        final String VERDE = "\u001B[32m";
        final String AMARILLO = "\u001B[33m";
        final String AZUL     = "\u001B[34m";
        final String MAGENTA  = "\u001B[35m";
        final String CIAN     = "\u001B[36m";
        final String NEGRITA  = "\u001B[1m";

        // ========================== CONSTANTES ==========================
        final int MAX_ERRORES      = 3;
        final int NUM_VISITANTES   = 5;   
        final int NUM_RESIDENTES   = 5;
        final int NUM_PISTAS       = 4;
        final int PUNTOS_ACIERTO   = 100;
        final int PENALIZACION     = 50;   

        // ========================== VARIABLES ===========================
        //empleados por el usuario
        String numeroTelefono;

        // Estado de la partida
        int puntos = 0;
        int errores = 0;

        // Visitante actual
        int posResidente, posConviviente;          // posición del residente del que cogemos datos (0 a NUM_RESIDENTES - 1) y saber si en el piso hay alguien más
        boolean esImpostor;        // si es impostor o no

        // Control del menú
        int opcion;
        boolean decidido;          // true cuando se da el veredicto y acaba el turno

        // Contadores para valorar la decisión
        int pistasExistentes;
        int pistasEncontradas;

        // ===================== ARRAYS: REGISTRO =========================
        // Arrays paralelos: la misma posición describe al mismo residente
        String[] nombres   = new String[NUM_RESIDENTES];
        String[] pisos     = new String[NUM_RESIDENTES];
        String[] dnis      = new String[NUM_RESIDENTES];
        String[] telefonos = new String[NUM_RESIDENTES];

        // ===================== ARRAYS: PISTAS ===========================
        // Posiciones DE PISTAS: [0] nombre distinto, [1] piso incorrecto,
        //                       [2] DNI Incorrecto, [3] teléfono sin respuesta

        boolean[] pistaActiva      = new boolean[NUM_PISTAS];  // pistas que pone el programa
        boolean[] pistaDescubierta = new boolean[NUM_PISTAS];  // pistas que ha encontrado el jugador
        String[]  descripcionPistas = new String[NUM_PISTAS]; // texto que mostramos al jugador describiendo la pista

        //ABECEDARIO --> nos servirá para , en el caso de que el DNI sea incorrecto, generar una letra mala
        String[] abecedario = {"A", "B", "C", "D", "E", "F", "H"};

        // ================= ARRAYS: DATOS DEL VISITANTE ==================
        // Posiciones: [0] nombre, [1] piso, [2] DNI
        String[] datosDeclarados = new String[3]; //array con el que tomamos datos de los arrays paralelos, con esto formamos el documento del visitante
        String[] nombresCambiados = new String[NUM_RESIDENTES]; //esta nos ayuda a cambiar los nombres en el caso de que sea un impostor y la pista sea el nombre

        //cuerpo del programa

        // ======== 1. CARGA DE DATOS =========
        //RESIDENTES
        nombres[0] = "Julián Ramírez";
        pisos[0] = "3";
        dnis[0] = "K23O45P";
        telefonos[0] = "3030";

        nombres[1] = "Karla Real";
        pisos [1] = pisos[0];
        dnis [1] = "O13234D";
        telefonos[1] = telefonos[0];

        nombres[2] = "Sonia Doñoro";
        pisos[2] = "2";
        dnis[2] = "W24509U"; 
        telefonos[2] = "2020";

        nombres[3] = "Fran Pardo";
        pisos[3] = pisos[2];
        dnis[3] = "L96436N";
        telefonos[3] = telefonos[2];

        nombres[4] = "Cristina Casado";
        pisos[4] = "1";
        dnis[4] = "K34424F";
        telefonos[4] = "1010";

        //PISTAS
        nombresCambiados[0] = "Julián Ramiez";
        nombresCambiados[1] = "Carla Leal";
        nombresCambiados[2] = "Soniia Doñoro";
        nombresCambiados[3] = "Fron Pardo";
        nombresCambiados[4] = "Cristian Casado";

        descripcionPistas[0] = "El nombre no coincide con el nombre registrado";
        descripcionPistas[1] = "El piso no coincide con el piso registrado";
        descripcionPistas[2] = "El DNI no coincide con el DNI del registro" ;
        descripcionPistas[3] = "No ha habido respuesta";

        //============JUEGO================

        for (int contador = 0; contador < NUM_RESIDENTES; contador++) {

            // GENERAR UN VISITANTE E INDICAR SI ES IMPOSTOR O NO
            posResidente = (int) (Math.random() * NUM_RESIDENTES);
            esImpostor = Math.random() < 0.50; // si sale un num menor que 0.5 será impostor

            // PISTAS: reiniciamos para que no queden restos
            for (int i = 0; i < NUM_PISTAS; i++) {
                pistaActiva[i] = false;
                pistaDescubierta[i] = false;
            }

            //RELLENAMOS LOS DATOS QUE EL VISITANTE NOS VA A DAR, LO COPIAMOS DEL REGISTRO DE LA POSICION DE RESIDENTE QUE HEMOS RECOGIDO ANTES
            //NOTA: [0] es nombre, [1] es piso y [2] es DNI
            datosDeclarados[0] = nombres[posResidente];
            datosDeclarados[1] = pisos[posResidente];
            datosDeclarados[2] = dnis[posResidente];

            //EN EL CASO DE QUE SEA IMPOSTOR, UNO DE ESTOS 3 SE ALTERARÁ O NO , EN EL ÚLTIMO CASO OBLIGO A INVESTIGAR MÁS
            if (esImpostor) {
                switch ((int) (Math.random() * 4)) { //esto lo que hace es dar 0, 1 , 2 o 3 , que son nuetros 3 datos que podemos alterar o la posibilidad de no alterar
                    case 0:
                        datosDeclarados[0] = nombresCambiados[posResidente];
                        pistaActiva[0] = true;
                        break;

                    case 1:
                        int pisoRandom;
                        do {
                            pisoRandom = (int) (Math.random() * 4); //para que salga un piso entre 0 y 3

                        } while (pisoRandom == Integer.parseInt(datosDeclarados[1]));

                        datosDeclarados[1] = String.valueOf(pisoRandom);
                        pistaActiva[1] = true;
                        break;
                        
                    case 2:
                        int posicionLetraRandom = (int)(Math.random()*7);
                        datosDeclarados[2] = abecedario[posicionLetraRandom]+ dnis[posResidente];
                        pistaActiva[2] = true;
                        break;

                    case 3:
                    posicionLetraRandom = (int)(Math.random()*7);
                    datosDeclarados[2] = abecedario[posicionLetraRandom]+ dnis[posResidente];
                    pistaActiva[3] = true;
                    break;

                }
                
            }

            //MOSTRAMOS POR PANTALLA
            System.out.println(NEGRITA +"TURNO "+ (contador+1) +RESET);
            System.out.println(NEGRITA + AZUL +"VISITANTE: "+ RESET+ "Buenos días señor portero, me abre por favor? "+ RESET);
            System.out.println(MAGENTA);
            switch ((int) (Math.random() * 2)) { //el visitante puede entregarnos el documento o no
                case 0:
                    System.out.printf("%s %n", "*El visitante le entrega su documento* ");
                    System.out.println(AZUL+ datosDeclarados[0] +" | "+ datosDeclarados[1] +" | "+ datosDeclarados[2] + RESET);

                    break;

                case 1:
                    System.out.printf("\s \n"+ "*El visitante no le entrega nada*");
                    break;
            }
            System.out.println(RESET);
            do {
                System.out.println(NEGRITA + AZUL+ "------------------------------------+"+ RESET);
                System.out.println(NEGRITA + "Opciones:                           |");
                System.out.printf("%-2s %-32s%n", "1.", "Consultar registro de residentes |");
                System.out.printf("%-2s %-32s | %n", "2.", "Pedir documento");
                System.out.printf("%-2s %-32s | %n", "3.", "Hacer una pregunta");
                System.out.printf("%-2s %-32s | %n", "4.", "Llamar al apartamento");
                System.out.printf("%-2s %-32s | %n", "5.", "Apuntar pista");
                System.out.printf("%-2s %-32s | %n", "6.", "Tomar decisión");
                System.out.printf("%-2s %-32s | %n", "0.", "Abandonar jornada");
                System.out.println(NEGRITA + AZUL+ "------------------------------------+"+ RESET);
                System.out.print(NEGRITA +"Opción: ");
                opcion = Integer.parseInt(leer.readLine());

                System.out.print(RESET);

                if (opcion!=0) {
                    switch (opcion) {
                            case 1:
                                System.out.println(NEGRITA+ AMARILLO+ "=============REGISTRO DE RESIDENTES================"+ RESET);
                                System.out.print(NEGRITA);
                                System.out.printf("%-20s| %-5s| %-10s| %-5s |%n", "NOMBRE", "PISO", "DNI", "TELÉFONO");
                                System.out.println("---------------------------------------------------");
                                System.out.print(RESET);
                                for (int i = 0; i < NUM_RESIDENTES; i++) {
                                System.out.printf("%-20s| %-5s| %-10s| %-8s |%n", nombres[i], pisos[i], dnis[i], telefonos[i]);

                                }
                                System.out.print(NEGRITA);
                                System.out.println("---------------------------------------------------");

                                break;
                        
                            case 2:
                                System.out.println(NEGRITA+ CIAN+ "PORTERO: "+ RESET +"Por favor, entrégueme su documento");
                                
                                System.out.println(NEGRITA+ AZUL+ "VISITANTE: "+RESET +"Perdona, aquí tiene ");
                                System.out.println(AZUL+ datosDeclarados[0] +" | "+ datosDeclarados[1] +" | "+ datosDeclarados[2] + RESET);

                                break;
                            case 3:
                                break;
                            case 4:
                                System.out.println("Escriba el número de teléfono del apartamento: ");
                                numeroTelefono = leer.readLine();

                                if (numeroTelefono.equals("1010")) {
                                System.out.println("Llamando...");

                                    switch ((int)(Math.random()*3)) { //1--> No contesta nadie 2--> Contesta alguien
                                        
                                    }
                                    
                                }else if (numeroTelefono.equals("2020")) {
                                    System.out.println("Llamando...");
                                    switch ((int)(Math.random()*3)) { //1--> No contesta nadie 2--> Contesta alguien

                                        case 1:
                                            System.out.println("Sin respuesta");
                                            break;
                                        case 2:
                                            System.out.println(AZUL + NEGRITA + nombres[2] + RESET +" : ¿ Diga ?");
                                            System.out.println(CIAN + NEGRITA + "PORTERO: "+ RESET + "¿ Está "+ datosDeclarados[0] + " en casa ?");
                                            if (esImpostor) {
                                                System.out.println(AZUL + NEGRITA + nombres[2] +": " + RESET +": Sí, aquí está.");
                                            } else {
                                                System.out.println(AZUL + NEGRITA + nombres[2] +": " + RESET +"No, ha salido.");
                                            }
                                            break;
                                    
                                    }
                                }else if (numeroTelefono.equals("3030")) { //1--> COntesta la única persona que vive aquí 2--> No contesta nadie
                                System.out.println("Llamando...");
                                    
                                }

                                break;
                            case 5:

                                break;
                            case 6:
                                
                                break;
                        }
                    
            }

            } while (opcion != 0);

                

        }

    }
}
