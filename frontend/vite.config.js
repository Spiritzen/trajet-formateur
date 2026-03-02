import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { fileURLToPath } from "node:url";
import { dirname, resolve } from "node:path";

// Dans un fichier ESM (comme vite.config.js), __dirname n'existe pas par défaut.
// On le reconstruit à partir de l'URL du fichier courant.
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": resolve(__dirname, "src"), // => "@/..." pointe sur "src/..."
    },
  },
});
