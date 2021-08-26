import { writable } from "svelte/store";

export const STEP_CONNECT_WALLET = "connect-wallet";
export const STEP_ENTER_EMAIL = "enter-email";
export const STEP_CONFIRMATION = "confirm-code";
export const STEP_FINAL = "finished";

export const start = STEP_CONNECT_WALLET;
export const end = STEP_FINAL;
export const current = writable(start);

export const transition = {
  "connect-wallet": {
    nextStep: () => {
      let nextStep = STEP_ENTER_EMAIL;
      const currentStep = getRawCurrentStep();
      if (currentStep && currentStep !== STEP_CONNECT_WALLET) {
        nextStep = currentStep;
      }
      return nextStep;
    },
  },
  "enter-email": {
    nextStep: STEP_CONFIRMATION,
    previousStep: STEP_CONNECT_WALLET,
  },
  "confirm-code": {
    nextStep: STEP_FINAL,
    previousStep: STEP_ENTER_EMAIL,
  },
  finished: {
    previousStep: STEP_CONFIRMATION,
  },
};

const STORAGE_KEY = "currentStep";

export function loadCurrentStep() {
  current.set(localStorage.getItem(STORAGE_KEY) || start);
}

export function getRawCurrentStep() {
  return localStorage.getItem(STORAGE_KEY);
}

export function saveCurrentStep(step) {
  current.set(step);
  localStorage.setItem(STORAGE_KEY, step);
}
