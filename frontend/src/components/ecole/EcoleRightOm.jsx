// src/components/ecole/EcoleRightOm.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import NeumoButton from "@/components/bouton/NeumoButton";
import EcoleOmCreateEditModal from "@/modal/EcoleOmCreateEditModal";
import EcoleOmAffectationModal from "@/modal/EcoleOmAffectationModal";

/**
 * Panneau droit "Mes ordres de mission" pour l'ECOLE.
 *
 * Routes backend (baseURL axiosClient = "/api") :
 *  - GET  /api/ecole/om?statut=&page=&size=
 *  - POST /api/ecole/om
 *  - PUT  /api/ecole/om/{id}
 */
export default function EcoleRightOm() {
  // ===== LISTE PAGINÉE DES OM =====
  const [items, setItems] = useState([]); // OM de la page courante
  const [page, setPage] = useState(0); // index 0-based
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  // Filtre de statut ("" = tous)
  const [statutFilter, setStatutFilter] = useState("");

  // États de chargement / erreur
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // ===== MODALE OM (create / edit) =====
  const [omModalOpen, setOmModalOpen] = useState(false);
  const [omModalMode, setOmModalMode] = useState("create"); // "create" | "edit"
  const [selectedOm, setSelectedOm] = useState(null);

  // ===== MODALE AFFECTATION FORMATEUR =====
  const [affectationModalOpen, setAffectationModalOpen] = useState(false);
  const [selectedOmForAffectation, setSelectedOmForAffectation] =
    useState(null);

  // ---------------------------------------------------------
  // Helper : formatage simple de dates (yyyy-mm-dd -> jj/mm/aaaa)
  // ---------------------------------------------------------
  const formatDate = (value) => {
    if (!value) return "—";
    try {
      const d = new Date(value);
      return d.toLocaleDateString("fr-FR");
    } catch {
      return value;
    }
  };

  // ---------------------------------------------------------
  // Chargement des OM côté école (paginé)
  // ---------------------------------------------------------
  const fetchOrdres = async (pageIndex = 0, statutOverride) => {
    try {
      setLoading(true);
      setError(null);

      const params = {
        page: pageIndex,
        size: pageSize,
      };

      const statutToUse =
        statutOverride !== undefined ? statutOverride : statutFilter;

      if (statutToUse && statutToUse.trim() !== "") {
        params.statut = statutToUse.trim();
      }

      const res = await axiosClient.get("/ecole/om", { params });
      const data = res.data || {};

      setItems(data.items || []);
      setPage(data.page ?? pageIndex);
      setTotalPages(data.totalPages ?? 1);
    } catch (err) {
      console.error("Erreur chargement ordres de mission (école)", err);
      setError("Impossible de charger la liste des ordres de mission.");
    } finally {
      setLoading(false);
    }
  };

  // Chargement initial
  useEffect(() => {
    fetchOrdres(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ---------------------------------------------------------
  // Changement de statut -> on repart en page 0 avec CE statut
  // ---------------------------------------------------------
  const handleChangeStatut = (e) => {
    const value = e.target.value;
    setStatutFilter(value);
    fetchOrdres(0, value); // <= on passe le nouveau statut
  };

  // ---------------------------------------------------------
  // Pagination
  // ---------------------------------------------------------
  const handlePrev = () => {
    if (page > 0) {
      const newPage = page - 1;
      fetchOrdres(newPage);
    }
  };

  const handleNext = () => {
    if (page < totalPages - 1) {
      const newPage = page + 1;
      fetchOrdres(newPage);
    }
  };

  // ---------------------------------------------------------
  // Ouverture modale CRÉATION d'OM
  // ---------------------------------------------------------
  const handleOpenCreateOm = () => {
    setOmModalMode("create");
    setSelectedOm(null);
    setOmModalOpen(true);
  };

  // ---------------------------------------------------------
  // Ouverture modale ÉDITION d'un OM (seulement si BROUILLON)
  // ---------------------------------------------------------
  const handleOpenEditOm = (om) => {
    if (!om) return;

    if (om.statut && om.statut.toUpperCase() !== "BROUILLON") {
      alert(
        "Seuls les ordres de mission en statut BROUILLON peuvent être modifiés."
      );
      return;
    }

    setSelectedOm(om); // 👉 on passe l'OM complet (avec coutTotalEstime)
    setOmModalMode("edit");
    setOmModalOpen(true);
  };

  // ---------------------------------------------------------
  // Ouverture modale AFFECTATION (cliquer sur la ligne ou sur l’œil)
  // ---------------------------------------------------------
  const handleOpenAffectation = (om) => {
    if (!om) return;

    // ⚠️ TRÈS IMPORTANT :
    // on passe l'objet complet "om" tel qu'il vient du backend,
    // sans le recréer à la main → coutTotalEstime reste présent.
    setSelectedOmForAffectation(om);
    setAffectationModalOpen(true);
  };

  // ---------------------------------------------------------
  // Callback après sauvegarde (create / edit / affectation)
  // ---------------------------------------------------------
  const handleAfterSave = () => {
    setOmModalOpen(false);
    setSelectedOm(null);
    setAffectationModalOpen(false);
    setSelectedOmForAffectation(null);
    // On recharge la page 0 en gardant le filtre courant
    fetchOrdres(0);
  };

  // ---------------------------------------------------------
  // Rendu
  // ---------------------------------------------------------
  return (
    <section className="admin-right-ecoles">
      {/* HEADER : titre + filtre statut + bouton "Créer un OM" */}
      <div className="admin-right-ecoles-header">
        <h2 className="admin-right-ecoles-title">MES ORDRES DE MISSION</h2>

        <div className="admin-right-ecoles-top-actions">
          {/* Filtre par statut */}
          <select
            className="admin-ecoles-search"
            value={statutFilter}
            onChange={handleChangeStatut}
          >
            <option value="">Tous les statuts</option>
            <option value="BROUILLON">Brouillon</option>
            <option value="PROPOSE">Proposé</option>
            <option value="EN_ATTENTE_VALIDATION">En attente validation</option>
            <option value="VALIDE">Validé</option>
            <option value="SIGNE">Signé</option>
            <option value="CLOTURE">Clôturé</option>
            <option value="REJETE">Rejeté</option>
          </select>

          <NeumoButton variant="success" onClick={handleOpenCreateOm}>
            + Créer un ordre de mission
          </NeumoButton>
        </div>
      </div>

      {/* TABLEAU + PAGINATION */}
      <div className="admin-right-ecoles-table-wrapper">
        {loading && <div className="admin-ecoles-status">Chargement...</div>}

        {error && <div className="admin-ecoles-status error">{error}</div>}

        {!loading && !error && (
          <>
            <table className="admin-right-ecoles-table">
              <thead>
                <tr>
                  <th>Code OM</th>
                  <th>Commentaire</th>
                  <th>Date début</th>
                  <th>Date fin</th>
                  <th>Statut</th>
                  <th>Formateur</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {items.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="admin-ecoles-empty">
                      Aucun ordre de mission pour l&apos;instant.
                    </td>
                  </tr>
                ) : (
                  items.map((om) => (
                    <tr
                      key={om.idOrdreMission}
                      className="admin-ecoles-row"
                      onClick={() => handleOpenAffectation(om)}
                    >
                      <td>{om.codeOrdre}</td>

                      {/* COMMENTAIRE SCROLLABLE */}
                      <td>
                        <div
                          className="om-comment-cell"
                          style={{
                            maxHeight: "80px",
                            overflowY: "auto",
                            paddingRight: "4px",
                          }}
                        >
                          {om.commentaire && om.commentaire.trim() !== ""
                            ? om.commentaire
                            : "—"}
                        </div>
                      </td>

                      <td>{formatDate(om.dateDebut)}</td>
                      <td>{formatDate(om.dateFin)}</td>
                      <td>{om.statut}</td>

                      <td>
                        {om.nomFormateur
                          ? om.nomFormateur
                          : om.idFormateur
                          ? `Formateur #${om.idFormateur}`
                          : "Pas encore affecté"}
                      </td>

                      <td
                        className="admin-ecoles-actions"
                        onClick={(e) => e.stopPropagation()}
                      >
                        <button
                          type="button"
                          className="icon-btn"
                          title="Voir le détail / affecter"
                          onClick={() => handleOpenAffectation(om)}
                        >
                          👁
                        </button>

                        <button
                          type="button"
                          className="icon-btn"
                          title="Modifier l'ordre de mission"
                          onClick={() => handleOpenEditOm(om)}
                        >
                          ✏️
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </>
        )}
      </div>

      {/* PAGINATION SOUS LE TABLEAU, MAIS TOUJOURS DANS LA CARTE BLANCHE */}
      <div className="admin-right-ecoles-pagination">
        <button
          className="icon-btn"
          onClick={handlePrev}
          disabled={page <= 0}
        >
          ◀
        </button>

        <span className="admin-right-ecoles-page-info">
          {totalPages > 0 ? `${page + 1} / ${totalPages}` : "0 / 0"}
        </span>

        <button
          className="icon-btn"
          onClick={handleNext}
          disabled={page >= totalPages - 1}
        >
          ▶
        </button>
      </div>

      {/* MODALE CRÉATION / ÉDITION OM */}
      <EcoleOmCreateEditModal
        isOpen={omModalOpen}
        mode={omModalMode}
        ordreMission={selectedOm}
        onClose={() => {
          setOmModalOpen(false);
          setSelectedOm(null);
        }}
        onSaved={handleAfterSave}
      />

      {/* MODALE AFFECTATION FORMATEUR */}
      <EcoleOmAffectationModal
        isOpen={affectationModalOpen}
        ordreMission={selectedOmForAffectation}
        onClose={() => {
          setAffectationModalOpen(false);
          setSelectedOmForAffectation(null);
        }}
        onSaved={handleAfterSave}
      />
    </section>
  );
}
