// src/pages/AdminFormateur.jsx
import NavBarAdmin from "@/components/admin/NavBarAdmin";
import AdminBoardLeft from "@/components/admin/AdminBoardLeft";
import AdminRightFormateurs from "@/components/admin/formateur/AdminRightFormateurs";
import "@/css/AdminEcole.css"; // ✅ On réutilise le même CSS

/**
 * Page ADMIN : Gestion des formateurs.
 *
 * Layout identique à AdminEcole :
 *  - barre du haut NavBarAdmin
 *  - colonne gauche : menu AdminBoardLeft
 *  - colonne droite : panneau de gestion des formateurs
 */
export default function AdminFormateur() {
  return (
    <div className="admin-ecole-page">
      {/* Barre de navigation admin (flottante) */}
      <NavBarAdmin />

      {/* Wrapper centré */}
      <div className="admin-ecole-wrapper">
        <main className="admin-ecole-grid">
          {/* Colonne gauche : menu admin */}
          <section className="admin-ecole-left">
            <AdminBoardLeft />
          </section>

          {/* Colonne droite : gestion des formateurs */}
          <section className="admin-ecole-right">
            <AdminRightFormateurs />
          </section>
        </main>
      </div>
    </div>
  );
}
