import { config } from "dotenv";

const { Crypto } = require("@peculiar/webcrypto");
window.crypto = new Crypto();

require("jest-localstorage-mock");
require("fake-indexeddb/auto");

window.__myapp = { env: { ...config().parsed } };
