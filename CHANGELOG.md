# Changelog

En este archivo documentaré el proceso de desarrollo.

## 2024-01-24

### Añadido

- Nuevo repositorio
- Añadido Chat base realizado como tarea
- Programa **cliente**
    - Acepta entrada del usuario por teclado y simultáneamente recibe mensajes 
      del servidor
- Programa **servidor**
    - Cuando acepta la conexión de un cliente, **lanza un hilo y abre 
      un puerto** para gestionar esta nueva conexión.
    - Al recibir un mensaje de un cliente, retransmite ese mensaje a todos 
      los clientes.
    - Permite al programa cliente identificarse con un nombre
      de usuario
