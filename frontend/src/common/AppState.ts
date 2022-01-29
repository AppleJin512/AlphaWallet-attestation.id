const STORAGE_KEY_CURRENT_ACCOUNT = "currentAccount";

export function getRawCurrentAccount() {
  return localStorage.getItem(STORAGE_KEY_CURRENT_ACCOUNT);
}

export function saveCurrentAccount(account: string) {
  localStorage.setItem(STORAGE_KEY_CURRENT_ACCOUNT, account);
}

const STORAGE_KEY_EAMIL = "email";

export function getRawEmail() {
  return localStorage.getItem(STORAGE_KEY_EAMIL);
}

export function saveEmail(email: string) {
  localStorage.setItem(STORAGE_KEY_EAMIL, email);
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
