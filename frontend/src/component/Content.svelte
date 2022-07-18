<script>
  import ConnectWallet from "../pages/connect/ConnectWallet.svelte";
  import EnterEmail from "../pages/enteremail/EnterEmail.svelte";
  import Confirmation from "../pages/confirm/Confirmation.svelte";
  import Final from "../pages/finish/Final.svelte";
  import {
    current,
    STEP_CONNECT_WALLET,
    STEP_ENTER_EMAIL,
    STEP_CONFIRMATION,
    STEP_FINAL,
  } from "../common/Flow";
</script>

{#if $current === STEP_CONNECT_WALLET}
  <ConnectWallet />
{:else if $current === STEP_ENTER_EMAIL}
  <EnterEmail />
{:else if $current === STEP_CONFIRMATION}
  <Confirmation />
{:else if $current === STEP_FINAL}
  <Final />
{/if}
