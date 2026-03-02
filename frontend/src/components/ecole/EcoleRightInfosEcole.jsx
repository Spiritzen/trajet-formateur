// src/components/ecole/EcoleRightInfosEcole.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import EcoleProfilEditModal from "@/modal/EcoleProfilEditModal";
import EcoleEtablissementEditModal from "@/modal/EcoleEtablissementEditModal";
import EcoleResponsableAccessibiliteEditModal from "@/modal/EcoleResponsableAccessibiliteEditModal";

/**
 * Colonne de droite de la page MonEcole :
 *  - grosse carte centrale : infos établissement
 *  - carte en haut à droite : responsable "compte ECOLE" (profil utilisateur)
 *  - carte en bas à droite : responsable accessibilité
 *
 * Ce composant est le "hub" de données :
 *  - il charge le DTO global MonEtablissementResponse (/ecole/mon-etablissement)
 *  - il ouvre/ferme les modales d’édition
 *  - il relance le chargement après chaque sauvegarde
 */
export default function EcoleRightInfosEcole() {
  const [monEtab, setMonEtab] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Modales
  const [profilModalOpen, setProfilModalOpen] = useState(false);
  const [etabModalOpen, setEtabModalOpen] = useState(false);
  const [respAccModalOpen, setRespAccModalOpen] = useState(false);

  // -------------------------------------------------------
  // Chargement du DTO global "MonEtablissementResponse"
  // -------------------------------------------------------
  async function fetchMonEtablissement() {
    try {
      setLoading(true);
      setError(null);

      // BaseURL axiosClient = "/api"
      const res = await axiosClient.get("/ecole/mon-etablissement");
      setMonEtab(res.data || null);
    } catch (err) {
      console.error("Erreur chargement /ecole/mon-etablissement", err);
      setError("Impossible de charger les informations de l'établissement.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchMonEtablissement();
  }, []);

  // -------------------------------------------------------
  // Callbacks après sauvegarde depuis les modales
  // -------------------------------------------------------
  const handleAfterSave = () => {
    // On ferme toutes les modales et on recharge les données
    setProfilModalOpen(false);
    setEtabModalOpen(false);
    setRespAccModalOpen(false);
    fetchMonEtablissement();
  };

  // Si aucune donnée (encore) : on affiche un squelette simple
  if (loading && !monEtab) {
    return (
      <section className="ecole-right">
        <div className="ecole-status">Chargement des informations...</div>
      </section>
    );
  }

  if (error && !monEtab) {
    return (
      <section className="ecole-right">
        <div className="ecole-status ecole-status-error">{error}</div>
      </section>
    );
  }

  // -------- Helpers de lecture --------

  // Profil (utilisateur ECOLE)
  const profilNomComplet =
    monEtab && (monEtab.prenom || monEtab.nom)
      ? `${monEtab.prenom ?? ""} ${monEtab.nom ?? ""}`.trim()
      : "(non renseigné)";

  // Responsable accessibilité : objet imbriqué dans le DTO
  const responsable = monEtab?.responsableAccessibilite || null;

  const respAccNomComplet =
    responsable && (responsable.prenom || responsable.nom)
      ? `${responsable.prenom ?? ""} ${responsable.nom ?? ""}`.trim()
      : "(non renseigné)";

  // -------------------------------------------------------
  // Ouverture Google Maps sur la localisation de l'établissement
  // -------------------------------------------------------
  function openMaps() {
    if (!monEtab) return;

    let url;

    // Si latitude / longitude disponibles, on les utilise en priorité
    if (monEtab.lat && monEtab.lon) {
      url = `https://www.google.com/maps?q=${monEtab.lat},${monEtab.lon}`;
    } else {
      // Sinon, on reconstruit une adresse texte
      const parts = [
        monEtab.adresseL1,
        monEtab.adresseL2,
        monEtab.codePostal,
        monEtab.ville,
        monEtab.paysCode,
      ].filter(Boolean);

      const query = parts.join(" ");
      url = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(
        query
      )}`;
    }

    window.open(url, "_blank", "noopener,noreferrer");
  }

  // -------------------------------------------------------
  // Rendu
  // -------------------------------------------------------
  return (
    <section className="ecole-right">
      {/* Message d'erreur éventuel en haut */}
      {error && (
        <div className="ecole-status ecole-status-error">{error}</div>
      )}

      <div className="ecole-right-layout">
        {/* =============================== */}
        {/*     CARTE CENTRALE ETABLISSEMENT */}
        {/* =============================== */}
        <article className="ecole-card ecole-card-main">
          <header className="ecole-card-header">
            <h2 className="ecole-card-title">INFOS ECOLE</h2>

            <div className="ecole-card-header-actions">
              {/* Bouton planète : ouvrir Google Maps */}
              <button
                type="button"
                className="ecole-card-map-btn"
                title="Ouvrir la localisation dans Google Maps"
                onClick={openMaps}
              >
                🌍
              </button>

              {/* Bouton crayon : ouvrir modale édition établissement */}
              <button
                type="button"
                className="ecole-card-edit-btn"
                title="Modifier les informations de l'établissement"
                onClick={() => setEtabModalOpen(true)}
              >
                ✏️
              </button>
            </div>
          </header>

          <div className="ecole-card-body">
            {monEtab ? (
              <div className="ecole-main-infos-grid">
                <div className="ecole-main-info-row">
                  <span className="label">Nom de l&apos;école</span>
                  <span className="value">
                    {monEtab.nomEcole || "(non renseigné)"}
                  </span>
                </div>

                <div className="ecole-main-info-row">
                  <span className="label">Adresse</span>
                  <span className="value">
                    {monEtab.adresseL1 || "(non renseignée)"}{" "}
                    {monEtab.adresseL2 && <br />}
                    {monEtab.adresseL2}
                  </span>
                </div>

                <div className="ecole-main-info-row">
                  <span className="label">Ville / CP / Pays</span>
                  <span className="value">
                    {(monEtab.codePostal || "") +
                      " " +
                      (monEtab.ville || "")}{" "}
                    {monEtab.paysCode && `(${monEtab.paysCode})`}
                  </span>
                </div>

                <div className="ecole-main-info-row">
                  <span className="label">Niveau d&apos;accessibilité</span>
                  <span className="value">
                    {monEtab.niveauAccessibilite || "(non renseigné)"}
                  </span>
                </div>

                <div className="ecole-main-info-row ecole-main-info-row-full">
                  <span className="label">Infos accès / remarques</span>
                  <span className="value">
                    {monEtab.infosAcces ||
                      "(aucune information pour l'instant)"}
                  </span>
                </div>
              </div>
            ) : (
              <p>Informations indisponibles.</p>
            )}
          </div>
        </article>

        {/* =============================== */}
        {/*   CARTE RESPONSABLE ECOLE (profil) */}
        {/* =============================== */}
        <article className="ecole-card ecole-card-side">
          <header className="ecole-card-header">
            <h3 className="ecole-card-subtitle">Responsable école</h3>

            <button
              type="button"
              className="ecole-card-edit-btn"
              title="Modifier mes informations de compte"
              onClick={() => setProfilModalOpen(true)}
            >
              ✏️
            </button>
          </header>

          <div className="ecole-card-body">
            {monEtab ? (
              <ul className="ecole-side-list">
                <li>
                  <span className="label">Nom / prénom</span>
                  <br />
                  <span className="value">{profilNomComplet}</span>
                </li>
                <li>
                  <span className="label">Email de connexion</span>
                  <br />
                  <span className="value">{monEtab.email}</span>
                </li>
                <li>
                  <span className="label">Téléphone</span>
                  <br />
                  <span className="value">
                    {monEtab.telephone || "(non renseigné)"}
                  </span>
                </li>
              </ul>
            ) : (
              <p>Profil indisponible.</p>
            )}
          </div>
        </article>

        {/* ====================================== */}
        {/*  CARTE RESPONSABLE ACCESSIBILITÉ       */}
        {/* ====================================== */}
        <article className="ecole-card ecole-card-side">
          <header className="ecole-card-header">
            <h3 className="ecole-card-subtitle">
              Responsable accessibilité
            </h3>

            <button
              type="button"
              className="ecole-card-edit-btn"
              title="Créer / modifier le responsable accessibilité"
              onClick={() => setRespAccModalOpen(true)}
            >
              ✏️
            </button>
          </header>

          <div className="ecole-card-body">
            {responsable ? (
              <ul className="ecole-side-list">
                <li>
                  <span className="label">Nom / prénom</span>
                  <br />
                  <span className="value">{respAccNomComplet}</span>
                </li>
                <li>
                  <span className="label">Fonction</span>
                  <br />
                  <span className="value">
                    {responsable.fonction || "(non renseignée)"}
                  </span>
                </li>
                <li>
                  <span className="label">Téléphone</span>
                  <br />
                  <span className="value">
                    {responsable.telephone || "(non renseigné)"}
                  </span>
                </li>
                <li>
                  <span className="label">Email</span>
                  <br />
                  <span className="value">
                    {responsable.email || "(non renseigné)"}
                  </span>
                </li>
                <li>
                  <span className="label">Plage horaire</span>
                  <br />
                  <span className="value">
                    {responsable.plageHoraire ||
                      "(non renseignée pour l'instant)"}
                  </span>
                </li>
              </ul>
            ) : (
              <p className="ecole-side-empty">
                Aucun responsable accessibilité renseigné pour l&apos;instant.
                <br />
                Cliquez sur le crayon pour le créer.
              </p>
            )}
          </div>
        </article>
      </div>

      {/* ======================= */}
      {/*     MODALES D'ÉDITION   */}
      {/* ======================= */}

      {/* Profil (utilisateur ECOLE) */}
      <EcoleProfilEditModal
        isOpen={profilModalOpen}
        monEtab={monEtab}
        onClose={() => setProfilModalOpen(false)}
        onSaved={handleAfterSave}
      />

      {/* Etablissement */}
      <EcoleEtablissementEditModal
        isOpen={etabModalOpen}
        monEtab={monEtab}
        onClose={() => setEtabModalOpen(false)}
        onSaved={handleAfterSave}
      />

      {/* Responsable accessibilité */}
      <EcoleResponsableAccessibiliteEditModal
        isOpen={respAccModalOpen}
        monEtab={monEtab}
        onClose={() => setRespAccModalOpen(false)}
        onSaved={handleAfterSave}
      />
    </section>
  );
}
