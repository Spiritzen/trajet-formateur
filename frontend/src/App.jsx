// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "@/context/AuthContext";
import Login from "@/pages/Login";
import ProtectedRoute from "@/components/auth/ProtectedRoute";
import AdminEcole from "@/pages/AdminEcole";
import MonEcole from "@/pages/ecole/MonEcole"; 
import EcoleOm from "@/pages/ecole/EcoleOm";
import AdminFormateur from "@/pages/AdminFormateur";
import EspaceFormateur from "@/pages/formateur/EspaceFormateur";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Page de connexion */}
          <Route path="/login" element={<Login />} />

          {/* Dashboard générique (temporaire / pour autres rôles) */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <div
                  style={{
                    padding: "2rem",
                    color: "#f5f5f5",
                    background: "#050914",
                    minHeight: "100vh",
                  }}
                >
                  <h1>Dashboard (temporaire)</h1>
                  <p>Plus tard : vue globale des ordres de mission, trajets, etc.</p>
                </div>
              </ProtectedRoute>
            }
          />

          {/* ✅ PAGE ADMIN ECOLES PROTÉGÉE */}
          <Route
            path="/admin/ecoles"
            element={
              <ProtectedRoute>
                <AdminEcole />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/formateurs"
             element={
              <ProtectedRoute>
                <AdminFormateur />
              </ProtectedRoute>
            }
          />

          {/* ✅ PAGE ECOLE / ETABLISSEMENT PROTÉGÉE */}
          <Route
            path="/ecole/etablissement"
            element={
              <ProtectedRoute>
                <MonEcole />
              </ProtectedRoute>
            }
          />

          {/* ✅ PAGE ECOLE / ORDRES DE MISSION PROTÉGÉE */}
          <Route
          path="/ecole/om"
          element={
            <ProtectedRoute>
              <EcoleOm />
            </ProtectedRoute>
          }
          />
              {/* ✅ FORMATEUR / DASHBOARD */}
          <Route
            path="/formateur"
            element={
              <ProtectedRoute>
                <EspaceFormateur />
              </ProtectedRoute>
            }
          />

          {/* par défaut : redirige vers /login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
