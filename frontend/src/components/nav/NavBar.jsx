// src/components/nav/NavBar.jsx
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import "@/css/NavBar.css";

export default function NavBar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  // Est-ce qu’un formateur est connecté ?
  const isFormateur =
    user && Array.isArray(user.roles) && user.roles.includes("FORMATEUR");

  const preventClick = (e) => e.preventDefault();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  function handleLogin() {
    navigate("/login");
  }

  function handleLogoClick() {
    if (isFormateur) {
      navigate("/formateur");
    } else {
      navigate("/login");
    }
  }

  return (
    <header className="tf-navbar">
      <div className="tf-navbar-inner">
        {/* Logo à gauche */}
        <button
          type="button"
          className="tf-logo-btn"
          onClick={handleLogoClick}
          aria-label="Accueil Trajet Formateur"
        >
          <img
            src="/img/LogoTrajetFormateur.png"
            alt="Logo Trajet Formateur"
            className="tf-logo-img"
          />
        </button>

        {/* Menus centraux (pour l’instant encore visuels) */}
        <nav className="tf-nav-menu" aria-label="Navigation principale">
          <button
            type="button"
            className="tf-nav-link"
            onClick={preventClick}
          >
            Tableaux de bord
          </button>
          <button
            type="button"
            className="tf-nav-link"
            onClick={preventClick}
          >
            Mes missions
          </button>
          <button
            type="button"
            className="tf-nav-link"
            onClick={preventClick}
          >
            Mes trajets
          </button>
          <button
            type="button"
            className="tf-nav-link"
            onClick={preventClick}
          >
            Mes justificatifs
          </button>
        </nav>

        {/* À droite : soit badge FORMATEUR + déconnexion, soit bouton connexion */}
        {isFormateur ? (
          <div className="tf-nav-admin-right">
            {/* Pastille bleue FORMATEUR */}
            <span className="tf-nav-title-badge tf-nav-title-badge-formateur">
              FORMATEUR
            </span>

            <button
              type="button"
              className="tf-login-btn"
              onClick={handleLogout}
              aria-label="Déconnexion"
            >
              <img
                src="/img/BtnDeconexion.png"
                alt="Déconnexion"
                className="tf-login-icon"
              />
            </button>
          </div>
        ) : (
          <button
            type="button"
            className="tf-login-btn"
            onClick={handleLogin}
            aria-label="Connexion"
          >
            <img
              src="/img/BtnConexion.png"
              alt="Bouton connexion"
              className="tf-login-icon"
            />
          </button>
        )}
      </div>
    </header>
  );
}
