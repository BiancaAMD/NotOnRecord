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
        final int NUM_RESIDENTES   = 5;
        final int PUNTOS_ACIERTO   = 100;
        final int PENALIZACION     = 50;  

        // ========================== VARIABLES ===========================
        //empleados por el usuario
        String numeroTelefono;

        // Estado de la partida
        int puntos;
        int errores;
        int aciertos;

        // Visitante actual
        int posResidente, posConviviente;          // posición del residente del que cogemos datos (0 a NUM_RESIDENTES - 1) y saber si en el piso hay alguien más
        boolean esImpostor;        // si es impostor o no
        boolean convivienteEnCasa; // indica si el conviviente está en casa

        // Control del menú
        String opcion;
        boolean decidido;         // true cuando se da el veredicto y acaba el turno

        //=========DUELO============
        int saludPortero, saludImpostor, dano, refuerzosRestantes;
        String accionDuelo;
        final int SALUD_INICIAL = 100;
        final int DANO_MAX = 50; //daño max que se puede hacer

        // ===================== ARRAYS: REGISTRO =========================
        // Arrays paralelos: la misma posición describe al mismo residente
        String[] nombres   = new String[NUM_RESIDENTES];
        String[] pisos     = new String[NUM_RESIDENTES];
        String[] dnis      = new String[NUM_RESIDENTES];
        String[] telefonos = new String[NUM_RESIDENTES];

        
        //ABECEDARIO --> nos servirá para , en el caso de que el DNI sea incorrecto, generar una letra mala
        String[] abecedario = {"A", "B", "C", "D", "E", "F", "H"};

        // ================= ARRAYS: DATOS DEL VISITANTE ==================
        // Posiciones: [0] nombre, [1] piso, [2] DNI
        String[] datosDeclarados = new String[3]; //array con el que tomamos datos de los arrays paralelos, con esto formamos el documento del visitante
        String[] nombresCambiados = new String[NUM_RESIDENTES]; //esta nos ayuda a cambiar los nombres en el caso de que sea un impostor 

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

        //NOMBRES COMO PISTAS
        nombresCambiados[0] = "Julian Ramirez";
        nombresCambiados[1] = "karla Leal";
        nombresCambiados[2] = "Sonio Doñoro";
        nombresCambiados[3] = "Fron Pardo";
        nombresCambiados[4] = "Cristian Casado";


        //============INTRO================
        System.out.println("");
        System.out.println("█   █  ███  █████     ███  █   █    ████  █████  ███   ███  ████  ████  \r\n" + //
                        "██  █ █   █   █      █   █ ██  █    █   █ █     █     █   █ █   █ █   █ \r\n" + //
                        "█ █ █ █   █   █      █   █ █ █ █    ████  ████  █     █   █ ████  █   █ \r\n" + //
                        "█  ██ █   █   █      █   █ █  ██    █  █  █     █     █   █ █  █  █   █ \r\n" + //
                        "█   █  ███    █       ███  █   █    █   █ █████  ███   ███  █   █ ████  ");
                     
        String opcionPrincipal;

        do {
            System.out.println("");
            System.out.println(NEGRITA + AMARILLO + "========== MENÚ PRINCIPAL ==========" + RESET );
            System.out.println("1. Empezar partida");
            System.out.println("2. Cómo jugar");
            System.out.println("3. Salir del juego");
            System.out.print(NEGRITA + "Opción: " + RESET);
            opcionPrincipal = leer.readLine();

            switch (opcionPrincipal) {
                case "1": {
                    puntos = 0;
                    errores = 0;
                    aciertos = 0;
                    saludPortero = SALUD_INICIAL;
                    refuerzosRestantes = 2;
                    boolean partidaTerminada = false; //pasa a true si perdemos (3 errores) o abandonas
                    

                    //============JUEGO================

                    
                    for (int contador = 0; contador < NUM_RESIDENTES && !partidaTerminada; contador++) {

                        // GENERAR UN VISITANTE E INDICAR SI ES IMPOSTOR O NO
                        posResidente = (int) (Math.random() * NUM_RESIDENTES);
                        esImpostor = Math.random() < 0.50; // si sale un num menor que 0.5 será impostor

                        //CONDICIONES: reiniciamos para que no queden restos
                        boolean llamadaRealizada = false;    
                        decidido = false;                    

                        //RELLENAMOS LOS DATOS QUE EL VISITANTE NOS VA A DAR, LO COPIAMOS DEL REGISTRO DE LA POSICION DE RESIDENTE QUE HEMOS RECOGIDO ANTES
                        //NOTA: [0] es nombre, [1] es piso y [2] es DNI
                        datosDeclarados[0] = nombres[posResidente];
                        datosDeclarados[1] = pisos[posResidente];
                        datosDeclarados[2] = dnis[posResidente];

                        //buscamos al otro residente del mismo piso y generamos si estará en casa o no
                        posConviviente = -1; // -1 significa nadie: ninguna posición de un array puede valer -1. Suponemos que vive solo porque hay casos así
                        for (int i = 0; i < NUM_RESIDENTES; i++) {
                             if (pisos[i].equals(pisos[posResidente]) && i != posResidente) { //con la segunda parte nos aseguramos de no coger al propio visitante
                                posConviviente = i;
                            }
                        }
                        convivienteEnCasa = posConviviente != -1 && Math.random() < 0.5; //si no vive solo y el num random es menor q 0.5 hay conviviente


                        //EN EL CASO DE QUE SEA IMPOSTOR: uno de los tres datos se altera o no
                        if (esImpostor) {
                            int tipoImpostor = (int) (Math.random()*4); //esto lo que hace es dar 0, 1 , 2 o 3 , que son nuestros 3 datos que podemos alterar y la posibilidad de no alterar

                             //un impostor puede vivir solo, por lo que la llamada puede no ayudar, hacemos que tenga una pista si o si
                            if (tipoImpostor==3 && posConviviente==-1) { 
                                tipoImpostor = (int)(Math.random()*3);
                            }

                            switch (tipoImpostor) { 

                                case 0:
                                    datosDeclarados[0] = nombresCambiados[posResidente];
                                    break;

                                case 1:
                                    int pisoRandom;
                                    do {
                                        pisoRandom = (int) (Math.random() * 4); //para que salga un piso entre 0 y 3

                                    } while (pisoRandom == Integer.parseInt(datosDeclarados[1]));

                                    datosDeclarados[1] = String.valueOf(pisoRandom);
                                break;
                                    
                                case 2:
                                    int posicionLetraRandom = (int)(Math.random()*7);
                                    datosDeclarados[2] = abecedario[posicionLetraRandom]+ dnis[posResidente];
                                    break;

                                case 3:
                                    //no alteramos nada
                                    //conviviente siempre en casa para tener pistas
                                    convivienteEnCasa = true;
                                break;

                            }
                        }                            
                        
         
                            //==============TURNOS==================
                            System.out.println("");
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
                                System.out.printf("%-2s %-32s | %n", "3.", "Llamar al apartamento");
                                System.out.printf("%-2s %-32s | %n", "4.", "Tomar decisión");
                                System.out.printf("%-2s %-32s | %n", "0.", "Abandonar jornada");
                                System.out.println(NEGRITA + AZUL+ "------------------------------------+"+ RESET);
                                System.out.print(NEGRITA +"Opción: ");
                                opcion = leer.readLine();

                                System.out.print(RESET);

                                if (!opcion.equals("0")) {
                                    switch (opcion) {
                                            case "1":
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
                                                System.out.print(RESET);
                                                break;
                                        
                                            case "2":
                                                System.out.println(NEGRITA+ CIAN+ "PORTERO: "+ RESET +"Por favor, entrégueme su documento");
                                                
                                                System.out.println(NEGRITA+ AZUL+ "VISITANTE: "+RESET +"Perdona, aquí tiene ");
                                                System.out.println(AZUL+ datosDeclarados[0] +" | "+ datosDeclarados[1] +" | "+ datosDeclarados[2] + RESET);

                                                break;
                                            case "3":
                                                if (llamadaRealizada==false) {
                                                
                                                    System.out.println("Escriba el número de teléfono del apartamento: ");
                                                    numeroTelefono = leer.readLine();
                                                    System.out.println("Llamando...");

                                                    llamadaRealizada = true;

                                                    if (numeroTelefono.equals(telefonos[posResidente]) && convivienteEnCasa) {
                                                    System.out.println(AZUL + NEGRITA + nombres[posConviviente] + ": " + RESET + "¿Diga?");
                                                    System.out.println(CIAN + NEGRITA + "PORTERO: " + RESET + "¿Está " + nombres[posResidente] + " en casa?");
                                                    
                                                        if (esImpostor) {
                                                            System.out.println(AZUL + NEGRITA + nombres[posConviviente] + ": " + RESET + "Sí, aquí está.");
                                                        } else {
                                                            System.out.println(AZUL + NEGRITA + nombres[posConviviente] + ": " + RESET + "No, ha salido.");
                                                        }
                                                    } else {
                                                        System.out.println("Sin respuesta");
                                                    }
                                                } else {
                                                    System.out.println("Ya ha llamado");
                                                }
                                            break;
                                            case "4":
                                            String decision;
                                
                                                do {
                                                    
                                                    System.out.println("OPCIÓN A: Dejar pasar");
                                                    System.out.println("OPCIÓN B: Rechazar entrada");
                                                    System.out.println("Seleccione una opción: ");
                                                    decision = leer.readLine();
                                                    decidido = true;
                                                    if (!decision.equalsIgnoreCase("A") && !decision.equalsIgnoreCase("B")) {
                                                        System.out.println(ROJO+ NEGRITA+ "Tome una decisión válida"+ RESET);
                                                    }

                                                } while (!decision.equalsIgnoreCase("A") && !decision.equalsIgnoreCase("B"));

                                                if (decision.equalsIgnoreCase("A")) { //le da igual mayusc. que minusc.
                                                    System.out.println(CIAN+ NEGRITA+ "PORTERO: "+ RESET+ "puede pasar.");
                                                    System.out.println(MAGENTA+ "*has dejado pasar al visitante"+ RESET);
                                                    if (esImpostor) {
                                                        System.out.println(ROJO+ "Has fallado, "+ RESET+ "era un impostor");
                                                        errores = errores + 1;
                                                        puntos = puntos - PENALIZACION * errores; //penalización creciente
                                                        System.out.println(ROJO+ "Errores: "+ errores +"/"+ MAX_ERRORES+ RESET);

                                                    }else {
                                                        System.out.println(VERDE+ "Has acertado, "+ RESET +"era un residente");
                                                        aciertos = aciertos + 1;
                                                        puntos = puntos + PUNTOS_ACIERTO;
                                                    }

                                                }else if (decision.equalsIgnoreCase("B")) {
                                                   System.out.println(CIAN + NEGRITA +"PORTERO: "+ RESET +"eres un impostor!!");

                                                    if (esImpostor) {
                                                        switch ((int)(Math.random()*2)) {
                                                            case 0: //huye
                                                                System.out.println(MAGENTA+ "*EL IMPOSTOR HA SALIDO CORRIENDO*");
                                                                aciertos = aciertos + 1;
                                                                puntos = puntos + PUNTOS_ACIERTO;
                                                                decidido = true;
                                                                break;
                                                        
                                                            case 1: //ataca
                                                                saludImpostor = SALUD_INICIAL;

                                                                System.out.println(ROJO+NEGRITA +"*EL IMPOSTOR TE ESTÁ ATACANDO*"+ RESET);
                                                                while (saludPortero>0 && saludImpostor>0) {
                                                                    boolean bloqueando = false;
                                                                    System.out.println("Tu salud: "+ saludPortero + " | Salud del impostor: " + saludImpostor);
                                                                    System.out.println("1. Atacar");
                                                                    System.out.println("2. Bloquear ataque");
                                                                    System.out.println("3. Pedir refuerzos ("+ refuerzosRestantes +")");
                                                                    System.out.print("Acción: ");
                                                                    accionDuelo = leer.readLine();

                                                                    switch (accionDuelo) {
                                                                        //nuestro turno
                                                                        case "1":
                                                                            dano = (int) (Math.random()*DANO_MAX) + 1;
                                                                            saludImpostor = saludImpostor - dano;
                                                                            System.out.println("Has golpeado al impostor");
                                                                            System.out.println(ROJO + NEGRITA +"-"+dano + RESET);

                                                                            break;
                                                                    
                                                                        case "2":
                                                                            bloqueando = true;
                                                                            System.out.println("*te proteges del siguiente golpe*");
                                                                            break;
                                                                        case "3":
                                                                            if (refuerzosRestantes > 0) {
                                                                                refuerzosRestantes = refuerzosRestantes - 1;
                                                                                saludImpostor = 0;
                                                                                System.out.println("*han llegado los refuerzos y han reducido al impostor*");
                                                                            }else {
                                                                                System.out.println("*no quedan refuerzos*");
                                                                            }
                                                                            break;
                                                                        default:
                                                                            System.out.println("Opción no válida, pierdes el turno");
                                                                    }

                                                                    //turno del impostor
                                                                    if (saludImpostor>0) {
                                                                        dano = (int) (Math.random()*DANO_MAX) + 1;
                                                                        if (bloqueando) {
                                                                            dano = dano/2; //te hace menos daño
                                                                        }
                                                                        saludPortero = saludPortero - dano;
                                                                        System.out.println(ROJO+ "El impostor te ha golpeado "+ RESET);
                                                                        System.out.println(ROJO + NEGRITA +"-"+dano + RESET);
                                                                    }
                                                    
                                                                }
                                                            //resultados
                                                                if (saludPortero<=0) {
                                                                    saludPortero = 0;
                                                                    errores = errores + 1;
                                                                    System.out.println(ROJO+ "El impostor te ha derrotado"+ RESET);
                                                                    partidaTerminada = true;
                                                                    puntos = puntos - PENALIZACION * errores;
                                                                }else {
                                                                    System.out.println(VERDE+ "Has reducido al impostor"+ RESET);
                                                                    aciertos = aciertos +1;
                                                                    decidido = true;
                                                                    puntos = puntos + PUNTOS_ACIERTO;
                                                                }
                                                            break;
                                                        }
                                                    
                                                    }else {
                                                        System.out.println(ROJO + "Has fallado, "+ RESET +"era un residente");
                                                        errores = errores + 1;
                                                        puntos = puntos - PENALIZACION * errores;
                                                        System.out.println(ROJO + "Errores: " + errores + "/" + MAX_ERRORES + RESET);
                                                    }

                                                }

                                                if (errores==MAX_ERRORES) { //acaba partida
                                                    System.out.println(ROJO+"HAS MUERTO"+RESET);
                                                    partidaTerminada = true;
                                                }
                                                
                                                break; 
                                            default:
                                            System.out.println("Elija una opción válida");
                                            break;   
                                        }
                                            
                                    
                                }

                            } while (!opcion.equals("0") && !decidido);

                            if (opcion.equals("0")) {
                                System.out.println("Has abandonado la jornada");
                                partidaTerminada = true; //corta el bucle de turnos
                            }

                        
                }

                    //RESUMEN CON ESTADISTICAS
                    System.out.println(NEGRITA+AMARILLO+ "========== FIN DE LA JORNADA =========="+ RESET);
                    
                    System.out.println("Puntos: "+ puntos);
                    System.out.println("Errores: "+ errores +"/"+ MAX_ERRORES);
                    System.out.println("Aciertos: "+ aciertos+"/"+ NUM_RESIDENTES);
                    if (errores==MAX_ERRORES || saludPortero<=0 ) {
                        System.out.println(ROJO+ NEGRITA+ "GAME OVER"+ RESET);
                    }else if (puntos>=400) {
                       System.out.println(VERDE+ "Valoración: EXPERTO"+ RESET); 
                    }else if (puntos>=200) {
                        System.out.println(AZUL+ "Valoración: PROFESIONAL"+ RESET);
                    }else {
                        System.out.println(AMARILLO+ "Valoración: NOVATO"+ RESET);
                    }

                    break;
                }
                case "2":
                    System.out.println(CIAN + "Eres el portero de un edificio: investiga a cada visitante con las opciones del menú y decide si entra o es un impostor." + RESET);
                    System.out.println(CIAN + "Acertar suma puntos, fallar suma un error y con " + MAX_ERRORES + " errores pierdes." + RESET);
                        break;
                case "3":
                    System.out.println("Saliendo del juego.");
                        break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (!opcionPrincipal.equals("3"));

    }
}
