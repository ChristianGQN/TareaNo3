# Proyecto: Notes and Categories (Action History Application)

## Descripción
Este proyecto gestiona notas organizadas por categorías con "sistema de auditoría de acciones". 
Registra automáticamente todas las operaciones CRUD realizadas y permite consultar el historial con filtros avanzados. 
Implementando la relación 1:N, búsqueda avanzada y persistencia usando Room (SQLite).

# CARACTERÍSTICAS

## Gestión de Notas y Categorías
- Crear y gestionar categorías
- Crear notas asociadas a categorías
- Relación 1:N (una categoría tiene muchas notas)
- Búsqueda avanzada en títulos, contenidos y nombres de categorías
- Editar y eliminar notas y categorías
- Eliminación en cascada

## Sistema de Historial (Actividad 3)
- Registro automático de todas las acciones CRUD
- Visualización completa del historial de acciones
- Filtros por tipo: Todas / Categorías / Notas
- Búsqueda en tiempo real en el historial
- Información detallada de cada acción (qué, cuándo, detalles)
- Opción para limpiar el historial completo
- Contador de acciones totales

## Arquitectura
Patrón MVC (Model-View-Controller)
- Model: Entidades, DAOs, Relaciones, HistoryManager y Base de datos (Room)
- View: Layouts XML y Adapters (CategoryAdapter, NoteAdapter, HistoryAdapter)
- Controller: Activities que manejan la lógica de negocio

##️ Estructura de la Base de Datos

### Tabla: `categories` (Actividad 2)
| Columna | Tipo | Descripción |
|---------|------|-------------|
| `category_id` | INTEGER | Primary Key (autoincremental) |
| `category_name` | TEXT | Nombre de la categoría (único, requerido) |

### Tabla: `notes (Actividad 2)
| Columna | Tipo | Descripción |
|---------|------|-------------|
| `note_id` | INTEGER | Primary Key (autoincremental) |
| `note_title` | TEXT | Título de la nota (requerido) |
| `note_content` | TEXT | Contenido de la nota (requerido) |
| `created_at` | TEXT | Fecha de creación |
| `category_id` | INTEGER | Foreign Key → categories(category_id) |

### Tabla: `history` (Actividad 3)
| Columna | Tipo | Descripción |
|---------|------|-------------|
| `history_id` | INTEGER | Primary Key (autoincremental) |
| `action` | TEXT | Tipo de acción (insert_note, update_category, etc.) |
| `created_at` | TEXT | Fecha y hora de la acción (requerido) |
| `details` | TEXT | Información detallada de la acción |

### Diagrama de Base de Datos
`````````````````````````````````````````````````
┌─────────────────┐          ┌─────────────────┐
│   categories    │          │      notes      │
├─────────────────┤          ├─────────────────┤
│ category_id(PK) │──┐    ┌─→│ note_id (PK)    │
│ category_name   │  │    │  │ note_title      │
└─────────────────┘  │    │  │ note_content    │
                     │    │  │ created_at      │
                     └────┘  │ category_id(FK) │
                      1:N    └─────────────────┘

┌─────────────────┐
│    history      │  (Auditoría de acciones)
├─────────────────┤
│ history_id (PK) │
│ action          │
│ created_at      │
│ details         │
└─────────────────┘
``````````````````````````````````````````````````
### Consultas de Historial
```sql
-- Obtener todo el historial
SELECT * FROM history ORDER BY history_id DESC

-- Filtrar por tipo de acción
SELECT * FROM history WHERE action = 'insert_note' ORDER BY history_id DESC

-- Buscar en detalles
SELECT * FROM history WHERE details LIKE '%searchText%' ORDER BY history_id DESC

-- Contar acciones totales
SELECT COUNT(*) FROM history
```
### Consultas de Relación
```sql
-- Obtener categoría con todas sus notas
SELECT * FROM categories WHERE category_id = :id

-- Contar notas por categoría
SELECT COUNT(*) FROM notes WHERE category_id = :categoryId
```
## Tecnologías Utilizadas
- Lenguaje: Java
- SDK mínimo: Android API 24
- Base de datos: Room 2.6.1
- Arquitectura: MVC
- UI Components: RecyclerView, CardView, Spinner
- Otras: Foreign Keys, Cascading Deletes, JOIN queries, Singleton Pattern

##Instalación y Ejecución

### Pasos de instalación
1. Clona el repositorio:
```bash
git clone [URL_DEL_REPOSITORIO]
```

2. Abre el proyecto en Android Studio:
   - File > Open
   - Seleccione la carpeta del proyecto

3. Sincroniza Gradle:
   - Android Studio sincronizará automáticamente
   - O manualmente: File > Sync Project with Gradle Files

4. Ejecuta la aplicación:
   - Conecte un dispositivo Android o inicia un emulador
   - Haga clic en el botón Run
   - Seleccione el dispositivo de destino

## Estructura del Proyecto
```
NotesCategories/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/notescategories/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── Note.java
│   │   │   │   │   ├── History.java
│   │   │   │   │   ├── CategoryWithNotes.java
│   │   │   │   │   ├── ActionCount.java
│   │   │   │   │   ├── CategoryDao.java
│   │   │   │   │   ├── NoteDao.java
│   │   │   │   │   ├── HistoryDao.java
│   │   │   │   │   ├── HistoryManager.java
│   │   │   │   │   └── AppDatabase.java
│   │   │   │   ├── view/
│   │   │   │   │   ├── CategoryAdapter.java
│   │   │   │   │   ├── NoteAdapter.java
│   │   │   │   │   └── HistoryAdapter.java
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── AddCategoryActivity.java
│   │   │   │   ├── AddNoteActivity.java
│   │   │   │   └── HistoryActivity.java
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       │   ├── activity_main.xml
│   │   │       │   ├── activity_add_category.xml
│   │   │       │   ├── activity_add_note.xml
│   │   │       │   ├── activity_history.xml
│   │   │       │   ├── item_category.xml
│   │   │       │   ├── item_note.xml
│   │   │       │   └── item_history.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── README.md
```

## Convenciones de Nomenclatura
- **Clases**: PascalCase (`History`, `HistoryManager`, `HistoryAdapter`)
- **Métodos y variables**: camelCase (`actionType`, `logAction()`, `getAllHistory()`)
- **Constantes**: SNAKE_CASE (`DATE_FORMAT`, `ACTION_INSERT_NOTE`)
- **Columnas de BD**: snake_case (`history_id`, `created_at`)

## Versiones del Proyecto

### Versión 1.0 (Actividad 2)
- CRUD de categorías y notas
- Relación 1:N
- Búsqueda avanzada

### Versión 2.0 (Actividad 3)
Todo lo anterior mas:
- Sistema completo de historial de acciones
- Registro automático de operaciones CRUD
- Filtros y búsqueda en historial
- Gestión de historial (visualizar, buscar, limpiar)

## Alumno
- Quintero Nuñez Christian Gpe.
