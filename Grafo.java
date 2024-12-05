import java.util.*;

public class Grafo {

    static class Celula {
        int id;
        double x, y;
        Set<String> peptidos;

        public Celula(int id, double x, double y, Set<String> peptidos) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.peptidos = peptidos;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Leer el número de casos
        int casos = Integer.parseInt(sc.nextLine().trim());

        List<Map<Integer, Map<Integer, Integer>>> listaGrafos = new ArrayList<>();

        // Leer todos los casos antes de imprimir
        for (int c = 0; c < casos; c++) {
            String[] primeraLinea = sc.nextLine().trim().split("\\s+");
            int n = Integer.parseInt(primeraLinea[0]);
            double d = Double.parseDouble(primeraLinea[1]);

            List<Celula> celulas = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String[] partes = sc.nextLine().trim().split("\\s+");
                int id = Integer.parseInt(partes[0]);
                double x = Double.parseDouble(partes[1]);
                double y = Double.parseDouble(partes[2]);

                Set<String> peptidos = new HashSet<>();
                for (int j = 3; j < partes.length; j++) {
                    peptidos.add(partes[j]);
                }

                celulas.add(new Celula(id, x, y, peptidos));
            }

            Map<Integer, Map<Integer, Integer>> grafo = construirGrafo(celulas, d);
            listaGrafos.add(grafo);
        }

        // Ahora que se leyeron todos los casos y se construyeron todos los grafos,
        // procedemos a imprimirlos.
        for (int i = 0; i < listaGrafos.size(); i++) {
            Map<Integer, Map<Integer, Integer>> grafo = listaGrafos.get(i);
            System.out.println("Grafo del caso " + (i + 1) + ":");
            for (Map.Entry<Integer, Map<Integer, Integer>> entry : grafo.entrySet()) {
                System.out.print(entry.getKey() + ": ");
                for (Map.Entry<Integer, Integer> adj : entry.getValue().entrySet()) {
                    System.out.print("(" + adj.getKey() + ", " + adj.getValue() + ") ");
                }
                System.out.println();
            }
            System.out.println(); // Línea en blanco entre casos
        }
    }

    private static Map<Integer, Map<Integer, Integer>> construirGrafo(List<Celula> celulas, double d) {
        Map<Integer, Map<Integer, Integer>> grafo = new HashMap<>();

        for (Celula celula : celulas) {
            grafo.put(celula.id, new HashMap<>());
        }

        for (Celula origen : celulas) {
            for (Celula destino : celulas) {
                if (origen == destino) continue;

                double distancia = Math.hypot(origen.x - destino.x, origen.y - destino.y);

                if (distancia <= d + 1e-8) {
                    Set<String> peptidosCompartidos = new HashSet<>(origen.peptidos);
                    peptidosCompartidos.retainAll(destino.peptidos);
                    int capacidad = peptidosCompartidos.size();

                    if (capacidad > 0) {
                        grafo.get(origen.id).put(destino.id, capacidad);
                    }
                }
            }
        }

        return grafo;
    }
}
