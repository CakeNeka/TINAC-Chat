<h1 align="center"> T.I.N.A.C. </h1>

<pre align="center">
_______________________
/\                      \
\_|        TINAC        |
  |      This           |
  |      Is             |
  |      Not            |
  |      A              |
  |      Chat           |
  |                     |
    |   __________________|__
    \_/____________________/
</pre>

Un chat **cliente-servidor** desarrolado en Java utilizando `Socket` y `ServerSocket`.

No es muy útil porque solo es posible ejecutar servidor y clientes en un
mismo equipo. Con algunas modificaciones puede funcionar en red local.

## ⚙ Ejecución

### Dependencias 

Este proyecto utiliza la librería [flatlaf-3.3](https://mvnrepository.com/artifact/com.formdev/flatlaf)
para mejorar el aspecto de la interfaz gráfica.

### Desde IntelliJ IDEA

Es el IDE que estoy utilizando para desarrollar el proyecto. 

- **Permitir la ejecución de multiples instancias de Cliente**
    1. Ir a la clase **Cliente**
    2. Arriba a la derecha, elegir clase `Client` e ir a `Edit Configurations`
    3. `Build and run -> Modify options -> Allow multiple instances`

> [!NOTE]
> El código fuente ha sido comentado =)

## ✨ Funcionalidades

### Básico

- Cliente-Servidor
- Sala de chat abierta
- Identificar al cliente con el último octeto de su IP

### Mejoras

- Cliente escribe y recibe mensajes **simultáneamente**
- Los mensajes se almacenan en una estructura de almacenamiento, proceso de **recuperación**
- Sistema de **Comandos**
- **Autenticación** de usuarios
- Creación de **salas**
- Interfaz gráfica
- **Registrar los mensajes**
- **Cifrado** de Mensajes

## 📚 Recursos utilizados

El proyecto parte de los apuntes de clase acerca de programación en red, 
también he utilizado el código proporcionado en **Acceso a datos** para la encriptación de mensajes.

---

```yaml
Módulo: Programación de Servicios y Procesos
Lenguaje: Java
Tema: UT3: Programación de Comunicaciones en Red
Herramientas: 
  - IntelliJ Idea CE 2023.2.2
  - JDK 21
Fecha: 2024-02-15
```

