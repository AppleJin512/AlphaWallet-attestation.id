import { writable } from "svelte/store";
import * as cryptoUtils from "../common/CryptoUtils";
import { AttestationDb } from "./AttestationDb";

export const type = writable<string>("");
export const currentEmail = writable<string>("");
export const auth0AccessToken = writable<string>("");
export const isVerified = writable<boolean>(false);
export const confirmErrorMsg = writable<string>("");
export const testValidity = writable<number>(0);
export const requestEmail = writable<string>("");
export const currentWallet = writable<string>("");
export const providerName = writable<string>("");

export const attestationDb = new AttestationDb();
attestationDb.initDb();

declare let window: any;
export const keccak256 = window.keccak256;

const STORAGE_KEY_EMAIL = "email";

export async function getEmail() {
  const currentKey = getRawPair();
  if (currentKey) {
    try {
      return await cryptoUtils.decrypt(
        getRawPair().privateKey,
        localStorage.getItem(STORAGE_KEY_EMAIL)
      );
    } catch (e) {
      console.error(e);
      return "";
    }
  }
  return "";
}

export function saveEmail(email: string) {
  localStorage.setItem(STORAGE_KEY_EMAIL, email);
}

const STROAGE_KEY_TYPE = "type";

export function getType() {
  return localStorage.getItem(STROAGE_KEY_TYPE);
}

export function saveType(type: string) {
  return localStorage.setItem(STROAGE_KEY_TYPE, type);
}

const STORAGE_KEY_PAIR = "pair";

export function getRawPair() {
  return JSON.parse(localStorage.getItem(STORAGE_KEY_PAIR));
}

export function savePair(pair: { publicKey: string; privateKey: string }) {
  localStorage.setItem(STORAGE_KEY_PAIR, JSON.stringify(pair));
}

// const STORAGE_KEY_OTP = "otp";

// export function getRawOTP() {
//   return localStorage.getItem(STORAGE_KEY_OTP);
// }

// export function saveOTP(otp: string) {
//   localStorage.setItem(STORAGE_KEY_OTP, otp);
// }

// export function removeOTP() {
//   localStorage.removeItem(STORAGE_KEY_OTP);
// }

export function clearAll() {
  localStorage.clear();
}
