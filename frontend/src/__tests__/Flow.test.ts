import * as flow from "../common/Flow";

test("connect-wallet should have no previous Step", () => {
  expect(Object.keys(flow.transition["connect-wallet"])).not.toContain(
    "previousStep"
  );
});

test("connect-wallet should have next step to enter-email if no saved current step", () => {
  expect(flow.transition["connect-wallet"].nextStep()).toEqual("enter-email");
});

test("connect-wallet should have next step to enter-email if  saved current step is connect-wallet", () => {
  localStorage.setItem("currentStep", "connect-wallet");
  expect(flow.transition["connect-wallet"].nextStep()).toEqual("enter-email");
});

test("connect-wallet should have next step to the saved current step if  it is not connect-wallet", () => {
  localStorage.setItem("currentStep", "confirm-code");
  expect(flow.transition["connect-wallet"].nextStep()).toEqual("confirm-code");
});

test("enter-email should have correct next step and previous step", () => {
  expect(flow.transition["enter-email"].nextStep).toEqual("confirm-code");
  expect(flow.transition["enter-email"].previousStep).toEqual("connect-wallet");
});

test("confirm-code should have correct next step and previous step", () => {
  expect(flow.transition["confirm-code"].nextStep).toEqual("finished");
  expect(flow.transition["confirm-code"].previousStep).toEqual("enter-email");
});

test("finished should have correct previous step and no next step", () => {
  expect(flow.transition["finished"].previousStep).toEqual("confirm-code");
  expect(Object.keys(flow.transition["finished"])).not.toContain("nextStep");
});
