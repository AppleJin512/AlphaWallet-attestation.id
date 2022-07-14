const preprocess = require("svelte-preprocess");

module.exports = {
  preprocess: [preprocess()],
  vitePlugin: {
    disableDependencyReinclusion: ["@roxi/routify"],
  },
};
