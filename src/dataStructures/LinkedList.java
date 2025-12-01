package dataStructures;

import java.util.Objects;

public class LinkedList {

    // Clase interna para representar un Nodo
    public class Node {
        Integer data1;
        Integer data2;
        Integer data3;
        Node next;

        public Node(Integer dato1, Integer dato2, Integer dato3) {
            this.data1 = dato1;
            this.data2 = dato2;
            this.data3 = dato3;
            this.next = null;
        }

        public Integer getData1(){ return data1; }
        public Integer getData2(){ return data2; }
        public Integer getData3(){ return data3; }
        public Node getNext(){ return next; }
        public void setData3(Integer dato3){ this.data3 = dato3; }
    }

    private Node cabeza = null;

    // --- MÉTODOS DE LA LISTA ---
    /**
     * Añade un nodo al principio de la lista.
     * @param dato1 El valor a añadir.
     * @param dato2 El valor a añadir.
     */
    public void append(Integer dato1, Integer dato2, Integer dato3) {
        Node nuevoNodo = new Node(dato1, dato2, dato3);

        // Si la lista está vacía, el nuevo nodo es la cabeza y se apunta a sí mismo.
        if (cabeza == null) {
            cabeza = nuevoNodo;
            nuevoNodo.next = cabeza;
            return;
        }

        // Buscamos el último nodo de la lista (el que apunta a la cabeza).
        Node ultimo = cabeza;
        while (ultimo.next != cabeza) {
            ultimo = ultimo.next;
        }

        // El 'siguiente' del nuevo nodo será la antigua cabeza.
        nuevoNodo.next = cabeza;

        // La nueva cabeza de la lista es ahora el nuevo nodo.
        cabeza = nuevoNodo;

        // Finalmente, el último nodo debe apuntar a la nueva cabeza para cerrar el círculo.
        ultimo.next = cabeza;
    }

    /**
     * Borra un nodo de la lista identificado por clave1 y clave2. 🗑️
     * @param clave1 El primer dato para identificar el nodo a borrar.
     * @param clave2 El segundo dato para identificar el nodo a borrar.
     * @return true si el nodo fue encontrado y borrado, false en caso contrario.
     */
    public boolean delete(Integer clave1, Integer clave2) {
        if (cabeza == null) {
            return false;
        }

        // --- CASO 1: El nodo a borrar es la cabeza ---
        if (Objects.equals(cabeza.data1, clave1) && Objects.equals(cabeza.data2, clave2)) {
            // Si es el único nodo en la lista
            if (cabeza.next == cabeza) {
                cabeza = null;
            } else {
                // Si hay más nodos, necesitamos encontrar el último para reajustar su puntero
                Node ultimo = cabeza;
                while (ultimo.next != cabeza) {
                    ultimo = ultimo.next;
                }
                // La nueva cabeza será el siguiente nodo
                cabeza = cabeza.next;
                // El último nodo ahora apunta a la nueva cabeza
                ultimo.next = cabeza;
            }
            return true;
        }

        // --- CASO 2: El nodo a borrar no es la cabeza ---
        Node actual = cabeza.next;
        Node previo = cabeza;

        // Recorremos la lista para encontrar el nodo
        do {
            if (Objects.equals(actual.data1, clave1) && Objects.equals(actual.data2, clave2)) {
                // Nodo encontrado. El 'siguiente' del nodo previo apuntará al 'siguiente' del nodo actual.
                previo.next = actual.next;
                return true;
            }
            previo = actual;
            actual = actual.next;
        } while (actual != cabeza);

        // Si el bucle termina, significa que el nodo no se encontró en la lista.
        return false;
    }

    /**
     * Busca un elemento en la lista. 🔎
     * @param clave1 El valor a buscar.
     * @param clave2 El valor a buscar.
     * @return true si el elemento se encuentra, false en caso contrario.
     */
    public boolean find(Integer clave1, Integer clave2) {
        if (cabeza == null) {
            return false; // La lista está vacía
        }

        Node actual = cabeza;
        do {
            if (Objects.equals(actual.data1, clave1) && Objects.equals(actual.data2, clave2)) {
                return true; // ¡Elemento encontrado!
            }
            actual = actual.next;
        } while (actual != cabeza); // Repetir hasta dar una vuelta completa

        return false; // Si el bucle termina, no se encontró
    }

    /**
     * Muestra los elementos de la lista en la consola.
     */
    public void display() {
        if (cabeza == null) {
            System.out.println("La lista está vacía.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        Node actual = cabeza;
        do {
            sb.append(actual.data1).append(" -> ");
            sb.append(actual.data2).append(" -> ");
            actual = actual.next;
        } while (actual != cabeza);

        // Para mostrar que es circular
        sb.append("(cabeza: ").append(cabeza.data1).append(cabeza.data2).append(")");
        System.out.println(sb.toString());
    }

    public Node getHead() {
        return this.cabeza;
    }
}