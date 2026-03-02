// src/components/nav/NavBarEcole.jsx
import { useAuth } from "@/context/AuthContext";
import { useNavigate } from "react-router-dom";
import "@/css/NavBar.css";

/**
 * Barre de navigation pour le rôle ECOLE.
 *
 * On réutilise le même style que NavBarAdmin
 * en changeant simplement le badge (couleur + libellé).
 */
export default function NavBarEcole() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  return (
    <header className="tf-navbar">
      <div className="tf-navbar-inner">
        {/* Logo à gauche */}
        <button type="button" className="tf-logo-btn">
          <img
            src="/img/LogoTrajetFormateur.png"
            alt="Logo Trajet Formateur"
            className="tf-logo-img"
          />
        </button>

        {/* Titre centré seul */}
        <div className="tf-nav-title-only">
          <span className="tf-nav-title-main">TRAJET FORMATEURS</span>
        </div>

        {/* À droite : ECOLE + bouton de déconnexion */}
        <div className="tf-nav-admin-right">
          {/* Badge vert ECOLE (classe dédiée ci-dessous) */}
          <span className="tf-nav-title-badge tf-nav-title-badge-ecole">
            ECOLE
          </span>

          <button
            type="button"
            className="tf-login-btn"
            onClick={handleLogout}
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
