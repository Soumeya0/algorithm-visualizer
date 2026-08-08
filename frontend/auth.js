// ============================================================
// DreamWorld Auth / Arrays / Leaderboard module
// JWT lives ONLY in memory (a plain JS variable) — never in
// localStorage or sessionStorage, so it's gone on refresh.
// ============================================================

// NOTE: script.js already declares its own API_BASE = 'http://localhost:8080/api'
// (with /api baked in). To avoid a duplicate-const crash, this file uses its own
// name and includes /api explicitly in every path below.
const ARCADE_API_BASE = 'https://algorithm-visualizer-production-e2a0.up.railway.app/api';

// ---- In-memory auth state ----
let authToken = null;      // the JWT itself — memory only
let currentUserEmail = null;
let activeSavedArrayId = null; // the array currently loaded in the visualizer, if saved

// ---- Small fetch helper that attaches the token when we have one ----
async function apiFetch(path, options = {}) {
  const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
  if (authToken) headers["Authorization"] = "Bearer " + authToken;

  const res = await fetch(ARCADE_API_BASE + path, Object.assign({}, options, { headers }));
  const text = await res.text();
  let body;
  try { body = text ? JSON.parse(text) : null; } catch { body = text; }

  if (!res.ok) {
    const message = typeof body === "string" ? body : (body?.message || "Request failed");
    throw new Error(message);
  }
  return body;
}

// ============================================================
// AUTH: register / login / logout
// ============================================================

function setAuthState(token, email) {
  authToken = token;
  currentUserEmail = email;
  renderAuthStatus();
  if (token) {
    loadSavedArrays();
  } else {
    document.getElementById("savedArraysList").innerHTML =
      `<div class="empty-note">Log in to see your saved arrays.</div>`;
  }
}

function renderAuthStatus() {
  const box = document.getElementById("authStatusBox");
  if (authToken) {
    box.innerHTML = `
      <div class="player-tag">
        <span class="player-dot"></span>
        <span>Playing as <strong>${escapeHtml(currentUserEmail)}</strong></span>
        <button class="btn-tiny" onclick="logout()">Log out</button>
      </div>`;
  } else {
    box.innerHTML = `
      <button class="btn-primary btn-arcade" onclick="openAuthModal('login')">🎮 Log In</button>
      <button class="btn-secondary btn-arcade" onclick="openAuthModal('register')">✨ New Player</button>`;
  }
}

function openAuthModal(mode) {
  document.getElementById("authModal").style.display = "flex";
  switchAuthTab(mode);
  document.getElementById("authError").textContent = "";
}

function closeAuthModal() {
  document.getElementById("authModal").style.display = "none";
}

function switchAuthTab(mode) {
  const isLogin = mode === "login";
  document.getElementById("authTabLogin").classList.toggle("active", isLogin);
  document.getElementById("authTabRegister").classList.toggle("active", !isLogin);
  document.getElementById("authSubmitBtn").textContent = isLogin ? "Log In" : "Create Account";
  document.getElementById("authModalForm").dataset.mode = mode;
}

async function submitAuthForm(event) {
  event.preventDefault();
  const mode = document.getElementById("authModalForm").dataset.mode || "login";
  const email = document.getElementById("authEmail").value.trim();
  const password = document.getElementById("authPassword").value;
  const errorEl = document.getElementById("authError");
  errorEl.textContent = "";

  try {
    const path = mode === "login" ? "/api/auth/login" : "/api/auth/register";
    const data = await apiFetch(path, {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
    setAuthState(data.token, data.email);
    closeAuthModal();
  } catch (err) {
    errorEl.textContent = err.message || "Something went wrong";
  }
}

function logout() {
  setAuthState(null, null);
  activeSavedArrayId = null;
}

// ============================================================
// SAVED ARRAYS
// ============================================================

async function loadSavedArrays() {
  const listEl = document.getElementById("savedArraysList");
  try {
    const arrays = await apiFetch("/api/arrays");
    if (!arrays.length) {
      listEl.innerHTML = `<div class="empty-note">No saved arrays yet — generate or type one, then hit Save.</div>`;
      return;
    }
    listEl.innerHTML = arrays.map(a => `
      <div class="array-chip" onclick="loadArrayIntoVisualizer(${a.id}, '${escapeHtml(a.values.join(","))}')">
        <span class="chip-label">${escapeHtml(a.label)}</span>
        <span class="chip-values">[${a.values.join(", ")}]</span>
      </div>
    `).join("");
  } catch (err) {
    listEl.innerHTML = `<div class="empty-note">Couldn't load arrays: ${escapeHtml(err.message)}</div>`;
  }
}

async function saveCurrentArray() {
  if (!authToken) {
    openAuthModal("login");
    return;
  }
  const input = document.getElementById("arrayInput");
  const raw = input && input.value.trim();
  const values = raw
    ? raw.split(",").map(v => parseInt(v.trim(), 10)).filter(v => !isNaN(v))
    : (window.currentArray || []); // fall back to whatever the visualizer currently has, if exposed

  if (!values || !values.length) {
    flashStatus("saveArrayStatus", "Nothing to save — generate or type an array first.", true);
    return;
  }

  const label = prompt("Name this array (e.g. 'Nightmare Fuel', 'Speedrun Seed'):", "My Array");
  if (label === null) return;

  try {
    const saved = await apiFetch("/api/arrays", {
      method: "POST",
      body: JSON.stringify({ label: label || "My Array", values }),
    });
    activeSavedArrayId = saved.id;
    flashStatus("saveArrayStatus", `Saved "${saved.label}" ✅`, false);
    loadSavedArrays();
  } catch (err) {
    flashStatus("saveArrayStatus", err.message, true);
  }
}

function loadArrayIntoVisualizer(id, csv) {
  activeSavedArrayId = id;
  const input = document.getElementById("arrayInput");
  if (input) input.value = csv;
  if (typeof setCustomArray === "function") {
    setCustomArray(); // reuse the page's existing function if present
  }
  flashStatus("saveArrayStatus", "Loaded into visualizer — hit Start to run it.", false);
}

// ============================================================
// LEADERBOARD
// ============================================================

async function loadLeaderboard() {
  const algo = document.getElementById("leaderboardAlgo").value;
  const size = document.getElementById("leaderboardSize").value;
  const listEl = document.getElementById("leaderboardList");

  if (!size) {
    listEl.innerHTML = `<div class="empty-note">Enter an array size to see rankings.</div>`;
    return;
  }

  listEl.innerHTML = `<div class="empty-note">Loading rankings…</div>`;
  try {
    const rows = await apiFetch(`/api/leaderboard?algorithm=${encodeURIComponent(algo)}&size=${encodeURIComponent(size)}`);
    if (!rows.length) {
      listEl.innerHTML = `<div class="empty-note">No runs yet for this combo — be the first!</div>`;
      return;
    }
    listEl.innerHTML = rows.map((r, i) => `
      <div class="leader-row ${i === 0 ? "leader-first" : ""}">
        <span class="leader-rank">${medal(i)}</span>
        <span class="leader-name">${escapeHtml(r.email.split("@")[0])}</span>
        <span class="leader-stat">${r.comparisons} comps</span>
        <span class="leader-stat">${r.swaps} swaps</span>
      </div>
    `).join("");
  } catch (err) {
    listEl.innerHTML = `<div class="empty-note">Error: ${escapeHtml(err.message)}</div>`;
  }
}

function medal(i) {
  return ["🥇", "🥈", "🥉"][i] || `#${i + 1}`;
}

// ============================================================
// AUTO-SUBMIT RUN RESULT
// Watches the existing #message stat card. Your sorting classes
// (BubbleSort/MergeSort/QuickSort) all emit the exact final
// message "Array is sorted!" as their last step — we use that
// as the completion signal instead of touching script.js.
// ============================================================

function watchForRunCompletion() {
  const messageEl = document.getElementById("message");
  if (!messageEl) return;

  const observer = new MutationObserver(() => {
    const text = messageEl.textContent || "";
    if (text.toLowerCase().includes("sorted")) {
      handleRunCompleted();
    }
  });
  observer.observe(messageEl, { childList: true, characterData: true, subtree: true });
}

async function handleRunCompleted() {
  if (!authToken) return; // not logged in — nothing to submit

  if (!activeSavedArrayId) {
    flashStatus("saveArrayStatus", "Run finished! Save this array to submit it to the leaderboard next time.", false);
    return;
  }

  const algorithm = document.getElementById("algorithmSelect")?.value;
  const comparisons = parseInt(document.getElementById("comparisons")?.textContent || "0", 10);
  const swaps = parseInt(document.getElementById("swaps")?.textContent || "0", 10);

  try {
    await apiFetch("/api/runs", {
      method: "POST",
      body: JSON.stringify({ arrayId: activeSavedArrayId, algorithm, comparisons, swaps }),
    });
    flashStatus("saveArrayStatus", "🏆 Run submitted to the leaderboard!", false);
    loadLeaderboard();
  } catch (err) {
    flashStatus("saveArrayStatus", "Couldn't submit run: " + err.message, true);
  }
}

// ============================================================
// tiny helpers
// ============================================================

function flashStatus(elId, msg, isError) {
  const el = document.getElementById(elId);
  if (!el) return;
  el.textContent = msg;
  el.style.color = isError ? "#e0507a" : "#1a8a6f";
  clearTimeout(el._t);
  el._t = setTimeout(() => { el.textContent = ""; }, 4000);
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, c => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  }[c]));
}

// ============================================================
// init
// ============================================================
document.addEventListener("DOMContentLoaded", () => {
  renderAuthStatus();
  watchForRunCompletion();
  loadLeaderboard();
});