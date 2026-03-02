// src/pages/Login.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import NavBar from "@/components/nav/NavBar";
import "@/css/Login.css";

export default function Login() {
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);

    // 👉 login() doit renvoyer { success, message?, roles? }
    const result = await login(email, password);

    // ❌ Identifiants incorrects ou erreur backend → on affiche le message rouge
    if (!result.success) {
      setError(
        result.message || "Erreur de connexion. Vérifie tes identifiants."
      );
      return;
    }

    const roles = result.roles || [];
    console.log("Roles après login :", roles);

    // Aucun rôle renvoyé → on ne bouge pas, on affiche une erreur claire
    if (roles.length === 0) {
      setError(
        "Votre compte ne possède aucun rôle. Merci de contacter l’administrateur."
      );
      return;
    }

    // 🌈 Routage en fonction du rôle principal
    if (roles.includes("ADMIN")) {
      navigate("/admin/ecoles");
    } else if (roles.includes("ECOLE")) {
      // 👉 route d’atterrissage pour une école connectée
      navigate("/ecole/etablissement");
    } else if (roles.includes("FORMATEUR")) {
      // 👉 le formateur atterrit sur son espace dédié
      navigate("/formateur");
    } else {
      // Rôle inconnu → on reste sur la page et on explique
      setError(
        `Votre compte possède un rôle non reconnu (${roles.join(
          ", "
        )}). Merci de contacter l’administrateur.`
      );
    }

    // "rememberMe" sera géré plus tard côté refresh token / cookies.
  }

  return (
    <div className="login-page">
      {/* NavBar flottante au-dessus */}
      <NavBar />

      {/* Carte centrale de connexion */}
      <div className="login-card">
        <div className="login-header">
          <h1 className="login-title">Plateforme missions</h1>
          <p className="login-subtitle">Connexion</p>
        </div>

        <p className="login-intro">
          Connecte-toi pour gérer tes missions, trajets et justificatifs.
        </p>

        {error && <div className="login-error">{error}</div>}

        <form className="login-form" onSubmit={handleSubmit}>
          <label className="login-label">
            Adresse e-mail
            <input
              type="email"
              className="login-input"
              placeholder="prenom.nom@exemple.fr"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </label>

          <label className="login-label">
            Mot de passe
            <input
              type="password"
              className="login-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>

          <div className="login-extra">
            <label className="remember-me">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
              />
              <span>Se souvenir de moi</span>
            </label>

            <button
              type="button"
              className="login-link-button"
              onClick={() => alert("Fonctionnalité à venir 🙂")}
            >
              Mot de passe oublié ?
            </button>
          </div>

          <button
            type="submit"
            className="login-submit"
            disabled={loading}
          >
            {loading ? "Connexion..." : "Se connecter"}
          </button>
        </form>

        <p className="login-footer-text">
          Pas encore de compte ?{" "}
          <button
            type="button"
            className="login-link-button"
            onClick={() =>
              alert("Contacter l’administrateur – à intégrer plus tard")
            }
          >
            Contacter l’administrateur
          </button>
        </p>
      </div>
    </div>
  );
}
