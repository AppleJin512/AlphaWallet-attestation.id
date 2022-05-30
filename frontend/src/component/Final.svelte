<script lang="ts">
  import * as flow from "../common/Flow";

  import { getRawAttestation, sameEmail } from "../common/AppState";
  import { hexToBigint } from "bigint-conversion";
  import { parseAttestation } from "../attestation/AttesationUtils";

  import CountDown from "./CountDown.svelte";
  import { onMount } from "svelte";

  const attestation = getRawAttestation();
  let notBefore: Date;
  let notAfter: Date;

  if (!attestation) {
    flow.current.set(flow.start);
  } else {
    notBefore = parseAttestation(attestation.attestation).signedInfo.validity
      .notBefore.generalizedTime;
    notAfter = parseAttestation(attestation.attestation).signedInfo.validity
      .notAfter.generalizedTime;
  }

  onMount(async () => {
    if (window.location !== window.parent.location && $sameEmail) {
      returnAndClose();
    }
  });

  function apply() {
    flow.saveCurrentStep(flow.start);
  }

  function returnAndClose() {
    parent.postMessage(
      {
        attestation: attestation.attestation,
        requestSecret: hexToBigint(attestation.requestSecret),
        display: false,
      },
      "*"
    );
  }
</script>

{#if attestation}
  <div class="title">Success!</div>
  <CountDown {notAfter} {notBefore} />
  <div class="status">
    {#if window.location !== window.parent.location && !$sameEmail}
      <button on:click={returnAndClose}>Close</button>
    {/if}
    <div>
      Apply attestation again?
      <span on:click={apply} href="" class="apply"> Apply Now </span>
    </div>
  </div>
{/if}

<style>
  .apply {
    font-weight: 600;
    color: #007fed;
    cursor: pointer;
  }
</style>
