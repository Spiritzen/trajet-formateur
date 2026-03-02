// src/components/nav/NavBarFormateur.jsx
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/AuthContext";
import "@/css/NavBar.css";

/**
 * Barre de navigation pour le rôle FORMATEUR.
 *
 * Spécificités :
 *  - Logo à gauche
 *  - Onglets centraux (Tableaux de bord / Mes missions / Mes trajets / Mes justificatifs)
 *  - À droite : badge FORMATEUR + bouton de déconnexion
 */
export default function NavBarFormateur() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  function handleLogoClick() {
    // Page d'accueil formateur (layout + profil)
    navigate("/formateur");
  }

  // Helpers de navigation pour les onglets
  const goProfil = () => navigate("/formateur/profil");
  const goMissions = () => navigate("/formateur/om");
  const goTrajets = () => navigate("/formateur/trajets");
  const goJustifs = () => navigate("/formateur/justificatifs");

  return (
    <header className="tf-navbar">
      <div className="tf-navbar-inner">
        {/* Logo à gauche */}
        <button
          type="button"
          className="tf-logo-btn"
          onClick={handleLogoClick}
          aria-label="Accueil formateur"
        >
          <img
            src="/img/LogoTrajetFormateur.png"
            alt="Logo Trajet Formateur"
            className="tf-logo-img"
          />
        </button>

        {/* Menus centraux (onglets formateur) */}
        <nav className="tf-nav-menu" aria-label="Navigation formateur">
          <button
            type="button"
            className="tf-nav-link"
            onClick={goProfil}
          >
            Tableaux de bord
          </button>
          <button
            type="button"
            className="tf-nav-link"
            onClick={goMissions}
          >
            Mes missions
          </button>
          <button
            type="button"
            className="tf-nav-link"
            onClick={goTrajets}
          >
            Mes trajets
          </button>
          <button
            type="button"
            className="tf-nav-link"
            onClick={goJustifs}
          >
            Mes justificatifs
          </button>
        </nav>

        {/* À droite : pastille FORMATEUR + bouton power rouge */}
        <div className="tf-nav-admin-right">
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
      </div>
    </header>
  );
}
