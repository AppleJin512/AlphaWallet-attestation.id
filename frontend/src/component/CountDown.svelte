<script lang="ts">
  import * as flow from "../common/Flow";
  import * as cryptoUtils from "../common/CryptoUtils";
  import { writable } from "svelte/store";
  import { currentEmail } from "../common/AppState";

  export let notBefore: Date;
  export let notAfter: Date;

  const currentTime = writable(new Date().getTime());
  setInterval(() => currentTime.set(new Date().getTime()), 1000);

  let timeLeft = 0;

  $: {
    timeLeft = Math.floor((notAfter.getTime() - $currentTime) / 1000);
    if (timeLeft <= 0) {
      flow.current.set(flow.start);
    }
  }
</script>

<div class="countdown">
  {#if $currentEmail}
    <div class="header">
      Attestation for <scan class="subject">{$currentEmail}</scan>
    </div>
  {/if}
  <div class="body">
    <div class="count subject">{timeLeft}</div>
    <div class="unit">SECONDS</div>
  </div>
  <div class="footer">
    Valid until <scan class="subject"
      >{notAfter.toString().split("GMT")[0].trim()}</scan
    >
  </div>
</div>

<style>
  .countdown {
    border: 1px solid lightgray;
    border-radius: 5px;
    margin: 20px auto;
    max-width: 396px;
  }

  .header {
    padding: 5px 0;
    font-size: 20px;
  }

  .subject {
    color: #ff5a00;
    line-height: 1.71;
  }

  .count {
    font-size: 64px;
    text-align: center;
    margin: 0;
    height: 72px;
    line-height: 72px;
  }

  .unit {
    font-size: 20px;
    padding-bottom: 5px;
  }

  .body {
    width: 80%;
    background-color: #f2f2f2;
    margin: 0px auto 15px auto;
    border-radius: 5px;
  }

  .footer {
    border-top: 1px solid lightgray;
    padding: 5px 0;
    margin: 0;
  }
</style>
