import { writable } from "svelte/store";
import * as cryptoUtils from "../common/CryptoUtils";

export const auth0AccessToken = writable<string>("");
export const testValidity = writable<number>(0);
export const requestEmail = writable<string>("");
export const sameEmail = writable<boolean>(false);
export const currentWallet = writable<string>("");
export const providerName = writable<string>("");

const STORAGE_KEY_CURRENT_ACCOUNT = "currentAccount";

export function getRawCurrentAccount() {
  return localStorage.getItem(STORAGE_KEY_CURRENT_ACCOUNT);
}

export function saveCurrentAccount(account: string) {
  localStorage.setItem(STORAGE_KEY_CURRENT_ACCOUNT, account);
}

const STORAGE_KEY_EMAIL = "email";

export function getRawEmail() {
  return localStorage.getItem(STORAGE_KEY_EMAIL);
}

export function saveEmail(email: string) {
  localStorage.setItem(STORAGE_KEY_EMAIL, email);
}

export async function getCurrentEmail() {
  const currentEmail = getRawEmail();
  const currentKey = getRawPair();
  if (currentEmail && currentKey) {
    try {
      return await cryptoUtils.decrypt(getRawPair().privateKey, getRawEmail());
    } catch (e) {
      console.error(e);
      return "";
    }
  } else {
    return "";
  }
}

const STORAGE_KEY_PAIR = "pair";

export function getRawPair() {
  return JSON.parse(localStorage.getItem(STORAGE_KEY_PAIR));
}

export function savePair(pair: { publicKey: string; privateKey: string }) {
  localStorage.setItem(STORAGE_KEY_PAIR, JSON.stringify(pair));
}

const STORAGE_KEY_OTP = "otp";

export function getRawOTP() {
  return localStorage.getItem(STORAGE_KEY_OTP);
}

export function saveOTP(otp: string) {
  localStorage.setItem(STORAGE_KEY_OTP, otp);
}

export function removeOTP() {
  localStorage.removeItem(STORAGE_KEY_OTP);
}

const STORAGE_KEY_ATTESTATION = "attestation";

export function getRawAttestation() {
  return JSON.parse(localStorage.getItem(STORAGE_KEY_ATTESTATION));
}

export function saveAttestation(attestation: {
  attestation: string;
  requestSecret: string;
}) {
  localStorage.setItem(STORAGE_KEY_ATTESTATION, JSON.stringify(attestation));
}

export function clearAttestation() {
  localStorage.removeItem(STORAGE_KEY_ATTESTATION);
}

export function clearAll() {
  localStorage.clear();
}
