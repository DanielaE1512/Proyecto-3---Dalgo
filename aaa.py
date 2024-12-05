import sys
import math
import networkx as nx
import matplotlib.pyplot as plt

def construir_grafo(celulas, d):
    # grafo representado con networkx
    G = nx.Graph()
    # Agregar nodos
    for c in celulas:
        G.add_node(c['id'], x=c['x'], y=c['y'], peptidos=c['peptidos'])

    # Agregar aristas
    for i in range(len(celulas)):
        for j in range(i+1, len(celulas)):
            c1 = celulas[i]
            c2 = celulas[j]
            distancia = math.hypot(c1['x'] - c2['x'], c1['y'] - c2['y'])
            if distancia <= d + 1e-8:
                # Intersección de péptidos
                comunes = c1['peptidos'].intersection(c2['peptidos'])
                capacidad = len(comunes)
                if capacidad > 0:
                    G.add_edge(c1['id'], c2['id'], capacidad=capacidad)
    return G

def particionar_en_cliques_determinista(G):
    # Obtener todos los nodos
    nodos = sorted(G.nodes())
    no_asignados = set(nodos)
    asignaciones = {}
    grupo_id = 1

    while no_asignados:
        # Tomar el nodo de menor ID no asignado
        start = None
        for candidato in nodos:
            if candidato in no_asignados:
                start = candidato
                break

        # Formar el clique
        clique = {start}
        # Intentar agregar otros nodos no asignados que formen un clique
        # con los ya agregados
        for posible in nodos:
            if posible in no_asignados and posible != start:
                # verificar si posible se conecta con todos en el clique
                if all(G.has_edge(posible, miembro) for miembro in clique):
                    clique.add(posible)

        # Asignar grupo
        for miembro in clique:
            asignaciones[miembro] = grupo_id

        # Removerlos de no_asignados
        no_asignados -= clique
        grupo_id += 1

    return asignaciones

def graficar_grafo(G, asignaciones):
    # Dibujar el grafo con networkx
    pos = nx.spring_layout(G, seed=42)  # layout para la posición de los nodos
    # Colorear los nodos por grupo
    grupos = [asignaciones[n] for n in G.nodes()]
    cmap = plt.cm.get_cmap('rainbow', max(grupos))

    nx.draw_networkx_nodes(G, pos, node_color=grupos, cmap=cmap, node_size=600)
    nx.draw_networkx_labels(G, pos, labels={n:str(n) for n in G.nodes()}, font_color='black')
    # Etiquetar aristas con capacidad
    edge_labels = {(u,v):d['capacidad'] for u,v,d in G.edges(data=True)}
    nx.draw_networkx_edges(G, pos)
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels)

    plt.title("Grafo del caso")
    plt.axis('off')
    plt.show()

def main():
    input_data = sys.stdin.read().strip().split('\n')
    casos = int(input_data[0].strip())
    idx = 1

    for _ in range(casos):
        linea = input_data[idx].split()
        idx += 1
        n = int(linea[0])
        d = float(linea[1])

        celulas = []
        for i in range(n):
            partes = input_data[idx].split()
            idx += 1
            cid = int(partes[0])
            x = float(partes[1])
            y = float(partes[2])
            peptidos = set(partes[3:])
            celulas.append({
                'id': cid,
                'x': x,
                'y': y,
                'peptidos': peptidos
            })

        G = construir_grafo(celulas, d)
        asignaciones = particionar_en_cliques_determinista(G)

        # Imprimir las asignaciones
        for cid in sorted(asignaciones.keys()):
            print(cid, asignaciones[cid])

        # Opcional: graficar el grafo
        # Descomenta la siguiente línea si deseas visualizar
        # graficar_grafo(G, asignaciones)

if __name__ == "__main__":
    main()
