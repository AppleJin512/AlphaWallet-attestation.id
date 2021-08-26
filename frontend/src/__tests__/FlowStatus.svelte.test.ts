import "@testing-library/jest-dom/extend-expect";

import * as flow from "../common/Flow";
import { cleanup, render } from "@testing-library/svelte";

import FlowStatus from "../component/FlowStatus.svelte";

test("should show flow status correctly", () => {
  [
    { step: flow.start, expected: "Connect" },
    { step: "enter-email", expected: "Email" },
    { step: "confirm-code", expected: "Confirm" },
    { step: "finished", expected: "Finish" },
  ].forEach((item) => {
    flow.current.set(item.step);
    const { container } = render(FlowStatus);

    expect(container.querySelectorAll(".step").length).toBe(4);
    expect(container.querySelectorAll(".active").length).toBe(1);
    expect(container.querySelector(".active").textContent).toContain(
      item.expected
    );

    cleanup();
  });
});
