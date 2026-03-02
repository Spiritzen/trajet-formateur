// src/api/axiosClient.js
import axios from "axios";

/**
 * Instance Axios centralisée.
 *
 * - baseURL : vers ton backend Spring Boot (modifie si besoin)
 * - withCredentials: true -> pour que le navigateur envoie
 *   automatiquement le cookie HttpOnly (refresh token) aux endpoints concernés.
 */
const axiosClient = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

/**
 * Permet au AuthContext de mettre à jour le header Authorization
 * quand l'utilisateur se connecte ou se déconnecte.
 */
export function setAuthToken(token) {
  if (token) {
    axiosClient.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete axiosClient.defaults.headers.common["Authorization"];
  }
}

export default axiosClient;
