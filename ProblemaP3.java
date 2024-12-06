import java.util.*;

public class ProblemaP3 {

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

    private static Map<Integer, Map<Integer, Integer>> grafo;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int casos = Integer.parseInt(sc.nextLine().trim());
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

            grafo = construirGrafo(celulas, d);

            Map<Integer, Integer> asignaciones = particionarEnCliquesPorGrado(grafo);
        }
        sc.close();
    }

    private static Map<Integer, Map<Integer, Integer>> construirGrafo(List<Celula> celulas, double d) {
        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        for (Celula c : celulas) {
            g.put(c.id, new HashMap<>());
        }

        // Grafo no dirigido
        for (Celula origen : celulas) {
            for (Celula destino : celulas) {
                if (origen == destino) continue;
                double distancia = Math.hypot(origen.x - destino.x, origen.y - destino.y);
                if (distancia <= d + 1e-8) {
                    Set<String> comunes = new HashSet<>(origen.peptidos);
                    comunes.retainAll(destino.peptidos);
                    int capacidad = comunes.size();
                    if (capacidad > 0) {
                        g.get(origen.id).put(destino.id, capacidad);
                        g.get(destino.id).put(origen.id, capacidad);
                    }
                }
            }
        }
        return g;
    }

    // Ahora ordenamos los nodos por cantidad de aristas de mayor a menor.
    // En caso de empate en el grado, se ordenan por ID ascendente.
    private static Map<Integer, Integer> particionarEnCliquesPorGrado(Map<Integer, Map<Integer, Integer>> grafo) {
        Map<Integer, Integer> asignaciones = new HashMap<>();
        
        // Crear la lista de nodos y ordenarla
        List<Integer> nodos = new ArrayList<>(grafo.keySet());
        nodos.sort((a, b) -> {
            int diff = grafo.get(b).size() - grafo.get(a).size();
            if (diff != 0) return diff;
            return a - b; // desempate por ID
        });
        
        Set<Integer> noAsignados = new HashSet<>(nodos);

        int grupoId = 1;

        while (!noAsignados.isEmpty()) {
            // Tomar el siguiente nodo no asignado (el primero en la lista 'nodos' que esté en noAsignados)
            Integer start = null;
            for (Integer candidato : nodos) {
                if (noAsignados.contains(candidato)) {
                    start = candidato;
                    break;
                }
            }

            // Formar el clique
            Set<Integer> clique = new HashSet<>();
            clique.add(start);

            // 'candidatos' será el conjunto de nodos conectados a 'start'
            Set<Integer> candidatos = new HashSet<>(grafo.get(start).keySet());
            candidatos.retainAll(noAsignados);

            // Intentar agregar otros nodos al clique
            for (Integer posible : nodos) {
                if (!noAsignados.contains(posible) || posible.equals(start)) continue;
                // Verificar si posible está en candidatos
                if (candidatos.contains(posible)) {
                    clique.add(posible);
                    // Intersecar candidatos con los vecinos de posible
                    Set<Integer> vecinosPosible = grafo.get(posible).keySet();
                    candidatos.retainAll(vecinosPosible);
                }
            }

            // Asignar el grupo a todos los del clique
            for (Integer miembro : clique) {
                asignaciones.put(miembro, grupoId);
                System.out.println(miembro + " " + grupoId);
            }

            // Removerlos de noAsignados
            noAsignados.removeAll(clique);

            grupoId++;
        }

        return asignaciones;
    }
}
