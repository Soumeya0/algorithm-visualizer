# 🌳 Algorithm Visualizer

An interactive web application for visualizing sorting algorithms and tree data structures in real-time.

## ✨ Features

### 📊 Sorting Algorithms
- **Bubble Sort**, **Quick Sort**, **Merge Sort**
- Real-time statistics (comparisons, swaps)
- Adjustable animation speed
- Custom array input

### 🌲 Tree Algorithms
- **BST Operations**: Insert, Search, Delete
- **Traversals**: In-order, Pre-order, Post-order, BFS
- **Advanced**: AVL Tree with rotations, Max Heap
- Interactive canvas with pan/zoom controls

## 🚀 Quick Start

### Prerequisites
- Java 23+
- Maven
- Modern web browser

### Installation

1. **Clone and navigate**
```bash
git clone https://github.com/yourusername/algorithm-visualizer.git
cd algorithm-visualizer
```

2. **Run backend**
```bash
# macOS/Linux
./mvnw spring-boot:run
# Windows
mvnw.cmd spring-boot:run
```

3. **Open frontend**
- Open `index.html` in your browser
- Connects to `http://localhost:8080`

## 🎮 Usage

### Sorting
1. Select algorithm → Generate Array → Start
2. Use Pause/Step/Reset to control playback
3. Enter custom arrays (e.g., "5,2,9,1,7")

### Trees
1. Click "Trees" tab
2. Select algorithm → Enter value → Press Enter
3. Drag canvas to pan, scroll to zoom

## 📡 API Endpoints

### Sorting
- `POST /api/sort/{bubble|merge|quick}` - Get sort steps
- `GET /api/sort/test` - Connection test

### Trees
- `POST /api/tree/bst/insert/{value}`
- `POST /api/tree/bst/search/{value}`
- `POST /api/tree/bst/delete/{value}`
- `GET /api/tree/traversal/{inorder|preorder|postorder|bfs}`
- `POST /api/tree/avl/insert/{value}`
- `POST /api/tree/heap/insert/{value}`
- `POST /api/tree/reset`

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.5.11, Java 23
- **Frontend**: Vanilla JavaScript, HTML5 Canvas, CSS3
