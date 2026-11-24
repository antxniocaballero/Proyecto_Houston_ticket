package com.example.proyecto_houston_ticket.modelo;

/**
 * Enumeración que gestiona el ciclo de vida y la máquina de estados de un ticket.
 * 
 * Flujo unidireccional estricto:
 * ABIERTO -> EN_CURSO -> RESUELTO -> CERRADO
 */
public enum EstadoTicket {
    ABIERTO("Abierto"),
    EN_CURSO("En curso"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado");

    private final String etiqueta;

    EstadoTicket(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Mapea una cadena de texto al Enum correspondiente.
     */
    public static EstadoTicket deCadena(String estadoStr) {
        if (estadoStr == null || estadoStr.trim().isEmpty()) {
            return ABIERTO;
        }
        String e = estadoStr.trim().toUpperCase().replace(" ", "_");
        if (e.equals("EN_CURSO") || e.equals("ENCURSO") || e.equals("EN_PROCESO")) {
            return EN_CURSO;
        }
        if (e.equals("RESUELTO")) {
            return RESUELTO;
        }
        if (e.equals("CERRADO")) {
            return CERRADO;
        }
        return ABIERTO;
    }

    /**
     * Devuelve el siguiente estado en el flujo secuencial estricto.
     *
     * @return El siguiente EstadoTicket o null si ya se encuentra en CERRADO.
     */
    public EstadoTicket obtenerSiguienteEstado() {
        switch (this) {
            case ABIERTO:
                return EN_CURSO;
            case EN_CURSO:
                return RESUELTO;
            case RESUELTO:
                return CERRADO;
            case CERRADO:
            default:
                return null;
        }
    }

    /**
     * Valida si la transición hacia un nuevo estado respeta el orden estricto secuencial.
     *
     * @param nuevoEstado Estado al que se desea avanzar.
     * @return true si la transición es exactamente hacia el siguiente estado consecutivo.
     */
    public boolean esTransicionValida(EstadoTicket nuevoEstado) {
        if (nuevoEstado == null) return false;
        EstadoTicket siguiente = obtenerSiguienteEstado();
        return siguiente != null && siguiente == nuevoEstado;
    }
}
