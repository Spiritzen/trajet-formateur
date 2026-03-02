// src/pages/AdminEcole.jsx
import NavBarAdmin from "@/components/admin/NavBarAdmin";
import AdminBoardLeft from "@/components/admin/AdminBoardLeft";
import AdminRightEcoles from "@/components/admin/AdminRightEcoles";
import "@/css/AdminEcole.css";

export default function AdminEcole() {
  return (
    <div className="admin-ecole-page">
      {/* Barre du haut (flottante, comme sur la page de login) */}
      <NavBarAdmin />

      {/* Wrapper centré avec max-width 1480px */}
      <div className="admin-ecole-wrapper">
        <main className="admin-ecole-grid">
          {/* Colonne gauche : menu admin */}
          <section className="admin-ecole-left">
            <AdminBoardLeft />
          </section>

          {/* Colonne droite : gestion des écoles */}
          <section className="admin-ecole-right">
            <AdminRightEcoles />
          </section>
        </main>
      </div>
    </div>
  );
}
