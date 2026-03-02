// src/components/nav/NavBarAdmin.jsx
import { useAuth } from "@/context/AuthContext";
import { useNavigate } from "react-router-dom";
import "@/css/NavBar.css";

export default function NavBarAdmin() {
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

        {/* À droite : ADMIN collé au bouton de déconnexion */}
        <div className="tf-nav-admin-right">
          <span className="tf-nav-title-badge">ADMIN</span>

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
