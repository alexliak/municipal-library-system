# UML Diagrams - Municipal Library Management System

This directory contains all UML diagrams for the Municipal Library Management System created for the SWE6002 Enterprise Systems Development assessment.

## 📊 Included Diagrams

1. **use-case-diagram.puml** - System use cases with actors and relationships
2. **class-diagram.puml** - Complete class structure with entities, services, controllers
3. **sequence-login.puml** - Login process flow with Spring Security
4. **sequence-checkout.puml** - Book checkout process flow
5. **entity-relationship-diagram.puml** - Database schema with all relationships

## 🛠️ How to View the Diagrams

### Option 1: IntelliJ IDEA with PlantUML Plugin (Recommended)

1. **Install the Plugin:**
   - Open IntelliJ IDEA
   - Go to `File` → `Settings` → `Plugins`
   - Search for "PlantUML Integration"
   - Click `Install` and restart IDE

2. **View Diagrams:**
   - Open any `.puml` file in this directory
   - The diagram will render automatically in a side panel
   - Use the toolbar to:
     - Zoom in/out
     - Export as PNG/SVG/PDF
     - Copy to clipboard

3. **Export Options:**
   - Right-click on the diagram → `Export Diagram`
   - Choose format: PNG (recommended), SVG, PDF, LaTeX
   - Set resolution for high-quality exports

### Option 2: VS Code with PlantUML Extension

1. **Install Prerequisites:**
   - Install Java (required for PlantUML)
   - Install VS Code PlantUML extension

2. **View Diagrams:**
   - Open `.puml` file
   - Press `Alt+D` to preview
   - Press `Ctrl+Shift+P` → "PlantUML: Export"

### Option 3: Online PlantUML Server

1. Visit https://www.plantuml.com/plantuml/uml/
2. Copy the content of any `.puml` file
3. Paste in the editor
4. View and download the diagram

### Option 4: Command Line

```bash
# Install PlantUML
sudo apt-get install plantuml

# Generate PNG
plantuml use-case-diagram.puml

# Generate SVG
plantuml -tsvg class-diagram.puml

# Generate all diagrams
plantuml *.puml
```

## 📋 Diagram Descriptions

### Use Case Diagram
Shows the functional requirements and actor interactions:
- **Actors:** Guest, Member, Librarian, Administrator
- **Inheritance:** Shows role hierarchy
- **Use Cases:** All system functionalities
- **Relationships:** Include and extend relationships

### Class Diagram
Illustrates the system architecture:
- **Entities:** User, Book, Author, Loan, Role, BookRating
- **Services:** Business logic layer
- **Controllers:** MVC controllers
- **Repositories:** Data access layer
- **Relationships:** Associations, dependencies, inheritance

### Login Sequence Diagram
Details the authentication flow:
- Spring Security filter chain
- BCrypt password validation
- Role-based redirection
- Session management

### Checkout Sequence Diagram
Shows the book borrowing process:
- Validation steps
- Transaction management
- Inventory updates
- Error handling

### Entity Relationship Diagram
Database schema visualization:
- All tables with columns
- Primary and foreign keys
- Indexes for performance
- Junction tables for many-to-many relationships
- Business rule annotations

## 🎨 Customization

The diagrams use PlantUML themes for better visualization:
- `!theme plain` - Clean, professional appearance
- Custom colors for different element types
- Consistent styling across all diagrams

## 📝 For the Assessment

These diagrams fulfill the UML requirements for the SWE6002 assessment:
- ✅ Use Case Diagram - Shows all actors and functionalities
- ✅ Class Diagram - Demonstrates OOP design and patterns
- ✅ Sequence Diagrams - Illustrate key processes
- ✅ ERD - Complete database design

## 🔗 Related Resources

- [PlantUML Documentation](https://plantuml.com/)
- [Project Source Code](https://github.com/alexliak/municipal-library-system)
- [Main Project README](../../README.md)

---

Generated for SWE6002 Enterprise Systems Development  
University of Greater Manchester ex Bolton / New York College