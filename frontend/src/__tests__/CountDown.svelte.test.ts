import "@testing-library/jest-dom/extend-expect";

import { render } from "@testing-library/svelte";
import { get } from "svelte/store";

import * as flow from "../common/Flow";
import CountDown from "../component/CountDown.svelte";

xtest("should show correctly", () => {
  const notAfter = new Date(new Date().getTime() + 3600 * 1000);
  const { container } = render(CountDown, { notAfter });
  const textContent = container.querySelector(".countdown").textContent;

  expect(textContent).toContain(notAfter.toLocaleString());
  expect(textContent).toMatch(/\d+ seconds/i);
});

xtest("should change current to flow.start when counting down is over", () => {
  const notAfter = new Date(new Date().getTime() - 3600 * 1000);
  expect(get(flow.current)).toBe(flow.start);
  render(CountDown, { notAfter });
});
