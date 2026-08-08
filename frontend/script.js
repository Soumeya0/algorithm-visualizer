// API configuration
const API_BASE = 'https://algorithm-visualizer-production-e2a0.up.railway.app/api';

// State variables
let currentSteps = [];
let currentStepIndex = 0;
let isPlaying = false;
let animationTimeout = null;
let currentArray = [64, 34, 25, 12, 22, 11, 90]; // Default array
let currentTree = null; // Tree state

// Tree visualization state
let treeViewState = {
    offsetX: 0,
    offsetY: 0,
    scale: 1,
    isDragging: false,
    lastX: 0,
    lastY: 0
};

// DOM elements
const visualization = document.getElementById('visualization');
const algorithmSelect = document.getElementById('algorithmSelect');
const speedSlider = document.getElementById('speedSlider');
const startBtn = document.getElementById('startBtn');
const pauseBtn = document.getElementById('pauseBtn');
const stepBtn = document.getElementById('stepBtn');

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    generateArray();
    updateSpeedDisplay();
    testConnection();
    
    // Add tree algorithm change listener
    document.getElementById('treeAlgorithmSelect').addEventListener('change', updateTreeTypeDisplay);
});

// Generate random array
function generateArray(size = 15) {
    currentArray = Array.from({ length: size }, () => 
        Math.floor(Math.random() * 90) + 10
    );
    renderBars(currentArray, -1, -1);
    resetUI();
}

// Set custom array from input
function setCustomArray() {
    const input = document.getElementById('arrayInput').value;
    if (input.trim() === '') return;
    
    const numbers = input.split(',').map(num => parseInt(num.trim()));
    if (numbers.every(num => !isNaN(num))) {
        currentArray = numbers;
        renderBars(currentArray, -1, -1);
        resetUI();
    } else {
        alert('Please enter valid numbers separated by commas');
    }
}

// Render bars in the visualization area (UPDATED FOR GLASSY UI)
function renderBars(array, compareIdx1, compareIdx2) {
    const maxValue = Math.max(...array);
    
    visualization.innerHTML = array.map((value, index) => {
        const height = (value / maxValue) * 300;
        let barClass = 'bar';
        
        if (index === compareIdx1 || index === compareIdx2) {
            barClass += ' comparing';
        }
        
        // Added 'margin: 0 6px;' and width: 40px to ensure spacing for the new pill-shaped bars
        return `
            <div class="${barClass}" style="height: ${height}px; width: 40px; margin: 0 6px;">
                <span class="bar-value">${value}</span>
            </div>
        `;
    }).join('');
}

// Start visualization
async function startVisualization() {
    const algorithm = algorithmSelect.value;
    
    // Only fetch new steps if we don't have any or we're at the end
    if (!currentSteps.length || currentStepIndex >= currentSteps.length - 1) {
        await fetchSortSteps(algorithm, currentArray);
    }
    
    if (currentSteps.length > 0) {
        isPlaying = true;
        updateButtonStates();
        playStep();
    }
}

// Play steps sequentially
function playStep() {
    if (!isPlaying || currentStepIndex >= currentSteps.length) {
        pauseVisualization();
        return;
    }
    
    renderStep(currentSteps[currentStepIndex]);
    currentStepIndex++;
    
    const speed = parseInt(speedSlider.value);
    animationTimeout = setTimeout(playStep, speed);
}

// Render a single step
function renderStep(step) {
    renderBars(step.array, step.comparingIndex1, step.comparingIndex2);
    
    // Update stats
    document.getElementById('comparisons').textContent = step.comparisons;
    document.getElementById('swaps').textContent = step.swaps;
    document.getElementById('currentStep').textContent = 
        `${currentStepIndex + 1}/${currentSteps.length}`;
    document.getElementById('message').textContent = step.message;
}

// Pause visualization
function pauseVisualization() {
    isPlaying = false;
    clearTimeout(animationTimeout);
    updateButtonStates();
}

// Step forward one frame
function stepForward() {
    if (currentStepIndex < currentSteps.length) {
        renderStep(currentSteps[currentStepIndex]);
        currentStepIndex++;
        updateButtonStates();
    }
}

// Reset visualization
function resetVisualization() {
    pauseVisualization();
    currentStepIndex = 0;
    if (currentSteps.length > 0) {
        renderStep(currentSteps[0]);
    } else {
        renderBars(currentArray, -1, -1);
    }
    updateButtonStates();
}

// Fetch sort steps from backend
async function fetchSortSteps(algorithm, array) {
    try {
        const response = await fetch(`${API_BASE}/sort/${algorithm}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(array)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        currentSteps = await response.json();
        currentStepIndex = 0;
        renderStep(currentSteps[0]);
        return true;
    } catch (error) {
        console.error('Error fetching sort steps:', error);
        alert('Failed to connect to backend. Make sure Spring Boot is running on port 8080');
        return false;
    }
}

// Update button states based on current status
function updateButtonStates() {
    startBtn.disabled = isPlaying;
    pauseBtn.disabled = !isPlaying;
    stepBtn.disabled = isPlaying || currentStepIndex >= currentSteps.length;
}

// Reset UI to initial state
function resetUI() {
    currentSteps = [];
    currentStepIndex = 0;
    document.getElementById('comparisons').textContent = '0';
    document.getElementById('swaps').textContent = '0';
    document.getElementById('currentStep').textContent = '0/0';
    document.getElementById('message').textContent = 'Ready';
    updateButtonStates();
}

// Update speed display
function updateSpeedDisplay() {
    const speedValue = document.getElementById('speedValue');
    speedSlider.addEventListener('input', (e) => {
        speedValue.textContent = e.target.value + 'ms';
    });
}

// Test backend connection on load
async function testConnection() {
    try {
        const response = await fetch(`${API_BASE}/sort/test`);
        if (response.ok) {
            console.log('✅ Connected to backend successfully!');
        } else {
            console.warn('⚠️ Backend connection issue');
        }
    } catch (error) {
        console.error('❌ Cannot connect to backend. Make sure Spring Boot is running!');
    }
}

// ============ ENHANCED TREE FUNCTIONS ============

// Show tree controls and hide sorting
function showTrees() {
    // Update active tab
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Hide sorting controls, show tree controls
    document.querySelector('.controls').style.display = 'none';
    document.querySelector('.array-input').style.display = 'none';
    document.getElementById('treeControls').style.display = 'flex';
    document.querySelector('.tree-canvas-container').style.display = 'block';
    
    // Hide sorting visualization, show tree canvas
    document.getElementById('visualization').style.display = 'none';
    
    // Focus on input
    setTimeout(() => {
        document.getElementById('treeValue').focus();
    }, 100);
    
    // Update tree type display
    updateTreeTypeDisplay();
    
    // Draw tree with enhanced rendering
    if (currentTree && currentTree.root) {
        renderEnhancedTree(currentTree);
    } else {
        drawEnhancedEmptyTree();
    }
    
    // Add canvas event listeners for pan/zoom
    setupCanvasInteractions();
}

// Show sorting controls
function showSorting() {
    // Update active tab
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Show sorting controls, hide tree controls
    document.querySelector('.controls').style.display = 'flex';
    document.querySelector('.array-input').style.display = 'block';
    document.getElementById('treeControls').style.display = 'none';
    document.querySelector('.tree-canvas-container').style.display = 'none';
    
    // Show sorting visualization
    document.getElementById('visualization').style.display = 'flex';
}

// Update tree type display
function updateTreeTypeDisplay() {
    const algorithm = document.getElementById('treeAlgorithmSelect').value;
    const treeTypeMap = {
        'bst-insert': 'BST',
        'bst-search': 'BST',
        'bst-delete': 'BST',
        'inorder': 'BST',
        'preorder': 'BST',
        'postorder': 'BST',
        'bfs': 'BST',
        'avl-insert': 'AVL Tree',
        'heap-insert': 'Max Heap'
    };
    document.getElementById('treeType').textContent = treeTypeMap[algorithm] || 'Tree';
}

// Draw enhanced empty tree
function drawEnhancedEmptyTree() {
    const canvas = document.getElementById('treeCanvas');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Draw grid background
    drawGrid(ctx, canvas.width, canvas.height);
    
    // Draw empty state message
    ctx.font = 'bold 24px Inter, sans-serif';
    ctx.fillStyle = 'rgba(148, 163, 184, 0.3)';
    ctx.textAlign = 'center';
    ctx.fillText('🌳', canvas.width/2, canvas.height/2 - 40);
    
    ctx.font = '18px Inter, sans-serif';
    ctx.fillStyle = '#94a3b8';
    ctx.fillText('Tree is empty', canvas.width/2, canvas.height/2);
    ctx.fillText('Enter a value above to start', canvas.width/2, canvas.height/2 + 30);
    
    document.getElementById('treeMessage').textContent = '👆 Start by inserting a value';
}

// Draw grid background
function drawGrid(ctx, width, height) {
    ctx.strokeStyle = 'rgba(45, 45, 58, 0.3)';
    ctx.lineWidth = 1;
    
    // Draw vertical lines
    for (let x = 0; x < width; x += 50) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, height);
        ctx.strokeStyle = 'rgba(45, 45, 58, 0.2)';
        ctx.stroke();
    }
    
    // Draw horizontal lines
    for (let y = 0; y < height; y += 50) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(width, y);
        ctx.strokeStyle = 'rgba(45, 45, 58, 0.2)';
        ctx.stroke();
    }
}

// Setup canvas interactions (pan and zoom)
function setupCanvasInteractions() {
    const canvas = document.getElementById('treeCanvas');
    
    // Remove old listeners
    canvas.removeEventListener('mousedown', handleMouseDown);
    canvas.removeEventListener('mousemove', handleMouseMove);
    canvas.removeEventListener('mouseup', handleMouseUp);
    canvas.removeEventListener('mouseleave', handleMouseUp);
    canvas.removeEventListener('wheel', handleWheel);
    
    // Add new listeners
    canvas.addEventListener('mousedown', handleMouseDown);
    canvas.addEventListener('mousemove', handleMouseMove);
    canvas.addEventListener('mouseup', handleMouseUp);
    canvas.addEventListener('mouseleave', handleMouseUp);
    canvas.addEventListener('wheel', handleWheel);
}

function handleMouseDown(e) {
    const canvas = document.getElementById('treeCanvas');
    treeViewState.isDragging = true;
    treeViewState.lastX = e.offsetX;
    treeViewState.lastY = e.offsetY;
    canvas.style.cursor = 'grabbing';
}

function handleMouseMove(e) {
    if (!treeViewState.isDragging) return;
    
    const dx = e.offsetX - treeViewState.lastX;
    const dy = e.offsetY - treeViewState.lastY;
    
    treeViewState.offsetX += dx;
    treeViewState.offsetY += dy;
    
    treeViewState.lastX = e.offsetX;
    treeViewState.lastY = e.offsetY;
    
    if (currentTree) {
        renderEnhancedTree(currentTree);
    }
}

function handleMouseUp() {
    treeViewState.isDragging = false;
    const canvas = document.getElementById('treeCanvas');
    canvas.style.cursor = 'grab';
}

function handleWheel(e) {
    e.preventDefault();
    
    const zoomFactor = 0.95;
    if (e.deltaY < 0) {
        treeViewState.scale *= 1.1; // Zoom in
    } else {
        treeViewState.scale *= 0.9; // Zoom out
    }
    
    // Limit zoom scale
    treeViewState.scale = Math.min(3, Math.max(0.5, treeViewState.scale));
    
    if (currentTree) {
        renderEnhancedTree(currentTree);
    }
}

// Fit tree to canvas
function fitTreeToCanvas() {
    treeViewState.offsetX = 0;
    treeViewState.offsetY = 0;
    treeViewState.scale = 1;
    
    if (currentTree) {
        renderEnhancedTree(currentTree);
    }
}

// Reset tree view
function resetTreeView() {
    fitTreeToCanvas();
}

// Calculate tree statistics for better layout
function calculateTreeStats(root) {
    if (!root) return { height: 0, maxWidth: 0 };
    
    let maxWidth = 0;
    
    function calculate(node, level = 0, position = 0) {
        if (!node) return { width: 0, positions: [] };
        
        const left = calculate(node.left, level + 1, position * 2);
        const right = calculate(node.right, level + 1, position * 2 + 1);
        
        maxWidth = Math.max(maxWidth, position + 1);
        
        return {
            width: left.width + right.width + 1,
            positions: [...left.positions, ...right.positions, position]
        };
    }
    
    const result = calculate(root);
    return {
        height: getTreeHeight(root),
        maxWidth: maxWidth || 1,
        nodeCount: countTreeNodes(root)
    };
}

// Get tree height
function getTreeHeight(node) {
    if (!node) return 0;
    return 1 + Math.max(getTreeHeight(node.left), getTreeHeight(node.right));
}

// Count tree nodes
function countTreeNodes(node) {
    if (!node) return 0;
    return 1 + countTreeNodes(node.left) + countTreeNodes(node.right);
}

// Enhanced tree rendering
function renderEnhancedTree(treeData) {
    console.log('Rendering enhanced tree:', treeData);
    
    const canvas = document.getElementById('treeCanvas');
    if (!canvas) {
        console.error('Canvas element not found!');
        return;
    }
    
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Draw background grid
    drawGrid(ctx, canvas.width, canvas.height);
    
    if (!treeData || !treeData.root) {
        drawEnhancedEmptyTree();
        return;
    }
    
    // Update message
    if (treeData.message) {
        document.getElementById('treeMessage').textContent = treeData.message;
    }
    
    // Apply view state transformations
    ctx.save();
    ctx.translate(treeViewState.offsetX, treeViewState.offsetY);
    ctx.scale(treeViewState.scale, treeViewState.scale);
    
    // Calculate tree dimensions for dynamic positioning
    const treeStats = calculateTreeStats(treeData.root);
    const canvasWidth = canvas.width / treeViewState.scale;
    const canvasHeight = canvas.height / treeViewState.scale;
    
    // Dynamic node sizing based on tree size
    const nodeRadius = Math.min(35, Math.max(20, 30 - treeStats.height * 1.5));
    const levelHeight = Math.min(120, Math.max(70, 100 - treeStats.height * 5));
    
    // Calculate horizontal spacing based on tree width
    const horizontalSpacing = Math.min(
        canvasWidth / (treeStats.maxWidth + 1),
        150
    );
    
    // Center the tree
    const startX = canvasWidth / 2;
    const startY = 60;
    
    // Draw the tree with enhanced visuals
    drawEnhancedTreeNode(
        ctx, 
        treeData.root, 
        startX, 
        startY, 
        horizontalSpacing,
        levelHeight,
        nodeRadius,
        0,
        treeData.traversalPath || []
    );
    
    ctx.restore();
}

function drawEnhancedTreeNode(ctx, node, x, y, hSpacing, vSpacing, nodeRadius, level, highlightPath = []) {
    if (!node) return;
    
    // Check if this node is in the highlight path
    const isHighlighted = highlightPath.includes(node.value);
    const isSearchTarget = node.highlighted;
    
    // Draw connections to children first (so they appear behind nodes)
    ctx.strokeStyle = '#4a5568';
    ctx.lineWidth = 2;
    ctx.setLineDash([]);
    
    if (node.left) {
        ctx.beginPath();
        ctx.moveTo(x, y + nodeRadius);
        ctx.lineTo(x - hSpacing, y + vSpacing - nodeRadius);
        ctx.strokeStyle = 'rgba(100, 116, 139, 0.6)';
        ctx.stroke();
    }
    
    if (node.right) {
        ctx.beginPath();
        ctx.moveTo(x, y + nodeRadius);
        ctx.lineTo(x + hSpacing, y + vSpacing - nodeRadius);
        ctx.strokeStyle = 'rgba(100, 116, 139, 0.6)';
        ctx.stroke();
    }
    
    // Draw node shadow
    ctx.shadowColor = 'rgba(0, 0, 0, 0.5)';
    ctx.shadowBlur = 10;
    ctx.shadowOffsetY = 2;
    
    // Draw node with gradient
    const gradient = ctx.createRadialGradient(x-5, y-5, 5, x, y, nodeRadius + 5);
    
    if (isSearchTarget) {
        // Found node - golden
        gradient.addColorStop(0, '#fbbf24');
        gradient.addColorStop(1, '#f59e0b');
    } else if (isHighlighted) {
        // Traversal path - blue
        gradient.addColorStop(0, '#3b82f6');
        gradient.addColorStop(1, '#2563eb');
    } else {
        // Normal node - purple
        gradient.addColorStop(0, '#8b5cf6');
        gradient.addColorStop(1, '#6d28d9');
    }
    
    ctx.beginPath();
    ctx.arc(x, y, nodeRadius, 0, 2 * Math.PI);
    ctx.fillStyle = gradient;
    ctx.fill();
    
    // Reset shadow for border
    ctx.shadowBlur = 0;
    
    // Draw border
    ctx.strokeStyle = isSearchTarget ? '#fbbf24' : (isHighlighted ? '#60a5fa' : '#2d3748');
    ctx.lineWidth = isSearchTarget ? 4 : 2;
    ctx.stroke();
    
    // Draw value
    ctx.fillStyle = 'white';
    ctx.font = `bold ${Math.max(12, nodeRadius - 8)}px 'Inter', sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(node.value, x, y);
    
    // Recursively draw children with reduced horizontal spacing
    const newHSpacing = hSpacing * 0.6;
    
    if (node.left) {
        drawEnhancedTreeNode(
            ctx, 
            node.left, 
            x - hSpacing, 
            y + vSpacing, 
            newHSpacing, 
            vSpacing,
            nodeRadius * 0.9,
            level + 1,
            highlightPath
        );
    }
    
    if (node.right) {
        drawEnhancedTreeNode(
            ctx, 
            node.right, 
            x + hSpacing, 
            y + vSpacing, 
            newHSpacing, 
            vSpacing,
            nodeRadius * 0.9,
            level + 1,
            highlightPath
        );
    }
}

// Handle predefined value selection (removed - kept for backward compatibility)
function handlePredefinedValueSelect() {
    // This function is kept for backward compatibility but does nothing
}

// Handle Enter key press in tree input
function handleTreeInputKeyPress(event) {
    if (event.key === 'Enter') {
        insertTreeValue();
    }
}

// Insert value into tree
async function insertTreeValue() {
    const valueInput = document.getElementById('treeValue');
    const value = valueInput.value;
    const algorithm = document.getElementById('treeAlgorithmSelect').value;
    
    console.log('Inserting value:', value, 'with algorithm:', algorithm);
    
    if (!value) {
        alert('Please enter a value');
        return;
    }
    
    let endpoint = '';
    let method = 'POST';
    
    switch(algorithm) {
        case 'bst-insert':
            endpoint = `/tree/bst/insert/${value}`;
            break;
        case 'bst-search':
            endpoint = `/tree/bst/search/${value}`;
            break;
        case 'bst-delete':
            endpoint = `/tree/bst/delete/${value}`;
            break;
        case 'inorder':
            endpoint = '/tree/traversal/inorder';
            method = 'GET';
            break;
        case 'preorder':
            endpoint = '/tree/traversal/preorder';
            method = 'GET';
            break;
        case 'postorder':
            endpoint = '/tree/traversal/postorder';
            method = 'GET';
            break;
        case 'bfs':
            endpoint = '/tree/traversal/bfs';
            method = 'GET';
            break;
        case 'avl-insert':
            endpoint = `/tree/avl/insert/${value}`;
            break;
        case 'heap-insert':
            endpoint = `/tree/heap/insert/${value}`;
            break;
        default:
            console.error('Unknown algorithm:', algorithm);
            return;
    }
    
    try {
        const url = `${API_BASE}${endpoint}`;
        console.log('Fetching URL:', url);
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }
        
        const treeData = await response.json();
        console.log('Received tree data:', treeData);
        
        // Clear highlight from previous operations
        if (currentTree && currentTree.root) {
            clearHighlights(currentTree.root);
        }
        
        currentTree = treeData;
        renderEnhancedTree(treeData);
        updateTreeInfo(treeData);
        valueInput.value = ''; // Clear input
        valueInput.focus(); // Keep focus on input
        
        // If this was a search operation, reset highlight after 2 seconds
        if (algorithm === 'bst-search') {
            setTimeout(() => {
                if (currentTree && currentTree.root) {
                    clearHighlights(currentTree.root);
                    renderEnhancedTree(currentTree);
                }
            }, 2000);
        }
    } catch (error) {
        console.error('Error with tree operation:', error);
        alert('Failed to connect to backend. Make sure Spring Boot is running on port 8080\n\nError: ' + error.message);
    }
}

// Clear highlights from all nodes
function clearHighlights(node) {
    if (!node) return;
    node.highlighted = false;
    clearHighlights(node.left);
    clearHighlights(node.right);
}

// Insert multiple values at once
async function insertMultipleValues() {
    // Sample values for demonstration
    const sampleValues = [50, 30, 70, 20, 40, 60, 80, 35, 45, 55];
    
    const algorithm = document.getElementById('treeAlgorithmSelect').value;
    
    // Check if algorithm supports insertion
    if (!algorithm.includes('insert') && algorithm !== 'avl-insert' && algorithm !== 'heap-insert') {
        alert('Please select an insertion algorithm (BST Insertion, AVL Tree, or Heap Insert)');
        return;
    }
    
    // Insert values one by one with a delay
    for (let i = 0; i < sampleValues.length; i++) {
        const value = sampleValues[i];
        
        // Update input field to show current value
        document.getElementById('treeValue').value = value;
        
        // Call insert function
        await insertTreeValue();
        
        // Wait a bit between inserts for visualization
        if (i < sampleValues.length - 1) {
            await new Promise(resolve => setTimeout(resolve, 800));
        }
    }
    
    // Clear the input after all inserts
    document.getElementById('treeValue').value = '';
    document.getElementById('treeValue').focus();
}

// Update tree information display
function updateTreeInfo(treeData) {
    if (treeData) {
        const heightEl = document.getElementById('treeHeight');
        const countEl = document.getElementById('nodeCount');
        const messageEl = document.getElementById('treeMessage');
        
        if (heightEl) heightEl.textContent = treeData.height || 0;
        if (countEl) countEl.textContent = treeData.nodeCount || 0;
        
        if (treeData.message && messageEl) {
            messageEl.textContent = treeData.message;
        }
        
        if (treeData.traversalPath && treeData.traversalPath.length > 0) {
            console.log('Traversal path:', treeData.traversalPath.join(' → '));
        }
    }
}

// Reset tree
async function resetTree() {
    try {
        const response = await fetch(`${API_BASE}/tree/reset`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const treeData = await response.json();
        currentTree = treeData;
        renderEnhancedTree(treeData);
        updateTreeInfo(treeData);
        
        // Clear input
        document.getElementById('treeValue').value = '';
        document.getElementById('treeValue').focus();
        
        // Reset view
        resetTreeView();
    } catch (error) {
        console.error('Error resetting tree:', error);
        alert('Failed to reset tree: ' + error.message);
    }
}

// Make sure to expose functions to global scope
window.generateArray = generateArray;
window.setCustomArray = setCustomArray;
window.startVisualization = startVisualization;
window.pauseVisualization = pauseVisualization;
window.stepForward = stepForward;
window.resetVisualization = resetVisualization;
window.showSorting = showSorting;
window.showTrees = showTrees;
window.insertTreeValue = insertTreeValue;
window.resetTree = resetTree;
window.handlePredefinedValueSelect = handlePredefinedValueSelect;
window.handleTreeInputKeyPress = handleTreeInputKeyPress;
window.insertMultipleValues = insertMultipleValues;
window.fitTreeToCanvas = fitTreeToCanvas;
window.resetTreeView = resetTreeView;

// Run connection test
testConnection();