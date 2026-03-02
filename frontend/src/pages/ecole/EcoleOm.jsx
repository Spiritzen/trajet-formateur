// src/pages/EcoleOm.jsx
import NavBarEcole from "@/components/nav/NavBarEcole";
import EcoleBoardLeft from "@/components/ecole/EcoleBoardLeft";
import EcoleRightOm from "@/components/ecole/EcoleRightOm";
import "@/css/AdminEcole.css"; // pour le style global
import "@/css/ecole/EcoleOm.css"; // pour le layout



export default function EcoleOm() {
  return (
    <div className="ecole-om-page">
      <NavBarEcole />

      <div className="ecole-om-wrapper">
        <main className="ecole-om-grid">
          <section className="ecole-om-left">
            <EcoleBoardLeft active="om" />
          </section>

          <section className="ecole-om-right">
            {/* Ici on réutilise le style du tableau admin */}
            <EcoleRightOm />
          </section>
        </main>
      </div>
    </div>
  );
}
