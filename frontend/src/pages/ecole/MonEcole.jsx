// src/pages/MonEcole.jsx
import NavBarEcole from "@/components/nav/NavBarEcole";
import EcoleBoardLeft from "@/components/ecole/EcoleBoardLeft";
import EcoleRightInfosEcole from "@/components/ecole/EcoleRightInfosEcole";

// ✅ Même CSS que l’admin
import "@/css/AdminEcole.css";
import "@/css/ecole/EcoleEtablissement.css";

export default function MonEcole() {
  return (
    <div className="admin-ecole-page ecole-mon-page">
      {/* Barre du haut, version ÉCOLE */}
      <NavBarEcole />

      <div className="admin-ecole-wrapper">
        <main className="admin-ecole-grid">
          {/* Colonne gauche : menu École */}
          <section className="admin-ecole-left">
            <EcoleBoardLeft />
          </section>

          {/* Colonne droite : infos établissement + responsables */}
          <section className="admin-ecole-right">
            <EcoleRightInfosEcole />
          </section>
        </main>
      </div>
    </div>
  );
}
