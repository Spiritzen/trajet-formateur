// src/components/auth/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";

/**
 * Composant de protection de route.
 * Exemple d'utilisation :
 *   <Route
 *      path="/dashboard"
 *      element={
 *        <ProtectedRoute>
 *          <DashboardPage />
 *        </ProtectedRoute>
 *      }
 *   />
 */
export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    // On pourra plus tard ajouter un message "Veuillez vous connecter"
    return <Navigate to="/login" replace />;
  }

  return children;
}
