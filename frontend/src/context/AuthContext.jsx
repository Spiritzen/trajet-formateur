// src/context/AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import axiosClient, { setAuthToken } from "@/api/axiosClient";

/**
 * Contexte d'authentification global pour l'application React.
 *
 * Ce contexte stocke UNIQUEMENT les infos en MÉMOIRE (pas de localStorage) :
 *  - accessToken : JWT d'accès, utilisé pour appeler les routes protégées
 *  - user       : infos affichables (id, email, nom, rôle(s))
 *
 * Important par rapport aux consignes de ton tuteur :
 *  - le refresh token sera géré plus tard côté backend, via un cookie HttpOnly ;
 *  - le front ne voit jamais le refresh token ;
 *  - le front ne stocke l'access token que dans la mémoire de React.
 */

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);

  // -------------------------------------------------------
  // Petite utilitaire : normaliser les rôles
  // API renvoie souvent : ["ROLE_ADMIN", "ROLE_ECOLE"]
  // On veut côté front : ["ADMIN", "ECOLE"]
  // -------------------------------------------------------
  function normalizeRoles(rawRoles) {
    if (!rawRoles) return [];
    if (!Array.isArray(rawRoles)) return [];

    return rawRoles.map((r) =>
      typeof r === "string" && r.startsWith("ROLE_")
        ? r.substring(5) // supprime "ROLE_"
        : r
    );
  }

  /**
   * Login :
   * - envoie l'email + password à /api/auth/login (baseURL `/api` dans axiosClient)
   * - récupère { accessToken, userId, email, prenom, nom, roles }
   * - stocke ces infos en mémoire UNIQUEMENT
   * - renvoie { success, message?, roles? } pour que Login.jsx route en fonction du rôle
   */
  async function login(email, password) {
    setLoading(true);
    try {
      const response = await axiosClient.post("/auth/login", {
        email,
        password,
      });

      const data = response.data;
      // Exemple de payload attendu :
      // {
      //   "accessToken": "...",
      //   "tokenType": "Bearer",
      //   "userId": 1,
      //   "email": "admin@trajet.afci.test",
      //   "prenom": "Alice",
      //   "nom": "Admin",
      //   "roles": ["ROLE_ADMIN"]
      // }

      const roles = normalizeRoles(data.roles);

      setAccessToken(data.accessToken);
      setUser({
        userId: data.userId,
        email: data.email,
        prenom: data.prenom,
        nom: data.nom,
        roles, // 👈 rôles déjà normalisés : ["ADMIN"] / ["ECOLE"] / ...
      });

      // On configure le header Authorization dans axios pour TOUTES les requêtes suivantes.
      setAuthToken(data.accessToken);

      // 🔥 Très important : on renvoie aussi les rôles au composant Login
      return { success: true, roles };
    } catch (error) {
      console.error("Erreur de login :", error);

      return {
        success: false,
        message:
          error.response?.data?.message ||
          "Impossible de se connecter. Vérifie tes identifiants.",
      };
    } finally {
      setLoading(false);
    }
  }

  /**
   * Logout :
   * - efface le token en mémoire
   * - efface les infos utilisateur
   * - remet axios sans Authorization
   *
   * (Plus tard, on pourra aussi appeler un endpoint backend /api/auth/logout
   *  pour invalider le refresh token côté serveur.)
   */
  function logout() {
    setAccessToken(null);
    setUser(null);
    setAuthToken(null);
  }

  /**
   * Valeur exposée par le contexte à toute l'application.
   */
  const value = {
    accessToken,
    user,
    isAuthenticated: !!accessToken,
    loading,
    login,
    logout,
  };

  /**
   * Hook de démarrage :
   * Plus tard, on pourra ici :
   *  - appeler /api/auth/refresh pour obtenir un nouveau accessToken
   *    grâce au refresh token en cookie HttpOnly ;
   *  - ou /api/auth/me pour récupérer les infos utilisateur.
   *
   * Pour l'instant, on ne fait rien → l'utilisateur doit se reconnecter
   * à chaque reload (ce qui est acceptable en dev).
   */
  useEffect(() => {
    // TODO : implémenter un "silent refresh" quand le backend /api/auth/refresh sera prêt.
  }, []);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * Hook utilitaire pour consommer facilement le contexte d'authentification.
 * Exemple :
 *   const { user, isAuthenticated, login, logout } = useAuth();
 */
// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth doit être utilisé dans <AuthProvider>");
  }
  return ctx;
}
