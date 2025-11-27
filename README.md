[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/IEOUmR9z)

# Trabajo Final: Sistema de Registro de Asistencia a Eventos

Este repositorio contiene el desarrollo del trabajo práctico para la regularización de la materia, cuyo objetivo es construir un sistema para registrar la asistencia y gestionar la venta de asientos para eventos únicos (charlas, cursos, obras de teatro, etc.)

---

##  Arquitectura del Sistema

El sistema sigue una arquitectura de microservicios e interactúa con servicios externos provistos por la cátedra. Está dividido en los siguientes componentes principales:


### 1. Servicios de la Cátedra (Cátedra)

Servicios externos provistos por la cátedra que exponen _endpoints_ para listados, ventas, y bloqueos.
* **Servicio Principal (Java):** Expone la API principal.
* **Kafka:** Servicio de mensajería para notificar cambios en la información de los eventos.
* **Redis:** Base de datos en memoria utilizada para mantener la información actualizada del estado de los asientos de cada evento.

### 2. Servicios del Alumno

Componentes a desarrollar:

* **Backend (Java/SpringBoot - preferentemente JHipster):**
    * Backend principal que interactúa con el **Cliente Móvil** y el **Servicio de la Cátedra**.
    * Mantiene sincronizados los eventos de la cátedra mediante la lectura de Kafka (a través del Proxy).
    * Gestiona sesiones de usuario con Redis local.
    * Persiste localmente eventos y ventas en base de datos (My, Redis).
* **Proxy (Java):**
    * Único componente con acceso directo a **Kafka** y **Redis** de la cátedra.
    * Sirve como intermediario entre el **Backend** y estos servicios.
    * Consulta el estado de los asientos en el Redis de la cátedra a petición del Backend.
    * Está suscripto al tópico Kafka y debe notificar al Backend sobre cambios en los eventos.
* **Cliente Móvil (Kotlin Multiplatform - KMP):**
    * Interfaz gráfica del Backend.
    * Permite al usuario listar eventos, seleccionar asientos (hasta 4 por sesión), cargar datos de personas y realizar la compra (venta).
    * Debe manejar sesiones que se retoman incluso al cambiar de dispositivo.

---

##  Autenticación y Comunicación

* Toda la comunicación entre servicios se realiza mediante **JSON**.
* Los servicios deben estar autenticados utilizando **JWT (JSON Web Tokens)**, tanto entre el Backend y la Cátedra, como entre el Backend y el Proxy.