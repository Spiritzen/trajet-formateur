// src/components/ecole/EcoleBoardLeft.jsx
import { NavLink } from "react-router-dom";

// ⚙️ Items du menu École (dans l'ordre de ta maquette Photoshop)
const items = [
  { key: "etablissement", label: "ETABLISSEMENT", to: "/ecole/etablissement" },
  { key: "mes-om", label: "MES OM", to: "/ecole/om" },
  { key: "documents", label: "DOCUMENTS", to: "/ecole/documents" },
  { key: "contact", label: "CONTACT", to: "/ecole/contact" },
  { key: "compte", label: "COMPTE", to: "/ecole/compte" },
  { key: "securite", label: "SECURITE", to: "/ecole/securite" },
];

export default function EcoleBoardLeft() {
  return (
    // 💡 On réutilise *exactement* les mêmes classes que AdminBoardLeft
    <aside className="admin-board-left">
      <h2 className="admin-board-left-title">ECOLE DASH</h2>

      <nav className="admin-board-left-nav">
        {items.map((item) => (
          <NavLink
            key={item.key}
            to={item.to}
            className={({ isActive }) =>
              "admin-board-left-link" +
              (isActive ? " admin-board-left-link-active" : "")
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
