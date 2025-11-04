# 🏠 Victus Residencias  
![Logo Victus Residencias](imagenes/logo.png)

## 📘 Presentación General

**Victus Residencias** es una plataforma integral para la **gestión de conjuntos residenciales**, desarrollada por estudiantes de la **Universidad Católica de Oriente (UCO)**.  
El proyecto implementa una **arquitectura distribuida basada en microservicios** desplegada en **Microsoft Azure**, con un enfoque en **seguridad, disponibilidad y escalabilidad**.

### 🌐 Arquitectura General

- **Frontend:** React (Vite + Tailwind + ShadCN/UI)
- **Backend:** Spring Boot 3.5.x (Java 21)
- **Base de Datos:** PostgreSQL (persistencia principal)
- **Infraestructura:** Azure App Service, Azure Database for PostgreSQL
- **Seguridad:** OAuth2 + JWT, Azure Key Vault, Web Application Firewall
- **Observabilidad:** Azure Monitor + Application Insights
- **Control de versiones:** GitHub
- **Despliegue:** CI/CD con GitHub Actions

---

## 🧩 Modelo de Clases
📎 **Ubicación:** `imagenes/modelo-clases.png`  
![Modelo de Clases](imagenes/modeloClases.png)

Representa las entidades principales del dominio:  
`ConjuntoResidencial`, `Residente`, `Reserva`, `Administrador`, `ZonaComún`, y sus relaciones.  
Define la base estructural del sistema y sus asociaciones.

---

## 🧮 Modelo MER (Entidad–Relación)
📎 **Ubicación:** `imagenes/modelo-mer.png`  
![Modelo MER](imagenes/modeloMER.png)

Este modelo refleja el esquema físico de la base de datos en PostgreSQL, definiendo llaves primarias, foráneas y relaciones 1:N y N:M entre las tablas del sistema.

---

## 🔄 Modelo de Actividades
📎 **Ubicación:** `imagenes/modelo-actividades.png`  
![Modelo de Actividades](imagenes/DiagramaActividades.png)

El diagrama de actividades representa el flujo de negocio para los procesos clave:
- Registro de conjuntos residenciales
- Registro y validación de residentes
- Reservas en zonas comunes
- Confirmación y seguimiento de turnos

---

## 🧱 Modelo de Objetos
📎 **Ubicación:** `imagenes/modelo-objetos.png`  
![Modelo de Objetos](imagenes/ModeloObjetos.png)

Describe instancias específicas de clases y cómo interactúan en tiempo de ejecución dentro de un escenario concreto de gestión residencial.

---

## ⚙️ Modelo de Estados
📎 **Ubicación:** `imagenes/modelo-estados.png`  
![Modelo de Estados](imagenes/DiagramaEstados.png)

Muestra los estados posibles de una **Reserva**:
- Pendiente  
- Confirmada  
- En uso  
- Finalizada  
- Cancelada  

---

## ☁️ Modelo de Despliegue


### 🧭 Arquitectura de referencia
📎 **Ubicación:** `imagenes/modelo-despliegue.png`  
![Modelo de Despliegue](imagenes/Arquitectura.png)
### 🧭 Arquetipo de referencia
📎 **Ubicación:** `imagenes/modelo-despliegue.png`  
![Modelo de Despliegue](imagenes/Arquetipo.png)

### 🧭 Arquitectura Distribuida
Victus Residencias adopta una **arquitectura distribuida con microservicios**, donde cada módulo (Usuarios, Reservas, Administración) se despliega en contenedores independientes dentro del entorno **Azure App Service**.

### 🧱 Bloques de Construcción Adoptados
- Spring Boot Framework  
- PostgreSQL JDBC Driver  
- Azure Key Vault Connector  
- React Frontend SPA  
- OAuth2 / JWT Authentication  

### 🧩 Bloques de Construcción Desarrollados
**Backend:**
![Backend Components](imagenes/bloqConstDesaBack.png)

**Frontend:**
![Frontend Components](imagenes/bloqConstDesaFront.png)

---

## 🧮 Modelo de Paquetes
📎 **Ubicación:** `imagenes/modelo-paquetes.png`  
![Modelo de Paquetes](imagenes/ModeloPaquetes.png)

### 📄 Documentación del Modelo de Paquetes
📎 **Ubicación:** `imagenes/documentacion-paquetes.png`  
![Documentación de Paquetes](imagenes/DocPaquetes.png)

El modelo de paquetes organiza la aplicación siguiendo una arquitectura **Hexagonal (Ports & Adapters)**, garantizando separación clara entre capas:
- **Domain:** lógica de negocio
- **Application:** casos de uso
- **Infrastructure:** persistencia, API REST, seguridad

---

## ⚙️ Modelo de Componentes
📎 **Ubicación:** `imagenes/modelo-componentes.png`  
![Modelo de Componentes](imagenes/ModeloComponentes.png)

### 📄 Documentación de Componentes
📎 **Ubicación:** `imagenes/documentacion-componentes.png`  
![Documentación de Componentes](imagenes/DocComponentes.png)

Se identifican:
- Componentes **externos**: Java 21, Spring Boot, PostgreSQL JDBC.  
- Componentes **propios**: Microservicio VictusResidencias, CrossCutting, ApplicationCore.

---

## 🔁 Modelo de Secuencia
📎 **Ubicación:** `imagenes/modelo-secuencia.png`  
![Modelo de Secuencia](imagenes/ModeloSecuencias.png)

El flujo describe la interacción entre los actores del sistema para el caso de uso:
**Registrar Conjunto Residencial**

1. El administrador ingresa los datos (nombre, dirección, ciudad, administrador).  
2. El frontend React envía un `POST /api/conjuntos-residenciales`.  
3. El backend verifica la existencia de ciudad y administrador.  
4. Se persiste el nuevo conjunto residencial en PostgreSQL.  
5. Se retorna una respuesta HTTP `200 OK`.

### 📄 Documentación del Modelo de Secuencia
📎 **Ubicación:** `imagenes/documentacion-secuencia.png`  
![Documentación de Secuencia](imagenes/DocSecuencia.png)

---

## ⚖️ Trade-Off
📎 **Ubicación:** `imagenes/trade-off.png`  
![Trade-Off](imagenes/trade-Off.png)

Este modelo compara las decisiones arquitectónicas en términos de **seguridad, rendimiento, disponibilidad y mantenibilidad**, priorizando la integración en Azure y la modularidad del código.

---

## 🧠 Mapa de Empatía
📎 **Ubicación:** `imagenes/mapa-empatia.png`  
![Mapa de Empatía](imagenes/MapaEmpatia.png)

El mapa identifica las percepciones, frustraciones y necesidades de los **residentes**, **administradores** y **personal de portería**, garantizando una experiencia centrada en el usuario.

---

## 🎯 Escenarios de Calidad

| Atributo de Calidad | Característica | Identificador | Nombre | Tipo | Objetivo | Descripción | Criterio de Éxito | Fuente del Estímulo | Estímulo | Ambiente | Artefacto | Respuesta | Medida de la Respuesta | Cumplió | Nombre Táctica | Descripción Estrategia |
|----------------------|----------------|----------------|---------|------|------------|----------------|-------------------|----------------------|-----------|------------|-------------|------------|------------------------|-----------|------------------|------------------------|
| Seguridad | Arquitectura de Confianza Distribuida | SEG-CAL_0001 | Control de Acceso Basado en Roles | Preventivo | Garantizar que solo los usuarios tengan los privilegios apropiados para acceder a funciones administrativas críticas. | El sistema debe validar continuamente que los usuarios tengan los privilegios necesarios para acceder a funciones administrativas como la gestión de conjuntos residenciales y usuarios. | Solo usuarios con roles apropiados pueden ejecutar acciones administrativas críticas. | Usuario autenticado del sistema. | Solicitud de acceso a función crítica. | Sistema operativo con usuarios de diferentes niveles de privilegio. | Módulo de autorización y gestión de roles. | Concesión o denegación de acceso según privilegios. | Tiempo de validación < 200 ms; tasa de error = 0%. | No | Uso de JWT + OAuth 2.0 | Permite control de acceso basado en roles utilizando tokens como moneda de intercambio. |
| Seguridad | Gobernanza de Cumplimiento Integral | SEG-CAL_0002 | Validación Continua de Cumplimiento Regulatorio | Detectivo | Asegurar el cumplimiento continuo de regulaciones y normas de protección de datos. | El sistema debe monitorear el cumplimiento de GDPR, CCPA y normativas locales, generando alertas ante desviaciones. | Cumplimiento regulatorio ≥ 100 %. | Auditor interno automatizado. | Verificación periódica de cumplimiento. | Sistema operativo con procesamiento continuo de datos personales. | Motor de cumplimiento regulatorio. | Validación automática y aplicación de correcciones. | Frecuencia de verificación automática cada hora. | No | Bitácoras de Auditoría | Mantiene trazabilidad de permisos y acciones críticas para auditorías. |
| Seguridad | Paradigma de Privacidad por Diseño | SEG-CAL_0003 | Protección de Información Personal de Residentes | Preventivo | Garantizar que los datos de los residentes estén cifrados y protegidos contra accesos no autorizados. | El sistema debe cifrar toda la información sensible en tránsito y en reposo. | Los intentos de acceso no autorizados se registran y bloquean. | Atacante externo o interno. | Intento de acceso sin credenciales válidas. | Sistema con múltiples usuarios activos. | Base de datos y módulo de autenticación. | El sistema niega acceso, registra intento y notifica al administrador. | Tiempo de detección ≤ 2 s; tasa de bloqueos 100 %. | No | Cifrado de Tránsito | Cifra la conversación entre el usuario y el servidor durante el intercambio de información. |
| Seguridad | Resiliencia ante Amenazas Adaptativas | SEG-CAL_0004 | Gestión Segura de Sesiones de Usuario | Detectivo | Proteger las sesiones activas ante comportamientos sospechosos. | El sistema detecta actividades anómalas en sesiones activas. | Sesiones comprometidas se terminan y notifican. | Sesión comprometida o intento de suplantación. | Comportamiento inusual en sesión. | Sistema con múltiples sesiones concurrentes activas. | Sistema de gestión de sesiones y detección de anomalías. | Terminación forzosa de sesión y reautenticación. | Tiempo de detección < 10 s; tasa de falsos positivos < 2 %. | No | Detección de Anomalías | Usa API Key y patrones de comportamiento para mitigar accesos maliciosos. |
| Seguridad | Resiliencia ante Amenazas Adaptativas | SEG-CAL_0005 | Identificación Temprana de Amenazas Emergentes | Proactivo | Detectar amenazas nuevas y responder automáticamente. | El sistema debe usar machine learning para identificar patrones de ataque. | Sistema mitiga amenaza antes de impacto. | Algoritmo de aprendizaje automático. | Análisis de patrones de tráfico malicioso. | Sistema bajo condiciones normales de tráfico. | Motor de análisis de seguridad. | Ajuste automático de políticas de seguridad. | Precisión ≥ 85 %. | No | Web Application Firewall | Servicio API para detectar y bloquear patrones de ataque antes del impacto. |
| Disponibilidad | Estabilidad del Servicio en Alta Demanda | DISP-CAL_0013 | Estabilidad bajo Carga Máxima de Reservas | Preventivo | Garantizar rendimiento y respuesta bajo alta demanda. | El sistema mantiene tiempos de respuesta aceptables durante carga simultánea. | 99,5 % de solicitudes procesadas sin error. | Múltiples residentes. | Solicitudes simultáneas de reserva. | Sistema en operación normal. | Módulo del sistema de reservas. | Actualización en tiempo real. | Latencia < 4 s; disponibilidad del 100 %. | No | Gestión de Rendimiento | Balanceo de carga y caché para reducir latencia. |
| Disponibilidad | Resiliencia ante Interrupciones Críticas | DISP-CAL_0014 | Resiliencia de la Sesión ante Inestabilidad de Red | Detectivo | Asegurar continuidad del servicio ante fallos de red. | El sistema mantiene sesión del usuario activa durante pérdidas de conexión. | Reanudación automática. | Usuario final. | Desconexión breve (5–10 s). | Sistema de gestión de sesiones. | Reintento automático de conexión. | Reanudación en < 30 s. | No | Tolerancia a Fallos | Tokens persistentes y reconexión automática transparente. |
| Disponibilidad | Resiliencia ante Interrupciones Críticas | DISP-CAL_0015 | Gestión de Agotamiento del Pool de Conexiones | Reactivo | Evitar bloqueos por agotamiento de recursos. | El sistema libera y valida conexiones inactivas. | Sin bloqueos HTTP 503. | Múltiples solicitudes concurrentes. | Saturación de conexiones. | Sistema en producción. | Gestor del pool de conexiones. | Liberación automática. | Tiempo de recuperación ≤ 5 s. | No | Tolerancia a Fallos | Pool de conexiones validado y autocorregido. |
| Disponibilidad | Estabilidad del Servicio en Alta Demanda | DISP-CAL_0016 | Disponibilidad durante Eliminación Forzada de Entidad | Preventivo | Mantener integridad durante operaciones críticas. | El sistema garantiza consistencia transaccional. | No se pierden datos ni registros. | Administrador. | Eliminación de entidad. | Sistema en producción. | Módulo de gestión de viviendas. | Operación completa sin fallos. | Duración < 5 s. | No | Concurrencia | Transacciones con índices y bloqueos controlados. |
| Disponibilidad | Recuperación Oportuna del Servicio | DISP-CAL_0017 | Impacto del Mantenimiento Programado | Reactivo | Minimizar tiempo fuera de servicio. | Mantenimiento controlado y notificado. | Disponibilidad ≥ 99 %. | Equipo de operaciones. | Mantenimiento planificado. | Sistema en mantenimiento. | Infraestructura de despliegue. | Restablecimiento automático. | Tiempo total de parada < 15 min. | No | Mantenimiento con Mínima Interrupción | Despliegue automatizado y validación post-mantenimiento. |

---

## 🧾 Autoría

**Universidad Católica de Oriente (UCO)**  
Proyecto desarrollado por estudiantes del programa **Ingeniería de Sistemas**  
**Año:** 2025  
**Arquitectura:** Microservicios distribuidos en Azure  
**Lenguaje principal:** Java 21  
**Framework:** Spring Boot 3.5.x  
**Base de datos:** PostgreSQL  
**Frontend:** React + Vite  
**Infraestructura:** Azure Cloud Services  

---
