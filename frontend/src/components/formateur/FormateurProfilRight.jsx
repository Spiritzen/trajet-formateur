// src/components/formateur/FormateurProfilRight.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import { useAuth } from "@/context/AuthContext";
import FormateurEditionProfil from "@/modal/formateur/FormateurEditionProfil";

/**
 * Colonne de droite "Mon profil formateur".
 *
 * Route backend utilisée :
 *   GET /api/formateur/mon-compte?idUser=...
 */
export default function FormateurProfilRight() {
  const { user } = useAuth();

  const [profil, setProfil] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [editOpen, setEditOpen] = useState(false);

  // ---------------------------------------
  // Chargement du profil formateur
  // ---------------------------------------
  useEffect(() => {
    if (!user || !user.userId) {
      return;
    }

    const fetchProfil = async () => {
      try {
        setLoading(true);
        setError(null);

        const res = await axiosClient.get("/formateur/mon-compte", {
          params: { idUser: user.userId },
        });

        setProfil(res.data);
      } catch (err) {
        console.error("Erreur chargement profil formateur", err);
        setError("Impossible de charger votre profil formateur.");
      } finally {
        setLoading(false);
      }
    };

    fetchProfil();
  }, [user]);

  // ---------------------------------------
  // Ouverture Google Maps
  // ---------------------------------------
  const openMaps = () => {
    if (!profil) return;

    let url;

    if (profil.lat != null && profil.lon != null) {
      // On a des coordonnées GPS → priorité
      url = `https://www.google.com/maps?q=${profil.lat},${profil.lon}`;
    } else {
      // Sinon on reconstruit une adresse textuelle
      const parts = [
        profil.adresseL1,
        profil.adresseL2,
        profil.codePostal,
        profil.ville,
        profil.paysCode,
      ].filter(Boolean);

      if (parts.length === 0) return;

      const query = parts.join(" ");
      url = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(
        query
      )}`;
    }

    window.open(url, "_blank", "noopener,noreferrer");
  };

  // ---------------------------------------
  // Gestion modale d'édition
  // ---------------------------------------
  const handleOpenEdit = () => setEditOpen(true);
  const handleCloseEdit = () => setEditOpen(false);

  const handleProfilUpdated = (updatedProfil) => {
    setProfil(updatedProfil);
    setEditOpen(false);
  };

  // ---------------------------------------
  // Rendu
  // ---------------------------------------
  if (!user || !user.userId) {
    return (
      <section className="admin-ecole-right">
        <div className="admin-ecoles-status">
          Utilisateur non identifié. Merci de vous reconnecter.
        </div>
      </section>
    );
  }

  return (
    <section className="admin-ecole-right">
      <div className="admin-right-ecoles">
        {/* HEADER PAGE (juste le titre, sans gros pavé) */}
        <div className="admin-right-ecoles-header">
          <h2 className="admin-right-ecoles-title">Mon profil formateur</h2>
        </div>

        <div className="admin-right-ecoles-table-wrapper">
          {loading && (
            <div className="admin-ecoles-status">Chargement...</div>
          )}

          {error && (
            <div className="admin-ecoles-status error">{error}</div>
          )}

          {!loading && !error && profil && (
            <article className="ecole-card ecole-card-main">
              {/* HEADER DE LA CARTE AVEC LES DEUX PETITS BOUTONS */}
              <header className="ecole-card-header">
                <h3 className="ecole-card-title">Mes infos</h3>

                <div className="ecole-card-header-actions">
                  {/* 🌍 bouton carte */}
                  <button
                    type="button"
                    className="ecole-card-map-btn"
                    title="Ouvrir la localisation dans Google Maps"
                    onClick={openMaps}
                  >
                    🌍
                  </button>

                  {/* ✏️ bouton édition */}
                  <button
                    type="button"
                    className="ecole-card-edit-btn"
                    title="Modifier mon profil formateur"
                    onClick={handleOpenEdit}
                  >
                    ✏️
                  </button>
                </div>
              </header>

              {/* CORPS DE LA CARTE */}
              <div className="ecole-card-body">
                <div className="ecole-main-infos-grid">
                  <div className="ecole-main-info-row">
                    <span className="label">Nom / prénom</span>
                    <span className="value">
                      {profil.prenom} {profil.nom}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Email de connexion</span>
                    <span className="value">{profil.email}</span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Téléphone</span>
                    <span className="value">
                      {profil.telephone || "(non renseigné)"}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Adresse</span>
                    <span className="value">
                      {profil.adresseL1 || "(non renseignée)"}
                      {profil.adresseL2 && (
                        <>
                          <br />
                          {profil.adresseL2}
                        </>
                      )}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Ville / CP / Pays</span>
                    <span className="value">
                      {(profil.codePostal || "") + " " + (profil.ville || "")}{" "}
                      {profil.paysCode && `(${profil.paysCode})`}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Coordonnées GPS</span>
                    <span className="value">
                      {profil.lat != null && profil.lon != null
                        ? `${profil.lat}, ${profil.lon}`
                        : "Non renseignées"}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Zone kilométrique</span>
                    <span className="value">
                      {profil.zoneKm != null
                        ? `${profil.zoneKm} km`
                        : "(non renseignée)"}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Véhicule personnel</span>
                    <span className="value">
                      {profil.vehiculePerso ? "Oui" : "Non"}
                    </span>
                  </div>

                  <div className="ecole-main-info-row">
                    <span className="label">Permis de conduire</span>
                    <span className="value">
                      {profil.permis ? "Oui" : "Non"}
                    </span>
                  </div>

                  <div className="ecole-main-info-row ecole-main-info-row-full">
                    <span className="label">Commentaire</span>
                    <span className="value">
                      {profil.commentaire &&
                      profil.commentaire.trim() !== ""
                        ? profil.commentaire
                        : "Aucun commentaire particulier."}
                    </span>
                  </div>
                </div>
              </div>
            </article>
          )}

          {!loading && !error && !profil && (
            <div className="admin-ecoles-status">
              Profil formateur introuvable.
            </div>
          )}
        </div>
      </div>

      {/* Modale d'édition du profil formateur */}
      <FormateurEditionProfil
        isOpen={editOpen}
        profil={profil}
        onClose={handleCloseEdit}
        onSaved={handleProfilUpdated}
      />
    </section>
  );
}
