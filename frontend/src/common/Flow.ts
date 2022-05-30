import { writable } from "svelte/store";

export const STEP_CONNECT_WALLET = "connect-wallet";
export const STEP_ENTER_EMAIL = "enter-email";
export const STEP_CONFIRMATION = "confirm-code";
export const STEP_FINAL = "finished";

export const start = STEP_ENTER_EMAIL;
export const end = STEP_FINAL;
export const current = writable(start);

export const transition = {
  "enter-email": {
    nextStep: STEP_CONFIRMATION,
    previousStep: STEP_CONNECT_WALLET,
  },
  "confirm-code": {
    nextStep: STEP_CONNECT_WALLET,
    previousStep: STEP_ENTER_EMAIL,
  },
  "connect-wallet": {
    nextStep: STEP_FINAL,
    previousStep: STEP_CONFIRMATION,
  },
  finished: {
    previousStep: STEP_CONFIRMATION,
  },
};

export function saveCurrentStep(step) {
  current.set(step);
}
