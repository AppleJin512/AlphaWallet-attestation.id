import { svelte } from "@sveltejs/vite-plugin-svelte";
import { defineConfig, loadEnv } from "vite";

import nodePolyfills from "rollup-plugin-polyfill-node";
const production = process.env.NODE_ENV === "production";

export default defineConfig(
  {
    server: {
      port: 5000,
    },

    plugins: [
      svelte(),
      !production &&
        nodePolyfills({
          include: [
            "node_modules/**/*.js",
            new RegExp("node_modules/.vite/.*js"),
          ],
        }),
    ],

    build: {
      target: ["esnext"],
      rollupOptions: {
        plugins: [
          // ↓ Needed for build
          nodePolyfills(),
        ],
      },
      commonjsOptions: {
        transformMixedEsModules: true,
      },
    },
  },
  ({ command, mode }) => {
    const env = loadEnv(mode, process.cwd(), "");
    return {
      define: {
        __APP_ENV__: env.APP_ENV,
      },
    };
  }
);
