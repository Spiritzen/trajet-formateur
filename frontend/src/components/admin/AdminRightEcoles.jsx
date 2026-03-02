// src/components/admin/AdminRightEcoles.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import NeumoButton from "@/components/bouton/NeumoButton";
import EcoleDetailModal from "@/modal/EcoleDetailModal";
import EcoleEditModal from "@/modal/EcoleEditModal";
import EcoleCreateModal from "@/modal/EcoleCreateModal";
import EcoleDeleteModal from "@/modal/EcoleDeleteModal";

/**
 * Panneau droit "Gestion des écoles" pour l'ADMIN.
 *
 * Rôles :
 *  - charger la liste paginée des écoles via /api/admin/ecoles ;
 *  - filtrer en mémoire par code postal / ville / nom ;
 *  - afficher le tableau avec actions (voir / éditer / supprimer / localiser) ;
 *  - ouvrir :
 *      • une modale de DÉTAIL au clic sur une ligne / icône "œil" ;
 *      • une modale d'ÉDITION (établissement + référent) ;
 *      • une modale de CRÉATION d’école ;
 *      • une modale de CONFIRMATION DE SUPPRESSION.
 */
export default function AdminRightEcoles() {
  // ==== LISTE + PAGINATION ====

  const [items, setItems] = useState([]); // liste des écoles de la page courante
  const [page, setPage] = useState(0); // index de page (0-based)
  const [pageSize] = useState(10); // taille de page
  const [totalPages, setTotalPages] = useState(1);

  const [loading, setLoading] = useState(false); // état de chargement liste
  const [error, setError] = useState(null); // message d'erreur éventuel
  const [search, setSearch] = useState(""); // texte de recherche locale

  // ==== MODALE DÉTAIL ÉCOLE ====

  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [ecoleDetail, setEcoleDetail] = useState(null); // DTO détaillé
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [errorDetail, setErrorDetail] = useState(null);

  // ==== MODALE ÉDITION ÉTABLISSEMENT + RÉFÉRENT ====

  const [editModalOpen, setEditModalOpen] = useState(false);
  const [ecoleToEdit, setEcoleToEdit] = useState(null); // même DTO détaillé

  // ==== MODALE CRÉATION ÉCOLE ====

  const [createModalOpen, setCreateModalOpen] = useState(false);

  // ==== MODALE SUPPRESSION ÉCOLE ====

  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [ecoleToDelete, setEcoleToDelete] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [deleteError, setDeleteError] = useState(null);

  // ---------------------------------------------------------
  // Chargement de la liste paginée des écoles (pageIndex)
  // ---------------------------------------------------------
  const fetchEcoles = async (pageIndex) => {
    try {
      setLoading(true);
      setError(null);

      const res = await axiosClient.get("/admin/ecoles", {
        params: { page: pageIndex, size: pageSize },
      });

      setItems(res.data.items || []);
      setTotalPages(res.data.totalPages ?? 1);
      setPage(res.data.page ?? pageIndex);
    } catch (err) {
      console.error("Erreur chargement écoles", err);
      setError("Impossible de charger la liste des écoles.");
    } finally {
      setLoading(false);
    }
  };

  // Chargement initial : page 0
  useEffect(() => {
    fetchEcoles(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ---------------------------------------------------------
  // Pagination : page précédente / suivante
  // ---------------------------------------------------------
  const handlePrev = () => {
    if (page > 0) {
      const newPage = page - 1;
      fetchEcoles(newPage);
    }
  };

  const handleNext = () => {
    if (page < totalPages - 1) {
      const newPage = page + 1;
      fetchEcoles(newPage);
    }
  };

  // ---------------------------------------------------------
  // Filtrage en mémoire sur nomEcole / ville / codePostal
  // ---------------------------------------------------------
  const filteredItems = items.filter((item) => {
    const q = search.trim().toLowerCase();
    if (!q) return true;
    return (
      item.nomEcole.toLowerCase().includes(q) ||
      item.ville.toLowerCase().includes(q) ||
      item.codePostal.toLowerCase().includes(q)
    );
  });

  // ---------------------------------------------------------
  // Facteur commun pour charger un détail d'école
  // ---------------------------------------------------------
  const fetchEcoleDetail = async (idEcole) => {
    const res = await axiosClient.get(`/admin/ecoles/${idEcole}`);
    return res.data;
  };

  // ---------------------------------------------------------
  // Ouverture de la MODALE DÉTAIL pour une école donnée
  // ---------------------------------------------------------
  const handleOpenDetail = async (idEcole) => {
    if (!idEcole) return;

    setLoadingDetail(true);
    setErrorDetail(null);

    try {
      const data = await fetchEcoleDetail(idEcole);

      setEcoleDetail(data);
      setDetailModalOpen(true);

      // On s'assure que la modale d'édition et de suppression sont fermées
      setEditModalOpen(false);
      setEcoleToEdit(null);
      setDeleteModalOpen(false);
      setEcoleToDelete(null);
    } catch (error) {
      console.error("Erreur chargement détail école :", error);
      setErrorDetail("Impossible de charger les détails de l'école.");
    } finally {
      setLoadingDetail(false);
    }
  };

  // Clic sur la ligne -> demande d'ouverture détail
  const handleRowClick = (ecole) => {
    if (!ecole?.idEcole) return;
    handleOpenDetail(ecole.idEcole);
  };

  // ---------------------------------------------------------
  // Ouverture de la MODALE ÉDITION pour une école donnée
  // (depuis l'icône crayon du tableau)
  // ---------------------------------------------------------
  const handleOpenEdit = async (idEcole) => {
    if (!idEcole) return;

    setLoadingDetail(true);
    setErrorDetail(null);

    try {
      const data = await fetchEcoleDetail(idEcole);

      setEcoleToEdit(data);
      setEditModalOpen(true);

      // On ferme les autres modales
      setDetailModalOpen(false);
      setEcoleDetail(null);
      setDeleteModalOpen(false);
      setEcoleToDelete(null);
    } catch (error) {
      console.error("Erreur chargement école pour édition :", error);
      setErrorDetail("Impossible de charger l'école pour édition.");
    } finally {
      setLoadingDetail(false);
    }
  };

  // ---------------------------------------------------------
  // Ouverture de la MODALE ÉDITION depuis la modale détail
  // ---------------------------------------------------------
  const handleOpenEditFromDetail = () => {
    if (!ecoleDetail) return;
    setDetailModalOpen(false);
    setEcoleToEdit(ecoleDetail);
    setEditModalOpen(true);
  };

  // ---------------------------------------------------------
  // Callback après sauvegarde dans la modale d'édition
  // ---------------------------------------------------------
  const handleAfterSave = () => {
    // On ferme la modale d'édition
    setEditModalOpen(false);

    // On rafraîchit la liste sur la page courante
    fetchEcoles(page);

    // Optionnel : ré-ouvrir la modale de détail avec les données à jour
    if (ecoleToEdit?.idEcole) {
      handleOpenDetail(ecoleToEdit.idEcole);
    }

    // On nettoie la référence locale
    setEcoleToEdit(null);
  };

  // ---------------------------------------------------------
  // Ouverture de la MODALE CRÉATION
  // ---------------------------------------------------------
  const handleOpenCreate = () => {
    setCreateModalOpen(true);

    // On ferme les autres modales au cas où
    setDetailModalOpen(false);
    setEditModalOpen(false);
    setDeleteModalOpen(false);
    setEcoleDetail(null);
    setEcoleToEdit(null);
    setEcoleToDelete(null);
  };

  /**
   * Callback après création d'une école :
   *  - on ferme la modale de création
   *  - on recharge la page 0 (où va apparaître la nouvelle école)
   */
  const handleAfterCreate = () => {
    setCreateModalOpen(false);
    fetchEcoles(0);
  };

  // ---------------------------------------------------------
  // Ouverture de la MODALE SUPPRESSION
  // ---------------------------------------------------------
  const handleOpenDelete = (ecole) => {
    if (!ecole) return;
    setEcoleToDelete(ecole);
    setDeleteError(null);
    setDeleteModalOpen(true);

    // On s'assure que les autres modales de vue/édition sont fermées
    setDetailModalOpen(false);
    setEditModalOpen(false);
    setEcoleDetail(null);
    setEcoleToEdit(null);
  };

  // ---------------------------------------------------------
  // Confirmation de suppression depuis la modale
  // ---------------------------------------------------------
  const handleConfirmDelete = async () => {
    if (!ecoleToDelete?.idEcole) return;

    setDeleteLoading(true);
    setDeleteError(null);

    try {
      await axiosClient.delete(`/admin/ecoles/${ecoleToDelete.idEcole}`);

      // Succès : on ferme la modale et on recharge la liste
      setDeleteModalOpen(false);
      setEcoleToDelete(null);
      fetchEcoles(page);
    } catch (err) {
      console.error("Erreur lors de la suppression de l'école :", err);

      // Gestion fine du 409 (conflit / contraintes FK)
      if (err.response?.status === 409) {
        setDeleteError(
          err.response?.data?.message ||
            "Impossible de supprimer cette école car des données y sont encore rattachées (ordres de mission, trajets, signatures...)."
        );
      } else {
        setDeleteError(
          err.response?.data?.message ||
            "Une erreur est survenue lors de la suppression de l'école."
        );
      }
    } finally {
      setDeleteLoading(false);
    }
  };

  // ---------------------------------------------------------
  // Ouvrir l'école dans Google Maps
  //
  // - Si plus tard le DTO de liste contient lat/lon, on utilisera
  //   la coordonnée précise (query lat,lon).
  // - Pour l'instant, on se base sur nomEcole + CP + ville + "France".
  // ---------------------------------------------------------
  const openInMaps = (ecole) => {
    if (!ecole) return;

    // 🔍 Cas futur : si on enrichit la liste avec lat/lon
    if (ecole.lat != null && ecole.lon != null) {
      const url = `https://www.google.com/maps/search/?api=1&query=${ecole.lat},${ecole.lon}`;
      window.open(url, "_blank", "noopener,noreferrer");
      return;
    }

    // 🌍 Fallback actuel : adresse textuelle
    const parts = [];

    if (ecole.nomEcole) parts.push(ecole.nomEcole);
    if (ecole.codePostal) parts.push(ecole.codePostal);
    if (ecole.ville) parts.push(ecole.ville);

    // Hypothèse : projet en France – ajustable si besoin
    parts.push("France");

    const query = encodeURIComponent(parts.join(" "));
    const url = `https://www.google.com/maps/search/?api=1&query=${query}`;

    window.open(url, "_blank", "noopener,noreferrer");
  };

  // ---------------------------------------------------------
  // Rendu JSX
  // ---------------------------------------------------------
  return (
    <section className="admin-right-ecoles">
      {/* HEADER : titre + barre de recherche + bouton ajouter */}
      <div className="admin-right-ecoles-header">
        <h2 className="admin-right-ecoles-title">GESTION DES ECOLES</h2>

        <div className="admin-right-ecoles-top-actions">
          <input
            type="text"
            placeholder="recherche : code postal / ville / nom école"
            className="admin-ecoles-search"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <NeumoButton variant="success" onClick={handleOpenCreate}>
            + Ajouter une école
          </NeumoButton>
        </div>
      </div>

      {/* TABLEAU LISTE ECOLES */}
      <div className="admin-right-ecoles-table-wrapper">
        {loading && (
          <div className="admin-ecoles-status">Chargement...</div>
        )}

        {error && (
          <div className="admin-ecoles-status error">{error}</div>
        )}

        {!loading && !error && (
          <table className="admin-right-ecoles-table">
            <thead>
              <tr>
                <th>Nom école</th>
                <th>Ville</th>
                <th>Code postal</th>
                <th>Référent</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredItems.length === 0 ? (
                <tr>
                  <td colSpan={5} className="admin-ecoles-empty">
                    Aucune école trouvée.
                  </td>
                </tr>
              ) : (
                filteredItems.map((ecole) => (
                  <tr
                    key={ecole.idEcole}
                    className="admin-ecoles-row"
                    onClick={() => handleRowClick(ecole)}
                  >
                    <td>{ecole.nomEcole}</td>
                    <td>{ecole.ville}</td>
                    <td>{ecole.codePostal}</td>
                    <td>{ecole.nomReferent}</td>

                    <td
                      className="admin-ecoles-actions"
                      onClick={(e) => e.stopPropagation()}
                    >
                      {/* Voir = modale détail */}
                      <button
                        type="button"
                        className="icon-btn"
                        title="Voir les détails"
                        onClick={() => handleOpenDetail(ecole.idEcole)}
                      >
                        👁
                      </button>

                      {/* Modifier = modale édition directe */}
                      <button
                        type="button"
                        className="icon-btn"
                        title="Modifier"
                        onClick={() => handleOpenEdit(ecole.idEcole)}
                      >
                        ✏️
                      </button>

                      {/* Ouvrir dans Google Maps */}
                      <button
                        type="button"
                        className="icon-btn"
                        title="Ouvrir dans Google Maps"
                        onClick={() => openInMaps(ecole)}
                      >
                        🌍
                      </button>

                      {/* Supprimer = modale de confirmation */}
                      <button
                        type="button"
                        className="icon-btn icon-danger"
                        title="Supprimer"
                        onClick={() => handleOpenDelete(ecole)}
                      >
                        ✖
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>

      {/* PAGINATION */}
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

      {/* MODALE DÉTAIL */}
      <EcoleDetailModal
        isOpen={detailModalOpen}
        ecole={ecoleDetail}
        onClose={() => setDetailModalOpen(false)}
        onEdit={handleOpenEditFromDetail}
      />

      {/* MODALE ÉDITION */}
      <EcoleEditModal
        isOpen={editModalOpen}
        initialEcole={ecoleToEdit}
        onClose={() => {
          setEditModalOpen(false);
          setEcoleToEdit(null);
        }}
        onSaved={handleAfterSave}
      />

      {/* MODALE CRÉATION */}
      <EcoleCreateModal
        isOpen={createModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onCreated={handleAfterCreate}
      />

      {/* MODALE SUPPRESSION */}
      <EcoleDeleteModal
        isOpen={deleteModalOpen}
        ecole={ecoleToDelete}
        loading={deleteLoading}
        errorMessage={deleteError}
        onCancel={() => {
          setDeleteModalOpen(false);
          setEcoleToDelete(null);
          setDeleteError(null);
        }}
        onConfirm={handleConfirmDelete}
      />

      {/* Messages erreur / chargement détail (optionnels) */}
      {errorDetail && (
        <div className="admin-ecoles-status error" style={{ marginTop: "8px" }}>
          {errorDetail}
        </div>
      )}

      {loadingDetail && (detailModalOpen || editModalOpen) && (
        <div className="admin-ecoles-status" style={{ marginTop: "8px" }}>
          Chargement des données de l&apos;école...
        </div>
      )}
    </section>
  );
}
