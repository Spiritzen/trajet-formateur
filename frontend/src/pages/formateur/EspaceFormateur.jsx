// src/pages/formateur/EspaceFormateur.jsx
import { useState } from "react";
import NavBarFormateur from "@/components/nav/NavBarFormateur";
import FormateurBoardLeft from "@/components/formateur/FormateurBoardLeft";
import FormateurProfilRight from "@/components/formateur/FormateurProfilRight";

// ✅ On réutilise le même CSS global que la page ÉCOLE / ADMIN
import "@/css/AdminEcole.css";

export default function EspaceFormateur() {
  // section active à droite (comme tu voulais au début)
  const [activeSection, setActiveSection] = useState("profil");

  return (
    <div className="admin-ecole-page formateur-page">
      {/* Barre du haut, version FORMATEUR */}
      <NavBarFormateur />

      {/* Wrapper centré (max-width: 1480px) */}
      <div className="admin-ecole-wrapper">
        <main className="admin-ecole-grid">
          {/* Colonne gauche : menu FORMATEUR */}
          <section className="admin-ecole-left">
            <FormateurBoardLeft
              activeSection={activeSection}
              onSelectSection={setActiveSection}
            />
          </section>

          {/* Colonne droite : profil / OM / trajets... */}
          <section className="admin-ecole-right">
            <FormateurProfilRight activeSection={activeSection} />
          </section>
        </main>
      </div>
    </div>
  );
}
